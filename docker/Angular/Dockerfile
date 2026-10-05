# =========================================================
# Stage 1: Build the Angular Application
# =========================================================
FROM node:22-alpine AS build

WORKDIR /app

# Cache dependencies
COPY package*.json ./
RUN npm ci --legacy-peer-deps

# Copy project files and compile production build
COPY . .
RUN npm run build -- --configuration production

# =========================================================
# Stage 2: Production Nginx Server
# =========================================================
FROM nginx:alpine

# Remove default nginx config
RUN rm -rf /etc/nginx/conf.d/*

# Configure Nginx for Angular Single Page Application and API reverse proxy
RUN printf 'server {\n\
    listen 80;\n\
    server_name localhost;\n\
\n\
    root /usr/share/nginx/html;\n\
    index index.csr.html index.html;\n\
\n\
    gzip on;\n\
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml image/svg+xml;\n\
\n\
    location / {\n\
        try_files $uri $uri/ $uri/index.html /index.csr.html /index.html =404;\n\
    }\n\
\n\
    location /api/ {\n\
        proxy_pass http://backend:8081/api/;\n\
        proxy_set_header Host $host;\n\
        proxy_set_header X-Real-IP $remote_addr;\n\
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n\
    }\n\
\n\
    error_page 500 502 503 504 /50x.html;\n\
    location = /50x.html {\n\
        root /usr/share/nginx/html;\n\
    }\n\
}\n' > /etc/nginx/conf.d/default.conf

# Copy compiled Angular browser assets
COPY --from=build /app/dist/ecommerce-frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
