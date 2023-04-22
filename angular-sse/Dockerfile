# build the application
FROM node:latest AS build-stage
WORKDIR /app
RUN npm i -g pnpm
COPY ./ .
RUN pnpm install
RUN pnpm run build --configuration=production
# run the application
FROM nginx:latest
COPY --from=build-stage /app/dist/angular-sse/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
