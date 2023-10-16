FROM node:18.18.0 as build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build
FROM nginx:latest
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80