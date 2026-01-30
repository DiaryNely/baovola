# Fonctionnalité 01 : Réservation de place

> Critères de recherche : date_heure_depart, lieu_depart, lieu_arrivee

---

## 1. Architecture données

### 1.1 Table principale
| Table | Description |
|-------|-------------|
| `reservation` | Enregistrement d'une réservation client |

### 1.2 Colonnes utilisées
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `code` | varchar(80) | Code unique auto-généré |
| `client_id` | bigint | FK vers client |
| `depart_id` | bigint | FK vers depart |
| `ref_reservation_statut_id` | bigint | FK vers ref_reservation_statut |
| `date_creation` | timestamp | Date de création |
| `montant_total` | decimal(12,2) | Montant total calculé |

### 1.3 Tables annexes

| Table | Relation | Colonne de liaison | Description |
|-------|----------|-------------------|-------------|
| `client` | N–1 | `client_id` | Client effectuant la réservation |
| `depart` | N–1 | `depart_id` | Départ réservé |
| `ref_reservation_statut` | N–1 | `ref_reservation_statut_id` | Statut (BROUILLON, CONFIRMEE, ANNULEE) |
| `reservation_passager` | 1–N | `reservation_id` | Passagers de la réservation |
| `paiement` | 1–N | `reservation_id` | Paiements associés |
| `billet` | 1–N | `reservation_id` | Billets émis |
| `trajet` | via depart | `depart.trajet_id` | Trajet avec lieux départ/arrivée |
| `lieu` | via trajet | `lieu_depart_id`, `lieu_arrivee_id` | Lieux de départ et arrivée |

---

## 2. Dessins d'écran (ASCII)

### 2.1 Écran de recherche de départs
```
┌─────────────────────────────────────────────────────────────────────┐
│                    RECHERCHER UN DÉPART                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Lieu de départ    [▼ Sélectionner________________]                 │
│                                                                     │
│  Lieu d'arrivée    [▼ Sélectionner________________]                 │
│                                                                     │
│  Date de départ    [📅 JJ/MM/AAAA    ]                              │
│                                                                     │
│                    [ 🔍 RECHERCHER ]                                │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Liste des départs disponibles
```
┌─────────────────────────────────────────────────────────────────────┐
│                    DÉPARTS DISPONIBLES                              │
├─────────────────────────────────────────────────────────────────────┤
│  Tana → Antsirabe  |  30/01/2026 08:00  |  12 places  |  [RÉSERVER] │
├─────────────────────────────────────────────────────────────────────┤
│  Tana → Antsirabe  |  30/01/2026 14:00  |  5 places   |  [RÉSERVER] │
├─────────────────────────────────────────────────────────────────────┤
│  Tana → Antsirabe  |  31/01/2026 06:00  |  18 places  |  [RÉSERVER] │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 Sélection des sièges
```
┌─────────────────────────────────────────────────────────────────────┐
│                    SÉLECTION DES PLACES                             │
│  Départ: DEP-001 | Tana → Antsirabe | 30/01/2026 08:00              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│     ┌─────┐                                                         │
│     │  C  │  Chauffeur                                              │
│     └─────┘                                                         │
│  ┌─────┬─────┬─────┐                                                │
│  │ [1] │ [2] │ [3] │  VIP - 75 000 MGA                              │
│  ├─────┼─────┼─────┤                                                │
│  │ [4] │ [5] │ [6] │  PREMIUM - 60 000 MGA                          │
│  ├─────┼─────┼─────┤                                                │
│  │ [7] │ [8] │ [9] │  STANDARD - 50 000 MGA                         │
│  ├─────┼─────┼─────┼─────┐                                          │
│  │[10] │[11] │[12] │[13] │  STANDARD - 50 000 MGA                   │
│  └─────┴─────┴─────┴─────┘                                          │
│                                                                     │
│  Légende: [■] Occupé  [○] Sélectionné  [ ] Disponible               │
│                                                                     │
│  Places sélectionnées: 2, 5         Total: 135 000 MGA              │
│                                                                     │
│                    [ CONTINUER → ]                                  │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.4 Formulaire passagers
```
┌─────────────────────────────────────────────────────────────────────┐
│                    INFORMATIONS PASSAGERS                           │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ═══ Siège N°2 (VIP) ═══                                            │
│  Nom       [____________________]                                   │
│  Prénom    [____________________]                                   │
│  Catégorie [▼ ADULTE___________]  (ENFANT, ADULTE, SENIOR)          │
│                                                                     │
│  ═══ Siège N°5 (PREMIUM) ═══                                        │
│  Nom       [____________________]                                   │
│  Prénom    [____________________]                                   │
│  Catégorie [▼ ADULTE___________]                                    │
│                                                                     │
│  ───────────────────────────────────────                            │
│  Sous-total:          135 000 MGA                                   │
│  Remise enfant:       - 5 000 MGA                                   │
│  ───────────────────────────────────────                            │
│  TOTAL:               130 000 MGA                                   │
│                                                                     │
│           [ ← RETOUR ]          [ CONFIRMER → ]                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.5 Modale de confirmation
```
┌─────────────────────────────────────────────────────────────────────┐
│                    ✓ RÉSERVATION CONFIRMÉE                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Code réservation: RES-2026-01-30-001                               │
│                                                                     │
│  Trajet: Tana → Antsirabe                                           │
│  Date:   30/01/2026 08:00                                           │
│  Places: 2, 5                                                       │
│  Montant: 130 000 MGA                                               │
│                                                                     │
│  Statut paiement: EN ATTENTE                                        │
│                                                                     │
│         [ PAYER MAINTENANT ]    [ FERMER ]                          │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Métier / Logique applicative

### 3.1 Classes utilisées

| Classe | Responsabilité | Emplacement |
|--------|---------------|-------------|
| `ReservationController` | Endpoints REST CRUD + recherche | Controller |
| `ReservationService` | Logique métier réservation | Service |
| `DepartService` | Recherche départs disponibles | Service |
| `SiegeConfigurationService` | Mapping sièges/catégories | Service |
| `ReservationMapper` | Conversion Entity ↔ DTO | Mapper |
| `ReservationPassagerMapper` | Conversion passagers | Mapper |

### 3.2 Méthodes principales

#### DepartService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `searchDeparts` | `lieuDepartId`, `lieuArriveeId`, `dateDepart` | `List<DepartDTO>` | Recherche les départs selon critères |
| `findById` | `Long id` | `DepartDTO` | Récupère un départ avec places disponibles |
| `getPlacesDisponibles` | `Long departId` | `List<SeatInfoDTO>` | Liste des sièges avec statut et tarif |

#### ReservationService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `creerReservation` | `CreerReservationRequest` | `ReservationDTO` | Crée une réservation avec passagers |
| `findById` | `Long id` | `ReservationDTO` | Détail d'une réservation |
| `findByClientId` | `Long clientId` | `List<ReservationDTO>` | Réservations d'un client |
| `confirmerReservation` | `Long id` | `ReservationDTO` | Passe statut BROUILLON → CONFIRMEE |
| `annulerReservation` | `Long id` | `void` | Annule et libère les places |
| `calculerMontantTotal` | `Reservation` | `BigDecimal` | Calcule tarifs - remises |

#### SiegeConfigurationService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `buildSeatCategoryMap` | `Depart` | `Map<Integer, RefSiegeCategorie>` | Associe n° siège → catégorie |
| `getSeatInfo` | `Depart`, `Integer numeroSiege` | `SeatInfoDTO` | Info complète d'un siège |

### 3.3 Workflow de réservation

```
1. Client recherche (lieu_depart, lieu_arrivee, date)
       ↓
2. Liste des départs disponibles affichée
       ↓
3. Sélection d'un départ → affichage plan sièges
       ↓
4. Sélection des sièges (vérification disponibilité temps réel)
       ↓
5. Saisie informations passagers + catégorie (ENFANT/ADULTE/SENIOR)
       ↓
6. Calcul automatique : tarif_siege - remise_passager = montant_passager
       ↓
7. Création réservation (statut = BROUILLON)
       ↓
8. Confirmation → statut = CONFIRMEE
       ↓
9. Paiement (optionnel immédiat ou différé)
       ↓
10. Émission billet
```

### 3.4 Services impactés
- `ReservationService`
- `DepartService`
- `SiegeConfigurationService`
- `PaiementService`
- `BilletService`
