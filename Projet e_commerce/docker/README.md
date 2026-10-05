# Configuration Docker - E-Commerce Sales Analytics Platform

Ce dossier contient la configuration Docker pour l'ensemble des composants du projet :
- **MySQL** (Base de données relationnelle)
- **Spring Boot** (Backend API REST - Java 21)
- **Angular** (Frontend SPA - Angular 22 & Nginx)

---

## 📁 Structure du dossier `docker`

```text
docker/
├── MySQL/
│   ├── Dockerfile          # Image MySQL 8.0 avec script d'initialisation
│   └── init.sql            # Script SQL d'initialisation des tables et données
├── Spring Boot/
│   ├── Dockerfile          # Multi-stage build (Maven 3.9 + Eclipse Temurin JDK 21 -> JRE 21 Alpine)
│   └── .dockerignore       # Fichiers ignorés lors du build
├── Angular/
│   ├── Dockerfile          # Multi-stage build (Node 22 Alpine -> Nginx Alpine)
│   ├── nginx.conf          # Configuration serveur Nginx (SPA routing + reverse proxy /api/)
│   └── .dockerignore       # Fichiers ignorés lors du build
├── docker-compose.yml      # Orchestration complète des 3 services
└── README.md
```

---

## 🚀 Démarrage rapide avec Docker Compose

Depuis la racine du projet ou depuis le dossier `docker/` :

```bash
# Lancer tous les conteneurs en arrière-plan
docker compose up --build -d

# Voir les logs en temps réel
docker compose logs -f

# Arrêter les conteneurs
docker compose down
```

### Accès aux services :
- **Frontend Angular** : [http://localhost:4200](http://localhost:4200)
- **Backend Spring Boot API** : [http://localhost:8081](http://localhost:8081)
- **Base de données MySQL** : `localhost:3306` (Base: `ecommerce_analytics`, User: `root`, Password: `12345`)

---

## 📦 Build et exécution individuelle des conteneurs

### 1. MySQL
```bash
cd docker/MySQL
docker build -t ecommerce-mysql .
docker run -d --name ecommerce-mysql -p 3306:3306 ecommerce-mysql
```

### 2. Spring Boot
```bash
# Depuis la racine du projet
docker build -f "docker/Spring Boot/Dockerfile" -t ecommerce-backend ./ecommerce
docker run -d --name ecommerce-backend -p 8081:8081 --link ecommerce-mysql:mysql ecommerce-backend
```

### 3. Angular
```bash
# Depuis la racine du projet
docker build -f "docker/Angular/Dockerfile" -t ecommerce-frontend ./ecommerce-frontend
docker run -d --name ecommerce-frontend -p 4200:80 ecommerce-frontend
```
