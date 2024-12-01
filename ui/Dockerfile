FROM node:22-alpine AS build
WORKDIR /build
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build
RUN ls /build

FROM nginx:latest
WORKDIR /app
USER root
COPY --from=build /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx
RUN chown -R nginx:nginx /usr/share/nginx/html /var/log/nginx /var/cache/nginx
CMD ["nginx", "-g", "daemon off;"]