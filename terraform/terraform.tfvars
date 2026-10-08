# =========================================================
# Valeurs des variables Terraform pour l'environnement local WSL
# =========================================================

docker_host   = "unix:///var/run/docker.sock"
network_name  = "ecommerce-network"
db_volume_name = "mysql-data"

# Configuration Base de données
db_name       = "ecommerce_analytics"
db_user       = "root"
db_password   = "12345"
mysql_port    = 3306

# Ports des applications
backend_port  = 8081
frontend_port = 4200

# Chemins des contextes de build (adaptés depuis Projet e_commerce/terraform)
mysql_dockerfile_dir    = "../docker/MySQL"
spring_dockerfile_path  = "../docker/Spring Boot/Dockerfile"
angular_dockerfile_path = "../docker/Angular/Dockerfile"
database_init_script    = "../database/script.sql"
backend_context_dir     = "../Projet e_commerce/ecommerce"
frontend_context_dir    = "../Projet e_commerce/ecommerce-frontend"
