#!/bin/bash

PROJECT_NAME="codenames"

docker compose -p $PROJECT_NAME -f docker-compose-dev.yml down
docker compose -p $PROJECT_NAME -f docker-compose-dev.yml build
docker images | grep "<none>" | awk '{print $3}' | xargs docker rmi
docker compose -p $PROJECT_NAME -f docker-compose-dev.yml up -d