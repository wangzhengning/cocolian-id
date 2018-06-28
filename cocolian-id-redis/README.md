### uuid
docker build -t centos7/redis .

docker run --name redis -d --privileged  -p 6379:6379 centos7/redis