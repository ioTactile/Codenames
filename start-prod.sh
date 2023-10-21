#!/bin/bash

cd backend && mvn clean install && cd ..

docker compose -f docker-compose-prod.yml down
docker compose -f docker-compose-prod.yml build
docker compose -f docker-compose-prod.yml up