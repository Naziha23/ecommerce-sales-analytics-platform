FROM mysql:8.0

# Environment variables
ENV MYSQL_ROOT_PASSWORD=12345 \
    MYSQL_DATABASE=ecommerce_analytics

# Copy the initialization SQL script
COPY ./init.sql /docker-entrypoint-initdb.d/01-init.sql

EXPOSE 3306

CMD ["mysqld"]
