# Fonctionnalité 04 : Diffusion publicitaire sur les départs

> Gestion des publicités vidéo diffusées pendant les trajets

---

## 1. Architecture données

### 1.1 Tables principales
| Table | Description |
|-------|-------------|
| `societe_publicitaire` | Sociétés qui achètent des espaces publicitaires |
| `publicite` | Vidéos publicitaires créées par les sociétés |
| `tarif_publicite` | Historique des tarifs de diffusion |
| `depart_publicite` | Association publicité ↔ départ (diffusion) |

### 1.2 Colonnes utilisées

#### Table `societe_publicitaire`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `code` | varchar(50) | Code unique |
| `nom` | varchar(200) | Nom de la société |

#### Table `publicite`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `code` | varchar(80) | Code unique |
| `societe_publicitaire_id` | bigint | FK vers societe_publicitaire |
| `titre` | varchar(200) | Titre de la pub |
| `date_debut_validite` | date | Début période de diffusion |
| `date_fin_validite` | date | Fin période de diffusion |

#### Table `tarif_publicite`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `ref_devise_id` | bigint | FK vers ref_devise |
| `montant` | decimal(12,2) | Prix par diffusion |
| `date_debut` | date | Début validité tarif |

#### Table `depart_publicite`
| Colonne | Type | Description |
|---------|------|-------------|
| `id` | bigint | Clé primaire |
| `depart_id` | bigint | FK vers depart |
| `publicite_id` | bigint | FK vers publicite |
| `tarif_publicite_id` | bigint | FK vers tarif_publicite |
| `montant_facture` | decimal(15,2) | Montant calculé |
| `nombre_repetitions` | integer | Nombre de diffusions |

### 1.3 Tables annexes

| Table | Relation | Colonne de liaison | Description |
|-------|----------|-------------------|-------------|
| `depart` | 1–N | `depart_id` | Départ sur lequel diffuser |
| `ref_devise` | N–1 | `ref_devise_id` | Devise du tarif |
| `paiement_publicite` | associé | `societe_publicitaire_id` | Paiements des sociétés |

### 1.4 Règles de calcul

```
Montant facturé = tarif_publicite.montant × nombre_repetitions

Conditions de diffusion :
  - publicite.date_debut_validite <= depart.date_heure_depart
  - publicite.date_fin_validite >= depart.date_heure_depart
  - publicite.actif = true
```

---

## 2. Dessins d'écran (ASCII)

### 2.1 Liste des sociétés publicitaires
```
┌─────────────────────────────────────────────────────────────────────┐
│                 SOCIÉTÉS PUBLICITAIRES                              │
├─────────────────────────────────────────────────────────────────────┤
│  🔍 Rechercher [____________________]                               │
├───────┬──────────────┬────────────────────────┬─────────────────────┤
│  ID   │    Code      │         Nom            │      Actions        │
├───────┼──────────────┼────────────────────────┼─────────────────────┤
│   1   │ SOC-COCA     │ Coca-Cola Madagascar   │ [👁️] [✏️] [🗑️]      │
├───────┼──────────────┼────────────────────────┼─────────────────────┤
│   2   │ SOC-TELMA    │ Telma Mobile           │ [👁️] [✏️] [🗑️]      │
├───────┼──────────────┼────────────────────────┼─────────────────────┤
│   3   │ SOC-BFV      │ BFV-SG Banque          │ [👁️] [✏️] [🗑️]      │
└───────┴──────────────┴────────────────────────┴─────────────────────┘
│                                    [ + Nouvelle société ]           │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Liste des publicités d'une société
```
┌─────────────────────────────────────────────────────────────────────┐
│           PUBLICITÉS - Coca-Cola Madagascar                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │ PUB-COCA-001 │ Promo été 2026        │ 01/01 → 31/03/2026    │  │
│  │              │ Durée: 30s            │ ✅ Active             │  │
│  │              │ Diffusions: 45        │ CA: 4 500 000 MGA     │  │
│  │              │                       │ [📺 Diffuser] [✏️]    │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │ PUB-COCA-002 │ Nouvelle bouteille    │ 15/01 → 15/02/2026    │  │
│  │              │ Durée: 45s            │ ✅ Active             │  │
│  │              │ Diffusions: 12        │ CA: 1 200 000 MGA     │  │
│  │              │                       │ [📺 Diffuser] [✏️]    │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│                              [ + Nouvelle publicité ]               │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 Planification diffusion sur un départ
```
┌─────────────────────────────────────────────────────────────────────┐
│         DIFFUSIONS PUBLICITAIRES - DÉPART DEP-2026-01-30-001        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Trajet: Tana → Antsirabe | Date: 30/01/2026 08:00                  │
│  Durée estimée: 3h00                                                │
│                                                                     │
│  ═══ PUBLICITÉS PLANIFIÉES ═══════════════════════════════════════  │
│  │ Publicité              │ Société   │ Répét. │ Montant      │     │
│  ├────────────────────────┼───────────┼────────┼──────────────┤     │
│  │ Promo été 2026         │ Coca-Cola │   2    │ 200 000 MGA  │ [🗑️]│
│  │ Telma 4G               │ Telma     │   3    │ 300 000 MGA  │ [🗑️]│
│  │ Crédit BFV             │ BFV-SG    │   1    │ 100 000 MGA  │ [🗑️]│
│  ═══════════════════════════════════════════════════════════════    │
│                                                                     │
│  Total diffusions: 6 | Montant total: 600 000 MGA                   │
│                                                                     │
│                    [ + AJOUTER PUBLICITÉ ]                          │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.4 Modale ajout diffusion
```
┌─────────────────────────────────────────────────────────┐
│           AJOUTER UNE DIFFUSION                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Départ: DEP-2026-01-30-001 (Tana → Antsirabe)          │
│                                                         │
│  Publicité:    [▼ Sélectionner une publicité_______]    │
│                                                         │
│  ┌─ Publicités disponibles ─────────────────────────┐   │
│  │ ○ Promo été 2026 (Coca-Cola) - 30s               │   │
│  │ ● Telma 4G (Telma) - 20s                         │   │
│  │ ○ Crédit BFV (BFV-SG) - 45s                      │   │
│  └──────────────────────────────────────────────────┘   │
│                                                         │
│  Nombre de répétitions: [ 2 ]                           │
│                                                         │
│  ─────────────────────────────────────────              │
│  Tarif unitaire:     100 000 MGA                        │
│  Répétitions:        × 2                                │
│  ─────────────────────────────────────────              │
│  MONTANT TOTAL:      200 000 MGA                        │
│                                                         │
│           [ CONFIRMER ]    [ ANNULER ]                  │
└─────────────────────────────────────────────────────────┘
```

### 2.5 Tarifs publicité (Admin)
```
┌─────────────────────────────────────────────────────────────────────┐
│                 TARIFS PUBLICITÉ                                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Tarif actuel: 100 000 MGA / diffusion                              │
│  En vigueur depuis: 01/01/2026                                      │
│                                                                     │
│  ═══ HISTORIQUE DES TARIFS ═════════════════════════════════════    │
│  │ Date début   │ Date fin     │ Montant       │ Statut       │     │
│  ├──────────────┼──────────────┼───────────────┼──────────────┤     │
│  │ 01/01/2026   │ -            │ 100 000 MGA   │ ✅ Actif     │     │
│  │ 01/07/2025   │ 31/12/2025   │ 80 000 MGA    │ ⬛ Expiré    │     │
│  │ 01/01/2025   │ 30/06/2025   │ 75 000 MGA    │ ⬛ Expiré    │     │
│  ════════════════════════════════════════════════════════════════   │
│                                                                     │
│                    [ + NOUVEAU TARIF ]                              │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Métier / Logique applicative

### 3.1 Classes utilisées

| Classe | Responsabilité | Emplacement |
|--------|---------------|-------------|
| `SocietePublicitaireController` | CRUD sociétés | Controller |
| `PubliciteController` | CRUD publicités | Controller |
| `TarifPubliciteController` | CRUD tarifs | Controller |
| `DepartPubliciteController` | CRUD diffusions | Controller |
| `SocietePublicitaireService` | Logique sociétés | Service |
| `PubliciteService` | Logique publicités | Service |
| `TarifPubliciteService` | Logique tarifs | Service |
| `DepartPubliciteService` | Logique diffusions | Service |

### 3.2 Méthodes principales

#### PubliciteService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findAll` | - | `List<PubliciteDTO>` | Toutes les publicités |
| `findBySocieteId` | `Long societeId` | `List<PubliciteDTO>` | Pubs d'une société |
| `findValidesForDate` | `LocalDate date` | `List<PubliciteDTO>` | Pubs valides à une date |
| `save` | `PubliciteDTO` | `PubliciteDTO` | Crée/modifie |

#### TarifPubliciteService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findTarifEnVigueur` | `LocalDate date` | `TarifPublicite` | Tarif actif à une date |
| `findAll` | - | `List<TarifPubliciteDTO>` | Historique tarifs |
| `save` | `TarifPubliciteDTO` | `TarifPubliciteDTO` | Nouveau tarif |

#### DepartPubliciteService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findByDepartId` | `Long departId` | `List<DepartPubliciteDTO>` | Diffusions d'un départ |
| `findByPubliciteId` | `Long publiciteId` | `List<DepartPubliciteDTO>` | Diffusions d'une pub |
| `create` | `DepartPubliciteDTO` | `DepartPubliciteDTO` | Planifie diffusion |
| `delete` | `Long id` | `void` | Annule diffusion |

### 3.3 Logique de création de diffusion

```java
// Dans DepartPubliciteService.create()
public DepartPubliciteDTO create(DepartPubliciteDTO dto) {
    // 1. Récupérer le départ et la publicité
    Depart depart = departRepository.findById(dto.getDepartId());
    Publicite pub = publiciteRepository.findById(dto.getPubliciteId());
    
    // 2. Valider que la pub est active à la date du départ
    LocalDate dateDepart = depart.getDateHeureDepart().toLocalDate();
    if (dateDepart.isBefore(pub.getDateDebutValidite()) ||
        dateDepart.isAfter(pub.getDateFinValidite())) {
        throw new BadRequestException("Publicité non valide pour cette date");
    }
    
    // 3. Récupérer le tarif en vigueur
    TarifPublicite tarif = tarifPubliciteRepository.findTarifEnVigueur(dateDepart);
    
    // 4. Calculer le montant facturé
    BigDecimal montant = tarif.getMontant()
                              .multiply(new BigDecimal(dto.getNombreRepetitions()));
    
    // 5. Créer l'entité
    DepartPublicite diffusion = new DepartPublicite();
    diffusion.setDepart(depart);
    diffusion.setPublicite(pub);
    diffusion.setTarifPublicite(tarif);
    diffusion.setNombreRepetitions(dto.getNombreRepetitions());
    diffusion.setMontantFacture(montant);
    
    return mapper.toDTO(departPubliciteRepository.save(diffusion));
}
```

### 3.4 Règles métier

```
1. Validité publicité :
   - date_debut_validite <= date_depart <= date_fin_validite
   - publicite.actif = true

2. Tarification :
   - Utiliser le tarif en vigueur à la date du départ
   - montant_facture = tarif × nombre_repetitions

3. Contraintes :
   - Une même publicité peut être diffusée plusieurs fois sur un départ
   - Le nombre de répétitions est libre (pas de limite)
```

### 3.5 Services impactés
- `SocietePublicitaireService`
- `PubliciteService`
- `TarifPubliciteService`
- `DepartPubliciteService`
- `ChiffreAffairesStatsService` (calcul CA publicité)
