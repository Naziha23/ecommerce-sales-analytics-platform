# =========================================================
# Variables d'environnement & Connexion Docker
# =========================================================

variable "docker_host" {
  description = "Point d'accès au démon Docker (socket Unix par défaut sous WSL/Linux)"
  type        = string
  default     = "unix:///var/run/docker.sock"
}

# =========================================================
# Paramètres Réseau & Volumes
# =========================================================

variable "network_name" {
  description = "Nom du réseau Docker partagé entre les conteneurs"
  type        = string
  default     = "ecommerce-network"
}

variable "db_volume_name" {
  description = "Nom du volume Docker pour la persistance des données MySQL"
  type        = string
  default     = "mysql-data"
}

# =========================================================
# Base de données MySQL
# =========================================================

variable "db_name" {
  description = "Nom de la base de données relationnelle"
  type        = string
  default     = "ecommerce_analytics"
}

variable "db_user" {
  description = "Utilisateur administrateur de la base"
  type        = string
  default     = "root"
}

variable "db_password" {
  description = "Mot de passe de la base de données"
  type        = string
  default     = "12345"
  sensitive   = true
}

variable "mysql_port" {
  description = "Port externe exposé pour MySQL"
  type        = number
  default     = 3306
}

# =========================================================
# Backend Spring Boot
# =========================================================

variable "backend_port" {
  description = "Port externe exposé pour le backend Spring Boot"
  type        = number
  default     = 8081
}

# =========================================================
# Frontend Angular
# =========================================================

variable "frontend_port" {
  description = "Port externe exposé pour l'application frontend Angular"
  type        = number
  default     = 4200
}

# =========================================================
# Chemins relatifs vers les projets et Dockerfiles
# =========================================================

variable "mysql_dockerfile_dir" {
  description = "Chemin vers le dossier contenant le Dockerfile MySQL"
  type        = string
  default     = "../docker/MySQL"
}

variable "spring_dockerfile_path" {
  description = "Chemin vers le Dockerfile Spring Boot"
  type        = string
  default     = "../docker/Spring Boot/Dockerfile"
}

variable "angular_dockerfile_path" {
  description = "Chemin vers le Dockerfile Angular"
  type        = string
  default     = "../docker/Angular/Dockerfile"
}

variable "database_init_script" {
  description = "Chemin vers le script SQL d'initialisation"
  type        = string
  default     = "../database/script.sql"
}

variable "backend_context_dir" {
  description = "Chemin vers le code source backend pour le build Docker"
  type        = string
  default     = "../Projet e_commerce/ecommerce"
}

variable "frontend_context_dir" {
  description = "Chemin vers le code source frontend pour le build Docker"
  type        = string
  default     = "../Projet e_commerce/ecommerce-frontend"
}
