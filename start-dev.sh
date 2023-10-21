#!/bin/bash

docker compose -f docker-compose-dev.yml down
docker compose -f docker-compose-dev.yml build
docker images | grep "<none>" | awk '{print $3}' | xargs docker rmi
docker compose -f docker-compose-dev.yml up -d