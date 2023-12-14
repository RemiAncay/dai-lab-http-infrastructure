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

We can build our infrastructure using `docker compose build` and start it using : `docker compose up` and stop it using `docker compose down` from a terminal opened in the root directory.

## Step 3
todo
## Step 4
todo
## Step 5
todo
## Step 6
todo
## Step 7
todo
