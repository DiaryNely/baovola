# Dossier d'examen — Remises par tarif siège & catégorie passager

_Date: 2026-01-16_

## 1) MCD (Modèle Conceptuel de Données)

### 1.1 Tables concernées

- **depart**
  - id (PK)
  - code, date_heure_depart, trajet_id, vehicule_id, cooperative_id, ...

- **ref_siege_categorie**
  - id (PK)
  - code, libelle, ordre

- **ref_passager_categorie**
  - id (PK)
  - code, libelle

- **depart_tarif_siege**
  - id (PK)
  - depart_id (FK → depart.id)
  - ref_siege_categorie_id (FK → ref_siege_categorie.id)
  - ref_devise_id (FK → ref_devise.id)
  - montant

- **depart_tarif_remise**
  - id (PK)
  - depart_id (FK → depart.id)
  - ref_siege_categorie_id (FK → ref_siege_categorie.id)
  - ref_passager_categorie_id (FK → ref_passager_categorie.id)
  - type_remise (VALEUR | POURCENT)
  - montant
  - created_at
  - **Unique** (depart_id, ref_siege_categorie_id, ref_passager_categorie_id)

- **reservation**
  - id (PK)
  - depart_id (FK → depart.id)
  - client_id (FK → client.id)
  - montant_total, montant_paye, reste_a_payer, ...

- **reservation_passager**
  - id (PK)
  - reservation_id (FK → reservation.id)
  - depart_id (FK → depart.id)
  - ref_siege_categorie_id (FK → ref_siege_categorie.id)
  - ref_passager_categorie_id (FK → ref_passager_categorie.id)
  - montant_tarif (tarif final appliqué)
  - montant_remise (différence info)
  - numero_siege, nom, prenom, ...

### 1.2 Relations (cardinalités)

- depart (1) —— (N) depart_tarif_siege
- ref_siege_categorie (1) —— (N) depart_tarif_siege
- depart (1) —— (N) depart_tarif_remise
- ref_siege_categorie (1) —— (N) depart_tarif_remise
- ref_passager_categorie (1) —— (N) depart_tarif_remise
- reservation (1) —— (N) reservation_passager
- ref_siege_categorie (1) —— (N) reservation_passager
- ref_passager_categorie (1) —— (N) reservation_passager

### 1.3 MCD — Schéma ASCII

```
┌─────────────────────┐           ┌────────────────────────┐
│        depart       │           │   ref_siege_categorie  │
├─────────────────────┤           ├────────────────────────┤
│ PK id               │           │ PK id                  │
│ code                │           │ code                   │
│ date_heure_depart   │           │ libelle                │
│ trajet_id (FK)      │           │ ordre                  │
│ vehicule_id (FK)    │           └────────────────────────┘
│ cooperative_id (FK) │
└─────────────────────┘
          │ 1                             │ 1
          │                               │
          │ N                             │ N
┌────────────────────────────┐            │
│     depart_tarif_siege     │            │
├────────────────────────────┤            │
│ PK id                      │            │
│ FK depart_id               │────────────┘
│ FK ref_siege_categorie_id  │
│ FK ref_devise_id           │
│ montant                    │
└────────────────────────────┘

┌──────────────────────────┐
│   ref_passager_categorie │
├──────────────────────────┤
│ PK id                    │
│ code                     │
│ libelle                  │
└──────────────────────────┘
          │ 1
          │
          │ N
┌────────────────────────────┐
│     depart_tarif_remise    │
├────────────────────────────┤
│ PK id                      │
│ FK depart_id               │───┐
│ FK ref_siege_categorie_id  │───┼─ vers ref_siege_categorie
│ FK ref_passager_categorie_id│──┘
│ type_remise (VALEUR/%)     │
│ montant                    │
│ created_at                 │
│ UNIQUE(depart_id, ref_siege_categorie_id,
│        ref_passager_categorie_id)        │
└────────────────────────────┘

┌─────────────────────┐           ┌────────────────────────┐
│     reservation     │           │   reservation_passager │
├─────────────────────┤           ├────────────────────────┤
│ PK id               │ 1       N │ PK id                  │
│ FK depart_id        │───────────│ FK reservation_id      │
│ FK client_id        │           │ FK depart_id           │
│ montant_total       │           │ FK ref_siege_categorie_id
│ montant_paye        │           │ FK ref_passager_categorie_id
│ reste_a_payer       │           │ montant_tarif (final)  │
└─────────────────────┘           │ montant_remise         │
                                 │ numero_siege           │
                                 │ nom, prenom            │
                                 └────────────────────────┘
```

---

## 2) Maquettes des pages (UI)

### 2.1 Page “Remises sièges” (CRUD)

- **Objectif**: gérer la configuration des remises par départ, catégorie siège et catégorie passager.
- **Zones**:
  1. Tableau (liste des remises)
  2. Bouton “Nouvelle remise”
  3. Modal de création/modification

```
┌────────────────────────────────────────────────────────────────────────────┐
│  Remises par tarif siège                             [➕ Nouvelle remise]  │
├────────────────────────────────────────────────────────────────────────────┤
│  ID │ Départ │ Cat. siège │ Cat. passager │ Type │ Montant │ Actions       │
│  1  │ DEP01  │ VIP        │ ENFANT        │ VALEUR │ 20 000 │ ✏️  🗑️        │
│  2  │ DEP01  │ ECO        │ ADULTE        │ %      │ 80     │ ✏️  🗑️        │
└────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────┐
│  Nouvelle remise                      ✖    │
├───────────────────────────────────────────┤
│  Départ *              [Select.........]  │
│  Catégorie siège *     [Select.........]  │
│  Catégorie passager *  [Select.........]  │
│  Type remise *         [VALEUR | %     ]  │
│  Montant *             [..............]   │
│                                           │
│              [Annuler] [💾 Enregistrer]   │
└───────────────────────────────────────────┘
```

### 2.2 Page “Réservation” (étape paiement)

- **Objectif**: afficher le tarif final par siège et indiquer si une remise est appliquée.
- **Zone**:
  - Tableau récapitulatif par siège (tarif de base, remise, tarif à payer)

```
┌─────────────────────────────────────────────────────────────┐
│  Récapitulatif et Paiement                                  │
├─────────────────────────────────────────────────────────────┤
│  Montant total: 120 000 Ar                                   │
│  Nombre de passagers: 2                                      │
├─────────────────────────────────────────────────────────────┤
│  Tarifs par siège                                            │
│  Siège │ Cat. siège │ Cat. passager │ Base │ Remise │ À payer│
│   5    │ VIP        │ ENFANT         │ 50k  │ 20k    │ 20k    │
│   6    │ ECO        │ ADULTE         │ 70k  │ 80%    │ 56k    │
├─────────────────────────────────────────────────────────────┤
│  Mode de paiement * [Paiement immédiat / Comptoir / Embarq.] │
│  ...                                                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 3) Métier (classes, méthodes, logique)

### 3.1 Entités / DTO / Repositories

- **DepartTarifRemise** (entity)
  - Champs: depart, refSiegeCategorie, refPassagerCategorie, typeRemise, montant

- **DepartTarifRemiseDTO**
  - Champs: departId, refSiegeCategorieId, refPassagerCategorieId, typeRemise, montant

- **DepartTarifRemiseRepository**
  - `List<DepartTarifRemise> findByDepartId(Long departId)`
  - `boolean existsByDepartIdAndRefSiegeCategorieIdAndRefPassagerCategorieId(Long departId, Long refSiegeCategorieId, Long refPassagerCategorieId)`
  - `boolean existsByDepartIdAndRefSiegeCategorieIdAndRefPassagerCategorieIdAndIdNot(Long departId, Long refSiegeCategorieId, Long refPassagerCategorieId, Long id)`

### 3.2 Services

- **DepartTarifRemiseService**
  - `List<DepartTarifRemiseDTO> getAll()`
  - `DepartTarifRemiseDTO getById(Long id)`
  - `List<DepartTarifRemiseDTO> getRemisesByDepart(Long departId)`
  - `DepartTarifRemiseDTO create(DepartTarifRemiseDTO dto)`
  - `DepartTarifRemiseDTO update(Long id, DepartTarifRemiseDTO dto)`
  - `void delete(Long id)`

**Logique métier principale**:
- Interdire la duplication d’une remise pour le même couple (depart, catégorie siège, catégorie passager).
- CRUD complet sur la table depart_tarif_remise.

- **ReservationService**
  - `ReservationDTO creerReservation(CreerReservationRequest request)`
  - `Map<Long, Map<Long, DepartTarifRemise>> loadRemisesByDepart(Long departId)`
  - `BigDecimal applyRemise(BigDecimal montantBase, Long siegeCategorieId, Long passagerCategorieId, Map<Long, Map<Long, DepartTarifRemise>> remisesByCategorie)`

**Logique métier remise (nouvelle règle)**:
- Si remise existe:
  - **Type VALEUR**: le tarif final = valeur de la remise (prix fixé)
  - **Type POURCENT**: le tarif final = montantBase × (pourcentage/100)
- Sinon: tarif final = montantBase

### 3.3 Controllers

- **DepartTarifRemiseController**
  - `GET /api/tarifs-remises`
  - `GET /api/tarifs-remises/{id}`
  - `GET /api/tarifs-remises/depart/{departId}`
  - `POST /api/tarifs-remises`
  - `PUT /api/tarifs-remises/{id}`
  - `DELETE /api/tarifs-remises/{id}`

- **DepartController**
  - `GET /api/departs/{id}/remises-passagers`

### 3.4 Pages (templates)

- **tarifs-remises.html**
  - CRUD de remise (modal + tableau)

- **reservations_new.html**
  - Ajout du tableau “Tarifs par siège” dans l’étape paiement

- **reservation-critere.html**
  - Ajout du tableau “Tarifs par siège” dans l’étape paiement

---

## 4) Remarque

Les schémas et maquettes sont fournis en ASCII conformément à la demande d'examen.
