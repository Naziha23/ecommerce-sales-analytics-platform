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
# 3. Images Docker (Build multi-stage automatique)
# =========================================================

# Image MySQL
resource "docker_image" "mysql" {
  name = "ecommerce-mysql:latest"
  build {
    context    = abspath("${path.module}/${var.mysql_dockerfile_dir}")
    dockerfile = "Dockerfile"
  }
}

# Image Backend Spring Boot
resource "docker_image" "backend" {
  name = "ecommerce-backend:latest"
  build {
    context    = abspath("${path.module}/${var.backend_context_dir}")
    dockerfile = abspath("${path.module}/${var.spring_dockerfile_path}")
  }
}

# Image Frontend Angular
resource "docker_image" "frontend" {
  name = "ecommerce-frontend:latest"
  build {
    context    = abspath("${path.module}/${var.frontend_context_dir}")
    dockerfile = abspath("${path.module}/${var.angular_dockerfile_path}")
  }
}

# =========================================================
# 4. Conteneur MySQL (Base de données)
# =========================================================

resource "docker_container" "mysql" {
  name    = "ecommerce-mysql"
  image   = docker_image.mysql.image_id
  restart = "always"

  networks_advanced {
    name    = docker_network.ecommerce_net.name
    aliases = ["mysql", "ecommerce-mysql"]
  }

  ports {
    internal = 3306
    external = var.mysql_port
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
# 5. Conteneur Spring Boot Backend
# =========================================================

resource "docker_container" "backend" {
  name    = "ecommerce-backend"
  image   = docker_image.backend.image_id
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

  # Dépendance explicite : attend la création du conteneur MySQL
  depends_on = [docker_container.mysql]
}

# =========================================================
# 6. Conteneur Angular Frontend (Nginx SPA + Reverse Proxy)
# =========================================================

resource "docker_container" "frontend" {
  name    = "ecommerce-frontend"
  image   = docker_image.frontend.image_id
  restart = "always"

  networks_advanced {
    name    = docker_network.ecommerce_net.name
    aliases = ["frontend", "ecommerce-frontend"]
  }

  ports {
    internal = 80
    external = var.frontend_port
  }

  # Dépendance explicite : attend le backend
  depends_on = [docker_container.backend]
}
