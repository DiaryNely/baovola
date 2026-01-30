# Fonctionnalité 02 : Gestion des types de place

> Catégories de sièges : STANDARD, PREMIUM, VIP avec prix variables selon le départ

---

## 1. Architecture données

### 1.1 Tables principales
| Table | Description |
|-------|-------------|
| `ref_siege_categorie` | Référentiel des catégories de sièges |
| `depart_tarif_siege` | Tarif par catégorie pour chaque départ |
| `vehicule_siege_config` | Configuration des sièges d'un véhicule |

### 1.2 Colonnes utilisées

#### Table `ref_siege_categorie`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `code` | varchar(30) | Code unique (STANDARD, PREMIUM, VIP) |
| `libelle` | varchar(80) | Libellé affiché |

#### Table `depart_tarif_siege`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `depart_id` | bigint | FK vers depart |
| `ref_siege_categorie_id` | bigint | FK vers ref_siege_categorie |
| `ref_devise_id` | bigint | FK vers ref_devise |
| `montant` | decimal(12,2) | Prix du siège pour ce départ |

#### Table `vehicule_siege_config`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `vehicule_id` | bigint | FK vers vehicule |
| `ref_siege_categorie_id` | bigint | FK vers ref_siege_categorie |
| `nb_places` | integer | Nombre de places de cette catégorie |

### 1.3 Tables annexes

| Table | Relation | Colonne de liaison | Description |
|-------|----------|-------------------|-------------|
| `depart` | 1–N | `depart_id` | Départ concerné |
| `vehicule` | 1–N | `vehicule_id` | Véhicule avec configuration |
| `ref_devise` | N–1 | `ref_devise_id` | Devise du tarif (MGA) |
| `reservation_passager` | N–1 | `ref_siege_categorie_id` | Catégorie du siège réservé |

### 1.4 Règles de calcul

```
Mapping siège → catégorie :
  - Sièges 1 à nb_places_vip → VIP
  - Sièges suivants jusqu'à nb_places_premium → PREMIUM  
  - Reste → STANDARD

Tarif d'un siège = depart_tarif_siege.montant 
                   WHERE depart_id = X 
                   AND ref_siege_categorie_id = catégorie du siège
```

---

## 2. Dessins d'écran (ASCII)

### 2.1 Liste des catégories de sièges (Admin)
```
┌─────────────────────────────────────────────────────────────────────┐
│                 CATÉGORIES DE SIÈGES                                │
├───────┬──────────┬───────────────────────┬──────────────────────────┤
│  ID   │   Code   │       Libellé         │        Actions           │
├───────┼──────────┼───────────────────────┼──────────────────────────┤
│   1   │ VIP      │ Place VIP             │  [✏️ Modifier]           │
├───────┼──────────┼───────────────────────┼──────────────────────────┤
│   2   │ PREMIUM  │ Place Premium         │  [✏️ Modifier]           │
├───────┼──────────┼───────────────────────┼──────────────────────────┤
│   3   │ STANDARD │ Place Standard        │  [✏️ Modifier]           │
└───────┴──────────┴───────────────────────┴──────────────────────────┘
│                                           [ + Ajouter catégorie ]   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Configuration sièges d'un véhicule
```
┌─────────────────────────────────────────────────────────────────────┐
│           CONFIGURATION SIÈGES - VÉHICULE 1234 TBA                  │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Véhicule: Toyota Hiace | Immat: 1234 TBA | Capacité: 18 places     │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │  Catégorie          │  Nb Places  │  Sièges concernés       │    │
│  ├─────────────────────┼─────────────┼─────────────────────────┤    │
│  │  VIP                │  [  3  ]    │  1, 2, 3                │    │
│  ├─────────────────────┼─────────────┼─────────────────────────┤    │
│  │  PREMIUM            │  [  6  ]    │  4, 5, 6, 7, 8, 9       │    │
│  ├─────────────────────┼─────────────┼─────────────────────────┤    │
│  │  STANDARD           │  [  9  ]    │  10 à 18                │    │
│  └─────────────────────┴─────────────┴─────────────────────────┘    │
│                                                                     │
│  Total configuré: 18 / 18 places                                    │
│                                                                     │
│                    [ ENREGISTRER ]    [ ANNULER ]                   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 Tarification par catégorie pour un départ
```
┌─────────────────────────────────────────────────────────────────────┐
│              TARIFS SIÈGES - DÉPART DEP-2026-01-30-001              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Trajet: Tana → Antsirabe                                           │
│  Date: 30/01/2026 08:00                                             │
│  Véhicule: 1234 TBA (18 places)                                     │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  Catégorie   │  Nb Places  │  Tarif unitaire  │  CA Maximum  │   │
│  ├──────────────┼─────────────┼──────────────────┼──────────────┤   │
│  │  VIP         │      3      │  [ 75 000 ] MGA  │   225 000    │   │
│  ├──────────────┼─────────────┼──────────────────┼──────────────┤   │
│  │  PREMIUM     │      6      │  [ 60 000 ] MGA  │   360 000    │   │
│  ├──────────────┼─────────────┼──────────────────┼──────────────┤   │
│  │  STANDARD    │      9      │  [ 50 000 ] MGA  │   450 000    │   │
│  └──────────────┴─────────────┴──────────────────┴──────────────┘   │
│                                                                     │
│  CA Maximum Total: 1 035 000 MGA                                    │
│                                                                     │
│                    [ ENREGISTRER ]    [ ANNULER ]                   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.4 Modale ajout/modification tarif
```
┌─────────────────────────────────────────────────────────┐
│              MODIFIER TARIF SIÈGE                       │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Départ:     DEP-2026-01-30-001                         │
│  Catégorie:  VIP                                        │
│                                                         │
│  Montant     [__________75000__] MGA                    │
│                                                         │
│  ⚠️ Attention: modifier le tarif recalculera            │
│     les réservations CONFIRMEES existantes              │
│                                                         │
│           [ ENREGISTRER ]    [ ANNULER ]                │
└─────────────────────────────────────────────────────────┘
```

---

## 3. Métier / Logique applicative

### 3.1 Classes utilisées

| Classe | Responsabilité | Emplacement |
|--------|---------------|-------------|
| `SiegeCategorieController` | CRUD catégories de sièges | Controller |
| `DepartTarifSiegeController` | CRUD tarifs par départ | Controller |
| `VehiculeController` | Gestion config sièges véhicule | Controller |
| `SiegeCategorieService` | Logique catégories | Service |
| `DepartTarifSiegeService` | Logique tarification | Service |
| `VehiculeSiegeConfigService` | Config sièges véhicule | Service |
| `SiegeConfigurationService` | Mapping siège → catégorie | Service |

### 3.2 Méthodes principales

#### SiegeCategorieService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findAll` | - | `List<SiegeCategorieDTO>` | Liste toutes les catégories |
| `findByCode` | `String code` | `RefSiegeCategorie` | Recherche par code |

#### DepartTarifSiegeService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findByDepartId` | `Long departId` | `List<DepartTarifSiegeDTO>` | Tarifs d'un départ |
| `save` | `DepartTarifSiegeDTO` | `DepartTarifSiegeDTO` | Crée/modifie un tarif |
| `createDefaultTarifs` | `Depart`, `BigDecimal montantBase` | `void` | Crée tarifs par défaut |
| `delete` | `Long id` | `void` | Supprime un tarif |

#### VehiculeSiegeConfigService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findByVehiculeId` | `Long vehiculeId` | `List<VehiculeSiegeConfigDTO>` | Config d'un véhicule |
| `saveConfiguration` | `Long vehiculeId`, `List<VehiculeSiegeConfigDTO>` | `void` | Enregistre la config |

#### SiegeConfigurationService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `buildSeatCategoryMap` | `Depart` | `Map<Integer, RefSiegeCategorie>` | N° siège → catégorie |
| `getTarifForSeat` | `Depart`, `Integer numeroSiege` | `BigDecimal` | Tarif d'un siège |

### 3.3 Règles métier

```
1. Création d'un départ :
   - Récupérer la configuration sièges du véhicule
   - Créer automatiquement les DepartTarifSiege avec montant par défaut
   
2. Modification d'un tarif :
   - Recalculer les réservations CONFIRMEES impactées
   - Mettre à jour montant_total des réservations
   - Mettre à jour montant_tarif des passagers

3. Mapping siège → catégorie :
   - Basé sur vehicule_siege_config
   - Sièges numérotés séquentiellement par catégorie
   - VIP d'abord, puis PREMIUM, puis STANDARD
```

### 3.4 Services impactés
- `SiegeCategorieService`
- `DepartTarifSiegeService`
- `VehiculeSiegeConfigService`
- `SiegeConfigurationService`
- `ReservationService` (recalcul lors de modification tarif)
