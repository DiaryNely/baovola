# Architecture de l'application Taxi Brousse

> Généré le 30/01/2026 - Basé sur le schéma simplifié (23 tables)

---

## 📊 Tables du schéma DBML

| Catégorie | Tables |
|-----------|--------|
| **Références** | `ref_devise`, `ref_depart_statut`, `ref_reservation_statut`, `ref_siege_categorie`, `ref_passager_categorie` |
| **Acteurs** | `lieu`, `cooperative`, `vehicule`, `chauffeur`, `client` |
| **Trajets** | `trajet`, `chauffeur_vehicule` |
| **Départs** | `depart`, `tarif` |
| **Réservations** | `reservation`, `reservation_passager`, `paiement`, `billet` |
| **Publicité** | `societe_publicitaire`, `publicite`, `tarif_publicite`, `depart_publicite` |
| **Produits** | `produit`, `stock_depart`, `vente_produit` |

---

## 🏗️ Entités Java (Entity)

### Tables de Référence
| Classe | Table DB |
|--------|----------|
| `RefDevise` | ref_devise |
| `RefDepartStatut` | ref_depart_statut |
| `RefReservationStatut` | ref_reservation_statut |
| `RefSiegeCategorie` | ref_siege_categorie |
| `RefPassagerCategorie` | ref_passager_categorie |

### Acteurs Principaux
| Classe | Table DB |
|--------|----------|
| `Lieu` | lieu |
| `Cooperative` | cooperative |
| `Vehicule` | vehicule |
| `Chauffeur` | chauffeur |
| `Client` | client |

### Trajets et Liaisons
| Classe | Table DB |
|--------|----------|
| `Trajet` | trajet |
| `ChauffeurVehicule` | chauffeur_vehicule |

### Départs et Tarification
| Classe | Table DB |
|--------|----------|
| `Depart` | depart |
| `Tarif` | tarif |

### Réservations et Paiements
| Classe | Table DB |
|--------|----------|
| `Reservation` | reservation |
| `ReservationPassager` | reservation_passager |
| `Paiement` | paiement |
| `Billet` | billet |

### Publicité
| Classe | Table DB |
|--------|----------|
| `SocietePublicitaire` | societe_publicitaire |
| `Publicite` | publicite |
| `TarifPublicite` | tarif_publicite |
| `DepartPublicite` | depart_publicite |

### Produits et Ventes
| Classe | Table DB |
|--------|----------|
| `Produit` | produit |
| `StockDepart` | stock_depart |
| `VenteProduit` | vente_produit |

---

## 📦 Repositories

### Tables de Référence
- `RefDeviseRepository`
- `RefDepartStatutRepository`
- `RefReservationStatutRepository`
- `RefSiegeCategorieRepository`
- `RefPassagerCategorieRepository`

### Acteurs Principaux
- `LieuRepository`
- `CooperativeRepository`
- `VehiculeRepository`
- `ChauffeurRepository`
- `ClientRepository`

### Trajets et Liaisons
- `TrajetRepository`
- `ChauffeurVehiculeRepository`

### Départs et Tarification
- `DepartRepository`
- `TarifRepository`

### Réservations et Paiements
- `ReservationRepository`
- `ReservationPassagerRepository`
- `PaiementRepository`
- `BilletRepository`

### Publicité
- `SocietePublicitaireRepository`
- `PubliciteRepository`
- `TarifPubliciteRepository`
- `DepartPubliciteRepository`

### Produits et Ventes
- `ProduitRepository`
- `StockDepartRepository`
- `VenteProduitRepository`

---

## ⚙️ Services

### Acteurs Principaux
- `CooperativeService`
- `VehiculeService`
- `ChauffeurService`
- `ClientService`

### Trajets
- `TrajetService`

### Départs et Tarification
- `DepartService`
- `TarifService`
- `DepartTarifSiegeService`

### Réservations et Paiements
- `ReservationService`
- `PaiementService`
- `BilletService`

### Publicité
- `SocietePublicitaireService`
- `PubliciteService`
- `TarifPubliciteService`
- `DepartPubliciteService`

### Produits et Ventes
- `ProduitService`
- `StockDepartService`
- `VenteProduitService`

### Services Statistiques
- `ChiffreAffairesStatsService`
- `PubliciteStatsService`
- `DashboardService`

---

## 🎮 Controllers (API REST)

### Acteurs Principaux
| Controller | Endpoint Base |
|------------|---------------|
| `LieuController` | `/api/lieux` |
| `CooperativeController` | `/api/cooperatives` |
| `VehiculeController` | `/api/vehicules` |
| `ChauffeurController` | `/api/chauffeurs` |
| `ClientController` | `/api/clients` |

### Trajets
| Controller | Endpoint Base |
|------------|---------------|
| `TrajetController` | `/api/trajets` |

### Départs et Tarification
| Controller | Endpoint Base |
|------------|---------------|
| `DepartController` | `/api/departs` |
| `TarifController` | `/api/tarifs` |
| `DepartTarifSiegeController` | `/api/depart-tarif-sieges` |

### Réservations et Paiements
| Controller | Endpoint Base |
|------------|---------------|
| `ReservationController` | `/api/reservations` |
| `PaiementController` | `/api/paiements` |
| `BilletController` | `/api/billets` |

### Publicité
| Controller | Endpoint Base |
|------------|---------------|
| `SocietePublicitaireController` | `/api/societes-publicitaires` |
| `PubliciteController` | `/api/publicites` |
| `TarifPubliciteController` | `/api/tarif-publicites` |
| `DepartPubliciteController` | `/api/depart-publicites` |

### Produits et Ventes
| Controller | Endpoint Base |
|------------|---------------|
| `ProduitController` | `/api/produits` |
| `StockDepartController` | `/api/stock-departs` |
| `VenteProduitController` | `/api/vente-produits` |

### Statistiques et Dashboard
| Controller | Endpoint Base |
|------------|---------------|
| `ChiffreAffairesStatsController` | `/api/stats/ca` |
| `PubliciteStatsController` | `/api/stats/publicite` |
| `DashboardFinancierController` | `/api/dashboard` |

### Références
| Controller | Endpoint Base |
|------------|---------------|
| `ReferenceController` | `/api/references` |
| `SiegeCategorieController` | `/api/siege-categories` |

---

## 📋 DTOs (Data Transfer Objects)

### Acteurs
- `LieuDTO`
- `CooperativeDTO`
- `VehiculeDTO`
- `ChauffeurDTO`
- `ClientDTO`

### Trajets et Départs
- `TrajetDTO`
- `DepartDTO`
- `TarifDTO`
- `DepartTarifSiegeDTO`

### Réservations
- `ReservationDTO`
- `ReservationPassagerDTO`
- `PaiementDTO`
- `BilletDTO`
- `CreerReservationRequest`
- `PaiementRequest`
- `SeatInfoDTO`

### Publicité
- `SocietePublicitaireDTO`
- `PubliciteDTO`
- `TarifPubliciteDTO`
- `DepartPubliciteDTO`

### Produits
- `ProduitDTO`
- `StockDepartDTO`
- `VenteProduitDTO`

### Statistiques
- `ChiffreAffairesStatsDTO`
- `DashboardDTO`
- `PubliciteCaStatsDTO`
- `StatistiquesFinancieresDTO`

---

## 🔄 Mappers

| Mapper | Entity → DTO |
|--------|--------------|
| `CooperativeMapper` | Cooperative → CooperativeDTO |
| `VehiculeMapper` | Vehicule → VehiculeDTO |
| `ChauffeurMapper` | Chauffeur → ChauffeurDTO |
| `ClientMapper` | Client → ClientDTO |
| `TrajetMapper` | Trajet → TrajetDTO |
| `DepartMapper` | Depart → DepartDTO |
| `TarifMapper` | Tarif → TarifDTO |
| `ReservationMapper` | Reservation → ReservationDTO |
| `ReservationPassagerMapper` | ReservationPassager → ReservationPassagerDTO |
| `BilletMapper` | Billet → BilletDTO |
| `PubliciteMapper` | Publicite → PubliciteDTO |
| `DepartPubliciteMapper` | DepartPublicite → DepartPubliciteDTO |
| `TarifPubliciteMapper` | TarifPublicite → TarifPubliciteDTO |
| `ProduitMapper` | Produit → ProduitDTO |
| `StockDepartMapper` | StockDepart → StockDepartDTO |
| `VenteProduitMapper` | VenteProduit → VenteProduitDTO |

---

## 📁 Structure des packages

```
com.taxi_brousse/
├── controller/          # 31 controllers REST
├── dto/                 # 40 DTOs
├── entity/              # 35 entités JPA
│   └── reference/       # 14 tables de référence
├── mapper/              # 19 mappers
├── repository/          # 41 repositories
├── service/             # 28 services
└── exception/           # Gestion des erreurs
```

---

## 🔗 Flux de données

```
Client HTTP → Controller → Service → Repository → Entity → Base de données
                 ↓              ↓
               DTO ←─── Mapper ←┘
```
