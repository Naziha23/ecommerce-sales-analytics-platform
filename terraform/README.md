# Guide Terraform (Infrastructure as Code) sous WSL

Ce dossier contient l'infrastructure complète de votre application **E-Commerce Sales Analytics** provisionnée avec Terraform et le provider Docker.

---

## 📁 Fichiers inclus

| Fichier | Rôle |
| :--- | :--- |
| [`providers.tf`](file:///c:/Users/Naziha%20SASSI/OneDrive/Documents/GitHub/ecommerce-sales-analytics-platform/Projet%20e_commerce/terraform/providers.tf) | Déclaration du provider Docker (`kreuzwerker/docker`) avec configuration du socket WSL |
| [`variables.tf`](file:///c:/Users/Naziha%20SASSI/OneDrive/Documents/GitHub/ecommerce-sales-analytics-platform/Projet%20e_commerce/terraform/variables.tf) | Déclaration des variables (ports, base de données, chemins) |
| [`terraform.tfvars`](file:///c:/Users/Naziha%20SASSI/OneDrive/Documents/GitHub/ecommerce-sales-analytics-platform/Projet%20e_commerce/terraform/terraform.tfvars) | Valeurs par défaut pour tester immédiatement en local |
| [`main.tf`](file:///c:/Users/Naziha%20SASSI/OneDrive/Documents/GitHub/ecommerce-sales-analytics-platform/Projet%20e_commerce/terraform/main.tf) | Définition des ressources : Réseau, Volume, Images et Conteneurs |
| [`outputs.tf`](file:///c:/Users/Naziha%20SASSI/OneDrive/Documents/GitHub/ecommerce-sales-analytics-platform/Projet%20e_commerce/terraform/outputs.tf) | Affichage automatique des URLs d'accès à la fin du déploiement |

---

## 🛠️ Prérequis sous WSL

1. **Docker en marche** :
   * Assurez-vous que Docker Desktop tourne sous Windows avec l'intégration WSL2 activée (dans *Docker Desktop > Settings > Resources > WSL Integration > Activer votre distribution Ubuntu/WSL*).
   * Ou que le service docker tourne sous WSL : `sudo service docker status`
2. **Droits Docker sous WSL** :
   ```bash
   # Vérifiez que votre utilisateur WSL accède à docker sans sudo
   docker ps
   ```

---

## 🚀 Étapes de déploiement sous WSL

### 1. Naviguer vers le dossier dans votre terminal WSL

```bash
cd "/mnt/c/Users/Naziha SASSI/OneDrive/Documents/GitHub/ecommerce-sales-analytics-platform/Projet e_commerce/terraform"
```

### 2. Initialiser Terraform (télécharge le provider Docker)

```bash
terraform init
```

### 3. Prévisualiser le plan d'infrastructure

```bash
terraform plan
```
> Vous verrez les 3 conteneurs (`ecommerce-mysql`, `ecommerce-backend`, `ecommerce-frontend`), le réseau `ecommerce-network`, et le volume persistant `mysql-data` qui seront créés.

### 4. Appliquer et déployer l'infrastructure

```bash
terraform apply
```
> Tapez `yes` lorsque Terraform demande confirmation.

---

## 🌐 Accès aux applications

Une fois `terraform apply` terminé, Terraform affichera automatiquement les adresses :

- **Frontend Angular** : [http://localhost:4200](http://localhost:4200)
- **Backend Spring Boot** : [http://localhost:8081/api](http://localhost:8081/api)
- **Base MySQL** : `localhost:3306` (Base: `ecommerce_analytics`, User: `root`, Password: `12345`)

---

## 🧹 Détruire l'infrastructure

Pour éteindre et supprimer proprement tous les conteneurs et réseaux :

```bash
terraform destroy
```
