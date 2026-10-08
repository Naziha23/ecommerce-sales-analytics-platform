# =========================================================
# Valeurs de sortie (Outputs) affichées après le déploiement
# =========================================================

output "frontend_url" {
  description = "URL d'accès à l'application Angular"
  value       = "http://localhost:${var.frontend_port}"
}

output "backend_api_url" {
  description = "URL de base de l'API REST Spring Boot"
  value       = "http://localhost:${var.backend_port}/api"
}

output "mysql_host_port" {
  description = "Hôte et port pour se connecter à la base MySQL"
  value       = "localhost:${var.mysql_port}"
}

output "network_name" {
  description = "Nom du réseau Docker créé par Terraform"
  value       = docker_network.ecommerce_net.name
}

output "mysql_container_id" {
  description = "Identifiant du conteneur MySQL"
  value       = docker_container.mysql.id
}

output "backend_container_id" {
  description = "Identifiant du conteneur Spring Boot"
  value       = docker_container.backend.id
}

output "frontend_container_id" {
  description = "Identifiant du conteneur Angular"
  value       = docker_container.frontend.id
}
