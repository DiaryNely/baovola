# Dossier d’examen — Tarif Senior (−20% du tarif adulte)

_Date: 2026-01-16_

## 1) MCD (ASCII) des tables impliquées

```
┌──────────────────────────┐
│   ref_passager_categorie │
├──────────────────────────┤
│ PK id                    │
│ code (ADULTE/ENFANT/...) │
│ libelle                  │
└──────────────────────────┘
          │ 1
          │
          │ N
┌──────────────────────────┐
│   reservation_passager   │
├──────────────────────────┤
│ PK id                    │
│ FK reservation_id        │
│ FK depart_id             │
│ FK ref_siege_categorie_id│
│ FK ref_passager_categorie_id
│ montant_tarif            │
│ montant_remise           │
│ numero_siege, nom, prenom│
└──────────────────────────┘
          ▲
          │
┌──────────────────────────┐
│     depart_tarif_siege   │
├──────────────────────────┤
│ PK id                    │
│ FK depart_id             │
│ FK ref_siege_categorie_id│
│ FK ref_devise_id         │
│ montant (tarif base)     │
└──────────────────────────┘
```

**Note**: la modification ajoute une catégorie **SENIOR** dans `ref_passager_categorie`.

---

## 2) Maquettes ASCII des pages impactées

### 2.1 Sélection des passagers (réservation)

```
┌────────────────────────────────────────────┐
│  Informations des passagers                │
├────────────────────────────────────────────┤
│  Passager - Siège 5 (VIP) - 50 000 Ar       │
│  Nom *      [.........................]    │
│  Prénom     [.........................]    │
│  Catégorie  [ADULTE | ENFANT | SENIOR]      │
└────────────────────────────────────────────┘
```

### 2.2 Récapitulatif paiement (tarif final)

```
┌────────────────────────────────────────────────────────────┐
│  Récapitulatif et Paiement                                 │
├────────────────────────────────────────────────────────────┤
│  Tarifs par siège                                          │
│  Siège │ Cat. siège │ Cat. passager │ Base │ À payer       │
│   5    │ VIP        │ SENIOR         │ 50k │ 40k (−20%)    │
└────────────────────────────────────────────────────────────┘
```

---

## 3) Métier (classes, méthodes, logique)

### 3.1 Références

- **RefPassagerCategorie** (entity)
  - Ajouter une ligne: `code=SENIOR`, `libelle=Senior`.

### 3.2 Services

- **ReservationService**
  - `ReservationDTO creerReservation(CreerReservationRequest request)`
  - `BigDecimal applyRemise(BigDecimal montantBase, Long siegeCategorieId, Long passagerCategorieId, Map<Long, Map<Long, DepartTarifRemise>> remisesByCategorie)` *(si remises encore actives)*

**Logique métier Senior (nouvelle règle)**
- Lorsque `refPassagerCategorie.code == "SENIOR"` :
  - $tarifFinal = tarifBase − (tarifBase × 20\%)$
- Pour les autres catégories :
  - $tarifFinal = tarifBase$ (ou logique existante)

### 3.3 Endpoints impactés

- `GET /api/reference/passager-categories` (doit renvoyer SENIOR)
- `POST /api/reservations` (calcul du tarif final côté service)

---

## 4) Résumé

- **Aucune nouvelle table**: ajout d’une valeur **SENIOR** dans `ref_passager_categorie`.
- **Calcul tarif**: base tarif-siege appliqué, puis réduction de 20% pour SENIOR.
- **UI**: catégorie SENIOR apparaît dans le select; le récapitulatif affiche le tarif final.
