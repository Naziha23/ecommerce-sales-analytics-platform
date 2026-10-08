terraform {
  required_version = ">= 1.5.0"
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.2"
    }
  }
}

provider "docker" {
  # Sous WSL / Linux, utilise par défaut le socket Unix standard : unix:///var/run/docker.sock
  # Peut être configuré via la variable docker_host
  host = var.docker_host
}
