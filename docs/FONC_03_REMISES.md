# Fonctionnalité 03 : Gestion des remises

> Catégories passager : ENFANT, ADULTE, SENIOR avec remises par catégorie de siège

---

## 1. Architecture données

### 1.1 Tables principales
| Table | Description |
|-------|-------------|
| `ref_passager_categorie` | Référentiel des catégories de passagers |
| `depart_tarif_remise` | Remise par catégorie passager/siège pour un départ |

### 1.2 Colonnes utilisées

#### Table `ref_passager_categorie`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `code` | varchar(30) | Code unique (ENFANT, ADULTE, SENIOR) |
| `libelle` | varchar(80) | Libellé affiché |

#### Table `depart_tarif_remise`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `depart_id` | bigint | FK vers depart |
| `ref_siege_categorie_id` | bigint | FK vers ref_siege_categorie |
| `ref_passager_categorie_id` | bigint | FK vers ref_passager_categorie |
| `type_remise` | varchar(20) | Type : VALEUR ou POURCENT |
| `montant` | decimal(12,2) | Valeur de la remise |

### 1.3 Tables annexes

| Table | Relation | Colonne de liaison | Description |
|-------|----------|-------------------|-------------|
| `depart` | 1–N | `depart_id` | Départ concerné |
| `ref_siege_categorie` | N–1 | `ref_siege_categorie_id` | Catégorie de siège |
| `ref_passager_categorie` | N–1 | `ref_passager_categorie_id` | Catégorie de passager |
| `reservation_passager` | utilise | `ref_passager_categorie_id` | Catégorie du passager réservé |

### 1.4 Règles de calcul

```
Calcul de la remise :
  SI type_remise = 'VALEUR' :
      remise = montant
  SI type_remise = 'POURCENT' :
      remise = tarif_siege * (montant / 100)

Montant final passager :
  montant_tarif = tarif_siege - remise

Contrainte :
  - Pas de remise pour ADULTE (catégorie par défaut)
  - Remise ENFANT : généralement -10% à -50%
  - Remise SENIOR : généralement -10% à -20%
```

---

## 2. Dessins d'écran (ASCII)

### 2.1 Liste des catégories passagers (Admin)
```
┌─────────────────────────────────────────────────────────────────────┐
│                 CATÉGORIES DE PASSAGERS                             │
├───────┬──────────┬───────────────────────┬──────────────────────────┤
│  ID   │   Code   │       Libellé         │        Actions           │
├───────┼──────────┼───────────────────────┼──────────────────────────┤
│   1   │ ENFANT   │ Enfant (0-12 ans)     │  [✏️ Modifier]           │
├───────┼──────────┼───────────────────────┼──────────────────────────┤
│   2   │ ADULTE   │ Adulte                │  [✏️ Modifier]           │
├───────┼──────────┼───────────────────────┼──────────────────────────┤
│   3   │ SENIOR   │ Senior (+60 ans)      │  [✏️ Modifier]           │
└───────┴──────────┴───────────────────────┴──────────────────────────┘
│                                           [ + Ajouter catégorie ]   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Configuration des remises pour un départ
```
┌─────────────────────────────────────────────────────────────────────┐
│              REMISES - DÉPART DEP-2026-01-30-001                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Trajet: Tana → Antsirabe | Date: 30/01/2026 08:00                  │
│                                                                     │
│  ═══════════════════════════════════════════════════════════════    │
│  │ Catégorie Siège │ Cat. Passager │ Type    │ Montant │ Aperçu │   │
│  ═══════════════════════════════════════════════════════════════    │
│  │ VIP (75 000)    │ ENFANT        │ POURCENT│  [20]%  │-15 000 │   │
│  │ VIP (75 000)    │ SENIOR        │ VALEUR  │[10 000] │-10 000 │   │
│  ├─────────────────┼───────────────┼─────────┼─────────┼────────┤   │
│  │ PREMIUM (60 000)│ ENFANT        │ POURCENT│  [20]%  │-12 000 │   │
│  │ PREMIUM (60 000)│ SENIOR        │ VALEUR  │[8 000]  │-8 000  │   │
│  ├─────────────────┼───────────────┼─────────┼─────────┼────────┤   │
│  │ STANDARD (50000)│ ENFANT        │ POURCENT│  [20]%  │-10 000 │   │
│  │ STANDARD (50000)│ SENIOR        │ VALEUR  │[5 000]  │-5 000  │   │
│  ═══════════════════════════════════════════════════════════════    │
│                                                                     │
│                    [ ENREGISTRER ]    [ ANNULER ]                   │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 Modale ajout/modification remise
```
┌─────────────────────────────────────────────────────────┐
│              CONFIGURER REMISE                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Départ:            DEP-2026-01-30-001                  │
│                                                         │
│  Catégorie siège:   [▼ VIP_________________]            │
│                                                         │
│  Catégorie passager:[▼ ENFANT______________]            │
│                                                         │
│  Type de remise:    ○ Valeur fixe (MGA)                 │
│                     ● Pourcentage (%)                   │
│                                                         │
│  Montant:           [______20______]                    │
│                                                         │
│  ─────────────────────────────────────────              │
│  Aperçu:                                                │
│  Tarif VIP:         75 000 MGA                          │
│  Remise (20%):     -15 000 MGA                          │
│  Prix final:        60 000 MGA                          │
│  ─────────────────────────────────────────              │
│                                                         │
│           [ ENREGISTRER ]    [ ANNULER ]                │
└─────────────────────────────────────────────────────────┘
```

### 2.4 Sélection catégorie lors de la réservation
```
┌─────────────────────────────────────────────────────────────────────┐
│                    INFORMATIONS PASSAGER                            │
│                    Siège N°5 (PREMIUM - 60 000 MGA)                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Nom       [_____RAKOTO_______________]                             │
│                                                                     │
│  Prénom    [_____Jean_________________]                             │
│                                                                     │
│  Catégorie passager:                                                │
│                                                                     │
│    ○ ADULTE  - Plein tarif         →  60 000 MGA                    │
│    ● ENFANT  - Remise 20%          →  48 000 MGA  (-12 000)         │
│    ○ SENIOR  - Remise 8 000 MGA    →  52 000 MGA  (-8 000)          │
│                                                                     │
│  ─────────────────────────────────────────────────────              │
│  Tarif appliqué: 48 000 MGA                                         │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Métier / Logique applicative

### 3.1 Classes utilisées

| Classe | Responsabilité | Emplacement |
|--------|---------------|-------------|
| `DepartTarifRemiseController` | CRUD remises par départ | Controller |
| `ReferenceController` | Liste des catégories passager | Controller |
| `DepartTarifRemiseService` | Logique des remises | Service |
| `ReservationService` | Application des remises | Service |

### 3.2 Méthodes principales

#### DepartTarifRemiseService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findByDepartId` | `Long departId` | `List<DepartTarifRemiseDTO>` | Remises d'un départ |
| `findByDepartAndCategories` | `departId`, `siegeCatId`, `passagerCatId` | `DepartTarifRemise` | Remise spécifique |
| `save` | `DepartTarifRemiseDTO` | `DepartTarifRemiseDTO` | Crée/modifie une remise |
| `delete` | `Long id` | `void` | Supprime une remise |

#### ReservationService (calcul remise)
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `calculerRemise` | `tarifSiege`, `DepartTarifRemise` | `BigDecimal` | Calcule montant remise |
| `calculerMontantPassager` | `Depart`, `numeroSiege`, `passagerCategorie` | `BigDecimal` | Tarif final passager |
| `loadRemisesByDepart` | `Long departId` | `Map<Long, Map<Long, DepartTarifRemise>>` | Cache des remises |

### 3.3 Logique de calcul

```java
// Dans ReservationService
public BigDecimal calculerRemise(BigDecimal tarifSiege, DepartTarifRemise remise) {
    if (remise == null) {
        return BigDecimal.ZERO;
    }
    
    if ("POURCENT".equals(remise.getTypeRemise())) {
        // Remise en pourcentage
        return tarifSiege.multiply(remise.getMontant())
                         .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    } else {
        // Remise en valeur fixe
        return remise.getMontant();
    }
}

public BigDecimal calculerMontantPassager(Depart depart, Integer siege, 
                                          RefPassagerCategorie passagerCat) {
    // 1. Déterminer catégorie siège
    RefSiegeCategorie siegeCat = getSiegeCategorie(depart, siege);
    
    // 2. Récupérer tarif de base
    BigDecimal tarif = getTarifSiege(depart, siegeCat);
    
    // 3. Chercher remise applicable
    DepartTarifRemise remise = findRemise(depart.getId(), siegeCat.getId(), passagerCat.getId());
    
    // 4. Calculer et retourner montant final
    BigDecimal remiseAmount = calculerRemise(tarif, remise);
    return tarif.subtract(remiseAmount);
}
```

### 3.4 Règles métier

```
1. Création d'une remise :
   - Combinaison unique (depart_id, siege_categorie_id, passager_categorie_id)
   - Type VALEUR : montant en MGA
   - Type POURCENT : valeur entre 0 et 100

2. Application d'une remise :
   - Lors de la saisie passager dans réservation
   - Catégorie ADULTE = pas de remise (tarif plein)
   - Si pas de remise configurée = tarif plein

3. Recalcul automatique :
   - Modification d'une remise → recalcul des réservations existantes
   - Modification tarif siège → recalcul avec remises
```

### 3.5 Services impactés
- `DepartTarifRemiseService`
- `ReservationService`
- `SiegeConfigurationService`
