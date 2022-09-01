FROM node:16-alpine as node

WORKDIR /app
COPY . .
RUN npm install --silent && npm run ng build

FROM nginx:alpine

VOLUME /var/cache/nginx
COPY --from=node app/www /usr/share/nginx/html
COPY --from=node app/nginx-docker-config/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
