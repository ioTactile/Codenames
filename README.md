# Codenames

This project is a replica of the Codenames game web application, primarily designed to facilitate my learning of Java, with a particular emphasis on backend development. The frontend is constructed using Vue.js with Vue Router and Pinia. The entire application is composed of Java Spring Boot for the backend and Vue.js for the frontend, and it operates within a Docker container.

## Todo list / Roadmap (I'm giving myself 2 months to deliver a basic version)

- [x] Learn the Java language **(started september 2023)**
- [x] Learn the Spring Boot framework
- [x] Create models for the database table "Room", which contains (Room, Player, Word, Clue)
- [x] Create a repository for the "Room" table
- [x] Create services (all the methods that will be used in the controllers)
- [x] Create REST API controllers for the "Room" table
- [x] Create a PostgreSQL database in a Docker container
- [x] Test the API with Postman
- [x] Write unit tests for all the methods in the Room service
- [x] Create a WebSocket configuration file
- [ ] Create custom exceptions for the API
- [x] Create a vue.js frontend **(started 1 october 2023)**
- [x] Create routes
- [x] Create views
- [x] Create components
- [x] Create a `api.ts` utils file
- [x] Create a `user.ts` file and `websocket.ts` file / pinia store
- [x] Implement responsive design
- [ ] Unit tests, e2e tests (vitest and playwright)
- [x] Create Dockerfiles for the backend and frontend **(16 october 2023)**
- [x] Create `docker-compose-dev.yml` and `docker-compose-prod.yml` files
- [x] Create shell scripts for the Docker Compose files
- [x] Verify that the shell script for the development environment is working **(21 october 2023)**
- [ ] Running a nginx container for production (reverse proxy)
- [ ] Create a CI/CD pipeline with GitHub Actions
- [ ] Create a Kubernetes cluster with Terraform
- [ ] Deploy the application on the Kubernetes cluster
- [ ] Create a sub-domain "codenames.iotactile.com" and obtain an SSL certificate
- [ ] Deploy the application

## Development setup / use shell script (recommended)

### Frontend

- docker build -t codenames-front-image .
- docker run --env-file ./.env.develpment -v ${PWD}/src:/app/frontend/src:ro -d -p 5173:5173 --name codenames-frontend codenames-front-image

### Backend

- docker build -t codenames-back-image .
- docker run --env-file ./.env.develpment -v ${PWD}/src:/app/backend/src:ro -d -p 8080:8080 --name codenames-backend codenames-back-image

### Docker-compose

- docker compose -f docker-compose-dev.yml up -d

### Shell script

- bash start-dev.sh

## Production setup / use shell script (recommended)

### Frontend

- docker build -t codenames-front-image-prod -f Dockerfile.prod .
- docker run --env-file ./.env.production -d -p 80:80 --name codenames-frontend-prod codenames-front-image-prod

### Backend

- docker build -t codenames-back-image-prod -f Dockerfile.prod .
- docker run --env-file ./.env.production -d -p 8080:8080 --name codenames-backend-prod codenames-back-image-prod

### Docker-compose

- docker compose -f docker-compose-prod.yml up -d

### Shell script

- bash start-prod.sh
