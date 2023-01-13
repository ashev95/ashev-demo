#!/bin/bash

cd /usr/local/docker

sudo docker compose -f docker-compose.prod.yaml down

#sub_cmd=$(echo " --build-arg ")$(sed ':a; N; $!ba; s/\n/ --build-arg /g' "/usr/local/docker/.env" )
sub_cmd=`python3 build-args.py "/usr/local/docker/.env"`
docker_compose_cmd_string="docker compose -f docker-compose.prod.yaml build $sub_cmd --no-cache"
#eval $docker_compose_cmd_string
sudo -s eval $docker_compose_cmd_string

sudo docker compose -f docker-compose.prod.yaml --env-file=/usr/local/docker/.env up -d