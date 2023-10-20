# Codenames

## DEV Setup

### Frontend

- docker build -t codenames-front-image .
- docker run --env-file ./.env.develpment -v ${PWD}/src:/app/frontend/src:ro -d -p 5173:5173 --name codenames-frontend codenames-front-image

### Backend

- docker build -t codenames-back-image .
- docker run --env-file ./.env.develpment -v ${PWD}/src:/app/backend/src:ro -d -p 8080:8080 --name codenames-backend codenames-back-image

### Database

- docker-compose -f docker-compose-dev.yml up -d

## PROD Setup

### Frontend

- docker build -t codenames-front-image-prod -f Dockerfile.prod .
- docker run --env-file ./.env.production -d -p 80:80 --name codenames-frontend-prod codenames-front-image-prod

### Backend

- docker build -t codenames-back-image-prod -f Dockerfile.prod .
- docker run --env-file ./.env.production -d -p 8080:8080 --name codenames-backend-prod codenames-back-image-prod

# Docker-compose

- docker-compose -f docker-compose-dev.yml up -d
- docker-compose -f docker-compose-prod.yml up -d
