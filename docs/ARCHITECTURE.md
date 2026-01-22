# Architecture MVC du Projet Taxi Brousse

## Structure du Projet

Le projet suit une architecture MVC (Model-View-Controller) bien structurée avec une séparation claire des responsabilités :

```
src/main/java/com/taxi_brousse/
├── controller/          # Contrôleurs REST API
├── service/             # Logique métier
├── repository/          # Accès aux données (DAO)
├── entity/              # Modèles de données (JPA Entities)
├── dto/                 # Data Transfer Objects
├── mapper/              # Conversion Entity <-> DTO
├── exception/           # Exceptions personnalisées
└── TaxiBrousseApplication.java
```

## Couches de l'Application

### 1. **Entity (Model)**
**Package:** `com.taxi_brousse.entity`

Les entités JPA qui représentent les tables de la base de données :
- `Vehicule` - Informations sur les véhicules
- `Cooperative` - Informations sur les coopératives
- `Chauffeur` - Informations sur les chauffeurs
- `Client` - Informations sur les clients
- `DepenseVehicule` - Dépenses liées aux véhicules
- `Trajet`, `Depart`, `Reservation`, etc.

**Sous-package:** `entity.reference` - Tables de référence (types, statuts, etc.)

### 2. **Repository (Data Access Layer)**
**Package:** `com.taxi_brousse.repository`

Interfaces Spring Data JPA pour l'accès aux données :
- `VehiculeRepository`
- `CooperativeRepository`
- `ChauffeurRepository`
- `ClientRepository`
- `DepenseVehiculeRepository`
- `RefTypeDepenseRepository`

**Fonctionnalités:**
- CRUD de base (héritées de JpaRepository)
- Requêtes personnalisées avec @Query
- Méthodes de recherche par critères

### 3. **DTO (Data Transfer Objects)**
**Package:** `com.taxi_brousse.dto`

Objets pour transférer les données entre les couches :
- `VehiculeDTO`
- `CooperativeDTO`
- `ChauffeurDTO`
- `ClientDTO`
- `DepenseVehiculeDTO`

**Avantages:**
- Validation des données avec annotations Jakarta Validation
- Séparation entre modèle de domaine et API
- Contrôle des données exposées

### 4. **Mapper**
**Package:** `com.taxi_brousse.mapper`

Convertisseurs entre Entities et DTOs :
- `VehiculeMapper`
- `CooperativeMapper`
- `ChauffeurMapper`
- `ClientMapper`
- `DepenseVehiculeMapper`

**Méthodes:**
- `toDTO(Entity)` - Entity → DTO
- `toEntity(DTO)` - DTO → Entity

### 5. **Service (Business Logic Layer)**
**Package:** `com.taxi_brousse.service`

Contient la logique métier de l'application :
- `VehiculeService`
- `CooperativeService`
- `ChauffeurService`
- `ClientService`
- `DepenseVehiculeService`

**Responsabilités:**
- Validation métier
- Orchestration des opérations
- Gestion des transactions (@Transactional)
- Logique complexe

### 6. **Controller (Presentation Layer)**
**Package:** `com.taxi_brousse.controller`

Contrôleurs REST API :
- `VehiculeController` - `/api/vehicules`
- `CooperativeController` - `/api/cooperatives`
- `ChauffeurController` - `/api/chauffeurs`
- `ClientController` - `/api/clients`
- `DepenseVehiculeController` - `/api/depenses-vehicule`

**Endpoints REST:**
- `GET /api/{resource}` - Liste tous
- `GET /api/{resource}/{id}` - Récupère par ID
- `POST /api/{resource}` - Créer
- `PUT /api/{resource}/{id}` - Modifier
- `DELETE /api/{resource}/{id}` - Supprimer

### 7. **Exception**
**Package:** `com.taxi_brousse.exception`

Gestion centralisée des erreurs :
- `ResourceNotFoundException` - Ressource non trouvée (404)
- `BadRequestException` - Requête invalide (400)
- `GlobalExceptionHandler` - Gestionnaire global (@RestControllerAdvice)

## Flux de Requête

```
Client HTTP Request
      ↓
[Controller] - Réception de la requête
      ↓
[Validation] - Validation des données (DTO + @Valid)
      ↓
[Service] - Logique métier
      ↓
[Repository] - Accès à la base de données
      ↓
[Entity] - Objets JPA
      ↓
[Mapper] - Conversion Entity → DTO
      ↓
[Controller] - Envoi de la réponse
      ↓
Client HTTP Response
```

## Exemple d'Utilisation

### Créer une Dépense de Véhicule

**Request:**
```http
POST /api/depenses-vehicule
Content-Type: application/json

{
  "vehiculeId": 1,
  "cooperativeId": 1,
  "refTypeDepenseId": 1,
  "refDeviseId": 1,
  "montant": 150000,
  "dateDepense": "2026-01-09",
  "description": "Carburant pour trajet Antananarivo-Toamasina",
  "numeroPiece": "FACT-2026-001"
}
```

**Response:**
```json
{
  "id": 1,
  "vehiculeId": 1,
  "vehiculeImmatriculation": "1234 TBA",
  "cooperativeId": 1,
  "cooperativeNom": "Cooperative Fiadanana",
  "refTypeDepenseId": 1,
  "refTypeDepenseLibelle": "Carburant",
  "refDeviseId": 1,
  "refDeviseCode": "MGA",
  "montant": 150000.00,
  "dateDepense": "2026-01-09",
  "description": "Carburant pour trajet Antananarivo-Toamasina",
  "numeroPiece": "FACT-2026-001",
  "createdAt": "2026-01-09T10:30:00"
}
```

## Technologies Utilisées

- **Spring Boot 4.0.1** - Framework principal
- **Spring Data JPA** - ORM et accès aux données
- **PostgreSQL** - Base de données
- **Lombok** - Réduction du code boilerplate
- **Jakarta Validation** - Validation des données
- **Jackson** - Sérialisation JSON

## Bonnes Pratiques Implémentées

1. **Séparation des responsabilités** - Chaque couche a un rôle spécifique
2. **DTOs** - Isolation du modèle de domaine
3. **Validation** - Validation côté API avec annotations
4. **Gestion des erreurs** - Gestionnaire centralisé des exceptions
5. **Transactions** - Gestion automatique avec @Transactional
6. **RESTful API** - Endpoints cohérents et standardisés
7. **Injection de dépendances** - Utilisation de @RequiredArgsConstructor (Lombok)
8. **Immutabilité** - DTOs avec validation

## Endpoints API Disponibles

### Véhicules
- `GET /api/vehicules` - Liste tous les véhicules
- `GET /api/vehicules/{id}` - Récupère un véhicule
- `POST /api/vehicules` - Créer un véhicule
- `PUT /api/vehicules/{id}` - Modifier un véhicule
- `DELETE /api/vehicules/{id}` - Supprimer un véhicule

### Coopératives
- `GET /api/cooperatives` - Liste toutes les coopératives
- `GET /api/cooperatives/{id}` - Récupère une coopérative
- `POST /api/cooperatives` - Créer une coopérative
- `PUT /api/cooperatives/{id}` - Modifier une coopérative
- `DELETE /api/cooperatives/{id}` - Supprimer une coopérative

### Dépenses Véhicule
- `GET /api/depenses-vehicule` - Liste toutes les dépenses
- `GET /api/depenses-vehicule/{id}` - Récupère une dépense
- `GET /api/depenses-vehicule/vehicule/{vehiculeId}` - Dépenses par véhicule
- `GET /api/depenses-vehicule/cooperative/{cooperativeId}` - Dépenses par coopérative
- `GET /api/depenses-vehicule/periode?dateDebut={date}&dateFin={date}` - Dépenses par période
- `GET /api/depenses-vehicule/vehicule/{vehiculeId}/periode?dateDebut={date}&dateFin={date}` - Dépenses par véhicule et période
- `POST /api/depenses-vehicule` - Créer une dépense
- `PUT /api/depenses-vehicule/{id}` - Modifier une dépense
- `DELETE /api/depenses-vehicule/{id}` - Supprimer une dépense

### Chauffeurs
- `GET /api/chauffeurs` - Liste tous les chauffeurs
- `GET /api/chauffeurs/{id}` - Récupère un chauffeur
- `POST /api/chauffeurs` - Créer un chauffeur
- `PUT /api/chauffeurs/{id}` - Modifier un chauffeur
- `DELETE /api/chauffeurs/{id}` - Supprimer un chauffeur

### Clients
- `GET /api/clients` - Liste tous les clients
- `GET /api/clients/{id}` - Récupère un client
- `POST /api/clients` - Créer un client
- `PUT /api/clients/{id}` - Modifier un client
- `DELETE /api/clients/{id}` - Supprimer un client
