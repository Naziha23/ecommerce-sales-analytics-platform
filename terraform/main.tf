# =========================================================
# 1. Réseau Docker partagé
# =========================================================

resource "docker_network" "ecommerce_net" {
  name   = var.network_name
  driver = "bridge"
}

# =========================================================
# 2. Volume Docker pour la persistance MySQL
# =========================================================

resource "docker_volume" "mysql_data" {
  name = var.db_volume_name
}

# =========================================================
# 3. Conteneur MySQL (Base de données)
# NOTE: L'image doit être buildée au préalable :
#   docker build -t ecommerce-mysql:latest ./docker/MySQL
# =========================================================

resource "docker_container" "mysql" {
  name    = "ecommerce-mysql"
  image   = "ecommerce-mysql:latest"
  restart = "always"

  networks_advanced {
    name    = docker_network.ecommerce_net.name
    aliases = ["mysql", "ecommerce-mysql"]
  }

  ports {
    internal = 3306
    external = 3307
  }

  env = [
    "MYSQL_ROOT_PASSWORD=${var.db_password}",
    "MYSQL_DATABASE=${var.db_name}"
  ]

  # Persistance des données MySQL
  volumes {
    volume_name    = docker_volume.mysql_data.name
    container_path = "/var/lib/mysql"
  }

  # Script SQL d'initialisation
  volumes {
    host_path      = abspath("${path.module}/${var.database_init_script}")
    container_path = "/docker-entrypoint-initdb.d/01-init.sql"
    read_only      = true
  }
}

# =========================================================
# 4. Conteneur Spring Boot Backend
# NOTE: L'image doit être buildée au préalable :
#   docker build -t ecommerce-backend:latest -f "docker/Spring Boot/Dockerfile" "./Projet e_commerce/ecommerce"
# =========================================================

resource "docker_container" "backend" {
  name    = "ecommerce-backend"
  image   = "ecommerce-backend:latest"
  restart = "always"

  networks_advanced {
    name    = docker_network.ecommerce_net.name
    aliases = ["backend", "ecommerce-backend"]
  }

  ports {
    internal = 8081
    external = var.backend_port
  }

  env = [
    "SERVER_PORT=8081",
    "SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/${var.db_name}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true",
    "SPRING_DATASOURCE_USERNAME=${var.db_user}",
    "SPRING_DATASOURCE_PASSWORD=${var.db_password}",
    "SPRING_JPA_HIBERNATE_DDL_AUTO=update"
  ]

  depends_on = [docker_container.mysql]
}

# =========================================================
# 5. Conteneur Angular Frontend (Nginx SPA + Reverse Proxy)
# NOTE: L'image doit être buildée au préalable :
#   docker build -t ecommerce-frontend:latest -f "docker/Angular/Dockerfile" "./Projet e_commerce/ecommerce-frontend"
# =========================================================

resource "docker_container" "frontend" {
  name    = "ecommerce-frontend"
  image   = "ecommerce-frontend:latest"
  restart = "always"

  networks_advanced {
    name    = docker_network.ecommerce_net.name
    aliases = ["frontend", "ecommerce-frontend"]
  }

  ports {
    internal = 80
    external = var.frontend_port
  }

  depends_on = [docker_container.backend]
}
