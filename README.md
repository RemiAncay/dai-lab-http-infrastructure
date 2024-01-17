# DAI HTTP Infrastructure report
Rémi Ançay & Lucas Charbonnier

## Step 1
We created a Dockerfile that copies (1) the html files for the static website and (2) the nginx.conf file.

We retrieved the nginx.conf file from the image using the docker command :
```docker cp <container_name>:/etc/nginx/nginx.conf nginx.conf```

Here are the docker commands we used to build and run the container :
- `docker build -t static-html-nginx .`
- `docker run -d -p 8080:80 static-html-nginx`

## Step 2

We created a simple `docker-compose.yml` file that builds our `static-html-nginx` image and binds the web server's port `80` on the port `8080` of the host.

We can build our infrastructure using `docker compose build`, start it using : `docker compose up` and stop it using `docker compose down` from a terminal opened in the root directory.

## Step 3

### API Dockerfile
We created an API using the Javalin framework. The API is a simple CRUD API that manages a database of foosball players and matches.
All the spec are documented in the [server-side-api-specs.md](server-side-api-specs.md) file.

### Dependencies
If you want to run the API localy in your IDE, you will need to add the following dependencies to your project :
````
io.javalin:javalin:5.6.3
org.slf4j:slf4j-simple:2.0.7
com.fasterxml.jackson.core:jackson-databind:2.15.1
````

### Dockerfile
We also created a Dockerfile that builds the API .jar in an image and runs it in a Docker container.
The Dockerfile downloads the `alpine:latest` image, copies the `.jar` from our `target/` folder in the image and runs it using `java -jar app.jar`. 

#### Important
In order for the image to run, the API has to be built before running the image, using the `mvn clean package` command.

### Docker-compose
We improve our docker-compose file to run both the API and the static web server.

## Step 4
We improve our docker-compose file with traefik to manage the routing between the API and the static web server.
The static-server service and the api service have to be modified to use the traefik labels.

The traefik dashboard is also available at `http://localhost:8080/dashboard/`.

We also had to change the routes in our API with the `/api` prefix to make it work with traefik.
The API is now available at `http://localhost/api/...` and the static web server at `http://localhost/` on the port 80.

Here is the docker-compose file at this point:
```yaml
version: '3'

services:
  static-server:
    image: static-html-nginx
    build: ./Static-server
    labels:
      - "traefik.enable=true"
      # Redirige les requêtes http
      - "traefik.http.routers.static-server.rule=Host(`localhost`)"
      - "traefik.http.routers.static-server.entrypoints=web"

  foosball-api:
    image: foosball-api
    build: ./foosball-api
    labels:
      - "traefik.enable=true"
      # Redirige les requêtes http pour l'API
      - "traefik.http.routers.foosball-api.rule=Host(`localhost`) && PathPrefix(`/api`)"
      - "traefik.http.routers.foosball-api.entrypoints=web"
      - "traefik.http.services.foosball-api.loadbalancer.server.port=7000"

  reverse-proxy:
    image: traefik
    command:
      - "--api.dashboard=true"
      - "--api.insecure=true"
      - "--providers.docker=true"
      - "--entrypoints.web.address=:80"
    ports:
      - "80:80"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

## Step 5

Traefik will by default detect if multiple instances of a service are running. In order to run multiple instances of the same service, you need to set the "replicas" tag the docker-compose to the number of your choice :

```
static-server:
  deploy:
    replicas: 3
```

You can also specify the number of instances of each service when running the `--scale` parameter in the `docker compose up` command :

```docker compose -d --scale static-server=3```

In order to add another server while the container is running, you can just run that command again with a different `--scale` parameter. This way, the container doesn't need to be stopped to add new instances.

The load balancing between the running instances is done automatically by Traefik. When we try to access our website, docker prints the HTTP request in the console and we can see that it's a **round-robin** between the running instances. 

## Step 6

Our API does not use a database, the data is all stored in the memory. This means that every instance of the API only has a copy of the data but there is no way for the instances to synchronise the data. If we don't use sticky-sessions, we'll encounter problems while using the API. For example, if we add a new foosball player through the API and then we try to get the list of existing players, we may not get the right results, depending on which server our request arrives.

After enabling sticky sessions and testing using Bruno, we can see that we will always end up on the same server so the data stored on the server will stay coherent between our requests. However, this would not necessarily be the case if we closed the connection and try to access the API again. To do things correctly, we should be using another database server which contains all of our data to make sure that, no matter the server on which we operate, the data always comes from the same source.

```
  foosball-api:
    image: foosball-api
    build: ./foosball-api
    labels:
      - "traefik.enable=true"
      # Active la sticky-session
      - "traefik.http.services.foosball-api.loadbalancer.sticky.cookie=true"
      - "traefik.http.services.foosball-api.loadbalancer.sticky.cookie.name=my-sticky-cookie"
```

## Step 7

### Certificates config in docker compose

First, we created 2 files: a key and a certificate (using the SSL documentation linked in Tutorial.md). We then put these 2 files in a folder called `certs`. And then added this folder in the docker compose config file :

```
  reverse-proxy:
    image: traefik
    ports:
      - "80:80"
      - "443:443"
      - "8080:8080"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./certs:/etc/traefik/certificates
      - ./config/traefik.yaml:/etc/traefik/traefik.yaml
```

Next, we had to create a traefik.yaml file to configure http and https. This also allowed use to move some of the labels from the docker-compose.yaml file to traefik.yaml :

**traefik.yaml**
```
# Enable Docker as a provider
providers:
  docker:
    endpoint: "unix:///var/run/docker.sock"
    exposedByDefault: false

# Define entrypoints for http and https
entryPoints:
  http:
    address: ":80"
  https:
    address: ":443"

# Enable TLS configuration
tls:
  certificates:
  - certFile: "/etc/traefik/certificates/cert.pem"
    keyFile: "/etc/traefik/certificates/key.pem"

# Enable API dashboard
api:
  insecure: true
  dashboard: true

```

### Portainer

We added portainer to our docker-compose config to manage and monitor our containers : 

```
  portainer:
    image: portainer/portainer-ce
    ports:
      - "9000:9000"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - portainer_data:/data
    restart: always
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.portainer.rule=Host(`portainer.localhost`)"
      - "traefik.http.routers.portainer.entrypoints=https"
      - "traefik.http.routers.portainer.tls=true"
      - "traefik.http.routers.portainer-http.entrypoints=http"
```



