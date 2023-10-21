#!/bin/bash

cd backend && mvn clean install && cd ..

PROJECT_NAME="codenames-prod"

docker compose -p $PROJECT_NAME -f docker-compose-prod.yml down
docker compose -p $PROJECT_NAME -f docker-compose-prod.yml build
docker images | grep "<none>" | awk '{print $3}' | xargs docker rmi
docker compose -p $PROJECT_NAME -f docker-compose-prod.yml up -d