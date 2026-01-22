# Structure MVC - Projet Taxi Brousse

## ✅ Architecture Complète Implémentée

### 📁 Structure des Packages

```
com.taxi_brousse/
│
├── 📦 entity/                    # Modèles de données (JPA)
│   ├── Vehicule.java
│   ├── Cooperative.java
│   ├── Chauffeur.java
│   ├── Client.java
│   ├── DepenseVehicule.java ⭐ NOUVEAU
│   ├── Depart.java
│   ├── Trajet.java
│   ├── Reservation.java
│   ├── ...
│   └── reference/
│       ├── RefTypeDepense.java ⭐ NOUVEAU
│       ├── RefVehiculeType.java
│       ├── RefDevise.java
│       └── ...
│
├── 📦 repository/                # Accès aux données
│   ├── VehiculeRepository.java
│   ├── CooperativeRepository.java
│   ├── ChauffeurRepository.java
│   ├── ClientRepository.java
│   ├── DepenseVehiculeRepository.java ⭐ NOUVEAU
│   ├── RefTypeDepenseRepository.java ⭐ NOUVEAU
│   └── ...
│
├── 📦 dto/                       # Data Transfer Objects ⭐ NOUVEAU
│   ├── VehiculeDTO.java
│   ├── CooperativeDTO.java
│   ├── ChauffeurDTO.java
│   ├── ClientDTO.java
│   └── DepenseVehiculeDTO.java
│
├── 📦 mapper/                    # Convertisseurs Entity <-> DTO ⭐ NOUVEAU
│   ├── VehiculeMapper.java
│   ├── CooperativeMapper.java
│   ├── ChauffeurMapper.java
│   ├── ClientMapper.java
│   └── DepenseVehiculeMapper.java
│
├── 📦 service/                   # Logique métier ⭐ NOUVEAU
│   ├── VehiculeService.java
│   ├── CooperativeService.java
│   ├── ChauffeurService.java
│   ├── ClientService.java
│   └── DepenseVehiculeService.java
│
├── 📦 controller/                # Contrôleurs REST
│   ├── VehiculeController.java ⭐ NOUVEAU
│   ├── CooperativeController.java ⭐ NOUVEAU
│   ├── ChauffeurController.java ⭐ NOUVEAU
│   ├── ClientController.java ⭐ NOUVEAU
│   ├── DepenseVehiculeController.java ⭐ NOUVEAU
│   ├── GestionController.java (ancien)
│   └── HomeController.java (ancien)
│
├── 📦 exception/                 # Gestion des erreurs ⭐ NOUVEAU
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   └── GlobalExceptionHandler.java
│
└── TaxiBrousseApplication.java
```

## 🎯 Fonctionnalités Implémentées

### 1. Gestion des Dépenses Véhicule
- ✅ Table `depense_vehicule` dans la base de données
- ✅ Table de référence `ref_type_depense` avec 13 types de dépenses
- ✅ Entités JPA avec relations ManyToOne
- ✅ Repository avec requêtes personnalisées
- ✅ Service avec logique métier
- ✅ Contrôleur REST avec 9 endpoints

### 2. Architecture MVC Complète
- ✅ **Model** : Entités JPA (74 fichiers compilés)
- ✅ **View** : DTOs pour exposition API
- ✅ **Controller** : Contrôleurs REST avec validation
- ✅ **Service** : Couche de logique métier
- ✅ **Repository** : Accès aux données
- ✅ **Mapper** : Conversion automatique Entity <-> DTO
- ✅ **Exception** : Gestion centralisée des erreurs

## 🔗 Endpoints API REST

### Dépenses Véhicule
```
GET    /api/depenses-vehicule                           # Liste toutes
GET    /api/depenses-vehicule/{id}                      # Par ID
GET    /api/depenses-vehicule/vehicule/{id}             # Par véhicule
GET    /api/depenses-vehicule/cooperative/{id}          # Par coopérative
GET    /api/depenses-vehicule/periode                   # Par période
GET    /api/depenses-vehicule/vehicule/{id}/periode     # Véhicule + période
POST   /api/depenses-vehicule                           # Créer
PUT    /api/depenses-vehicule/{id}                      # Modifier
DELETE /api/depenses-vehicule/{id}                      # Supprimer
```

### Véhicules
```
GET    /api/vehicules          # Liste tous
GET    /api/vehicules/{id}     # Par ID
POST   /api/vehicules          # Créer
PUT    /api/vehicules/{id}     # Modifier
DELETE /api/vehicules/{id}     # Supprimer
```

### Coopératives
```
GET    /api/cooperatives       # Liste toutes
GET    /api/cooperatives/{id}  # Par ID
POST   /api/cooperatives       # Créer
PUT    /api/cooperatives/{id}  # Modifier
DELETE /api/cooperatives/{id}  # Supprimer
```

### Chauffeurs
```
GET    /api/chauffeurs         # Liste tous
GET    /api/chauffeurs/{id}    # Par ID
POST   /api/chauffeurs         # Créer
PUT    /api/chauffeurs/{id}    # Modifier
DELETE /api/chauffeurs/{id}    # Supprimer
```

### Clients
```
GET    /api/clients            # Liste tous
GET    /api/clients/{id}       # Par ID
POST   /api/clients            # Créer
PUT    /api/clients/{id}       # Modifier
DELETE /api/clients/{id}       # Supprimer
```

## 📊 Types de Dépenses Disponibles

| Code | Libellé |
|------|---------|
| CARBURANT | Carburant |
| REPARATION_MOTEUR | Réparation moteur |
| REPARATION_CARROSSERIE | Réparation carrosserie |
| PNEUS | Pneus |
| VIDANGE | Vidange |
| REVISION | Révision |
| ASSURANCE | Assurance |
| TAXE | Taxe et impôts |
| PEAGE | Péage |
| LAVAGE | Lavage |
| PIECES_DETACHEES | Pièces détachées |
| ENTRETIEN | Entretien général |
| FRAIS_DIVERS | Frais divers |

## 🛠️ Technologies et Dépendances

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Spring Boot Core -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webmvc</artifactId>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Base de données -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Outils -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

## 📝 Exemple d'Utilisation

### Créer une Dépense

**Request:**
```bash
curl -X POST http://localhost:8080/api/depenses-vehicule \
  -H "Content-Type: application/json" \
  -d '{
    "vehiculeId": 1,
    "cooperativeId": 1,
    "refTypeDepenseId": 1,
    "refDeviseId": 1,
    "montant": 150000,
    "dateDepense": "2026-01-09",
    "description": "Carburant pour trajet Antananarivo-Toamasina",
    "numeroPiece": "FACT-2026-001"
  }'
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

### Consulter les Dépenses par Véhicule

```bash
curl http://localhost:8080/api/depenses-vehicule/vehicule/1
```

### Consulter les Dépenses par Période

```bash
curl "http://localhost:8080/api/depenses-vehicule/periode?dateDebut=2026-01-01&dateFin=2026-01-31"
```

## ✨ Avantages de cette Architecture

1. **Séparation des responsabilités** - Chaque couche a un rôle clair
2. **Maintenabilité** - Code organisé et facile à maintenir
3. **Testabilité** - Chaque couche peut être testée indépendamment
4. **Extensibilité** - Facile d'ajouter de nouvelles fonctionnalités
5. **Réutilisabilité** - Services et mappers réutilisables
6. **Validation** - Validation automatique des données
7. **Gestion des erreurs** - Traitement cohérent des erreurs
8. **Documentation** - Code auto-documenté avec DTOs

## 🚀 Prochaines Étapes

Pour utiliser cette architecture :

1. **Démarrer l'application** : `mvn spring-boot:run`
2. **Tester les endpoints** : Utiliser Postman ou curl
3. **Créer des données de test** : Utiliser les scripts SQL fournis
4. **Étendre** : Ajouter d'autres ressources en suivant le même pattern

## 📚 Documentation Complète

Voir [ARCHITECTURE.md](ARCHITECTURE.md) pour plus de détails sur l'architecture.
