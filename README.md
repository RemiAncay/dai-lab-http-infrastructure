## Run Dockerfile

docker build -t static-html-nginx .

docker run -d -p 8080:80 static-html-nginx