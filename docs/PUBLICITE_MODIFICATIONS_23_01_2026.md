# Modifications Système Publicité - 23 Janvier 2026

## Table des matières
1. [MCD des tables impactées](#1-mcd-des-tables-impactées)
2. [Maquettes ASCII des pages](#2-maquettes-ascii-des-pages)
3. [Architecture métier](#3-architecture-métier)

---

## 1. MCD des tables impactées

### Tables existantes modifiées

```
┌─────────────────────────────────────────────────────────────────┐
│                     PAIEMENT_PUBLICITE                          │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                    : BIGINT                              │
│ FK  societe_publicitaire_id : BIGINT                            │
│ FK  ref_devise_id         : BIGINT                              │
│     montant               : DOUBLE PRECISION                    │
│     date_paiement         : TIMESTAMP                           │
│     note                  : VARCHAR(500)                        │
│ NEW facture_mois          : INTEGER                             │
│ NEW facture_annee         : INTEGER                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ N
                              │
                              │ 1
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   SOCIETE_PUBLICITAIRE                          │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                    : BIGINT                              │
│     code                  : VARCHAR(50)                         │
│     nom                   : VARCHAR(200)                        │
│     contact               : VARCHAR(200)                        │
│     is_active             : BOOLEAN                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 1
                              │
                              │ N
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    DEPART_PUBLICITE                             │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                    : BIGINT                              │
│ FK  societe_publicitaire_id : BIGINT                            │
│ FK  depart_id             : BIGINT                              │
│ FK  publicite_id          : BIGINT                              │
│ FK  ref_devise_id         : BIGINT                              │
│     date_diffusion        : TIMESTAMP                           │
│     nombre_repetitions    : INTEGER                             │
│     montant_facture       : DOUBLE PRECISION                    │
│     statut_diffusion      : VARCHAR(20)  -- PLANIFIE/DIFFUSE/ANNULE │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ N
                              │
                              │ 1
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         DEPART                                  │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                    : BIGINT                              │
│     code                  : VARCHAR(50)                         │
│ FK  lieu_depart_id        : BIGINT                              │
│ FK  lieu_arrivee_id       : BIGINT                              │
│ FK  vehicule_id           : BIGINT                              │
│     date_heure_depart     : TIMESTAMP                           │
│     chiffre_affaires      : DOUBLE PRECISION                    │
│     chiffre_affaires_max  : DOUBLE PRECISION                    │
│     statut                : VARCHAR(20)                         │
└─────────────────────────────────────────────────────────────────┘
```

### Cardinalités

```
SOCIETE_PUBLICITAIRE ──(1,N)── PAIEMENT_PUBLICITE
    Une société peut avoir plusieurs paiements
    Un paiement appartient à une seule société

SOCIETE_PUBLICITAIRE ──(1,N)── DEPART_PUBLICITE
    Une société peut avoir plusieurs diffusions
    Une diffusion appartient à une seule société

DEPART ──(1,N)── DEPART_PUBLICITE
    Un départ peut avoir plusieurs diffusions publicitaires
    Une diffusion concerne un seul départ
```

---

## 2. Maquettes ASCII des pages

### 2.1 Page Liste des Départs (departs.html)

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│  GESTION DES DÉPARTS                                                         [+ Nouveau]    │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────────────────┐   │
│  │ Code │ Trajet │ Lieu départ │ Lieu arrivée │ Véhicule │ Date/Heure │ CA │ Montant  │   │
│  │      │        │             │              │          │            │    │ pubs     │   │
│  ├─────────────────────────────────────────────────────────────────────────────────────┤   │
│  │ D001 │ TNR-MJN│ Tana        │ Majunga      │ V123     │ 23/01 8h   │15K│ 5,000 Ar │   │
│  │      │        │             │              │          │            │   │          │   │
│  ├─────────────────────────────────────────────────────────────────────────────────────┤   │
│  │ CA max │ Total CA max + pubs │ Total réel (pubs + CA) │ Statut                     │   │
│  ├─────────────────────────────────────────────────────────────────────────────────────┤   │
│  │ 20,000 │ 25,000 Ar          │ 20,000 Ar              │ [TERMINE]                  │   │
│  └─────────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘

Nouvelles colonnes ajoutées:
- Lieu départ : Nom du lieu de départ (via relation)
- Lieu arrivée : Nom du lieu d'arrivée (via relation)
- Montant pubs : Somme des montants de diffusions (PLANIFIE + DIFFUSE, exclu ANNULE)
- Total CA max + pubs : chiffreAffairesMax + montantDiffusionsPublicite
- Total réel : chiffreAffaires + montantDiffusionsPublicite
```

### 2.2 Page Paiements Publicité (paiements-publicite.html)

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│  PAIEMENTS PUBLICITÉ                                                                        │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐    │
│  │ Sélection de la société                                                            │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │  Société: [▼ SOC01 - Coca Cola     ]  Mois: [▼ Janvier]  Année: [2026]           │    │
│  │                                                              [🔍 Charger]           │    │
│  └────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐                │
│  │  Total facturé      │  │  Déjà payé          │  │  Reste à payer      │                │
│  │  (bg-info/bleu)     │  │  (bg-success/vert)  │  │  (bg-warning/jaune) │                │
│  ├─────────────────────┤  ├─────────────────────┤  ├─────────────────────┤                │
│  │  150,000 Ar         │  │  100,000 Ar         │  │  50,000 Ar          │                │
│  │  Janvier 2026       │  │  paiements reçus    │  │  sur total facturé  │                │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘                │
│                                                                                             │
│  ┌─────────────────────┐  ┌─────────────────────┐                                          │
│  │  Pourcentage déjà   │  │  Pourcentage reste  │                                          │
│  │  payé (bg-primary)  │  │  à payer (bg-danger)│                                          │
│  ├─────────────────────┤  ├─────────────────────┤                                          │
│  │  67%                │  │  33%                │                                          │
│  │  sur total facturé  │  │  sur total facturé  │                                          │
│  └─────────────────────┘  └─────────────────────┘                                          │
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐    │
│  │ Détails des diffusions (période)                                                   │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ Date        │ Départ │ Publicité      │ Répétitions │ Montant    │ Statut         │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ 23/01 08:00 │ D001   │ Promo Coca 500ml│ [3]        │ 10,000 Ar  │ DIFFUSE        │    │
│  │ 23/01 14:30 │ D002   │ Promo Coca 1L   │ [2]        │ 15,000 Ar  │ PLANIFIE       │    │
│  └────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐    │
│  │ Historique des paiements                                        [+ Nouveau paiement]│    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ Date             │ Montant      │ Note                                             │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ 20/01/26 10:00   │ 50,000 Ar    │ Paiement partiel janvier                         │    │
│  │ 15/01/26 14:30   │ 50,000 Ar    │ Acompte                                          │    │
│  └────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Modal Nouveau Paiement

```
┌──────────────────────────────────────────────┐
│  Nouveau paiement                        [×] │
├──────────────────────────────────────────────┤
│                                              │
│  Montant *                                   │
│  [                                     ]     │
│                                              │
│  Date paiement                               │
│  [                                     ]     │
│                                              │
│  Note                                        │
│  [                                     ]     │
│  [                                     ]     │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ℹ️ Paiement partiel autorisé.          │ │
│  │ Le solde sera recalculé sur toutes    │ │
│  │ les diffusions non annulées.          │ │
│  └────────────────────────────────────────┘ │
│                                              │
│               [Annuler]  [💾 Enregistrer]    │
│                                              │
└──────────────────────────────────────────────┘
```

---

## 3. Architecture métier

### 3.1 Entités modifiées

#### PaiementPublicite.java
```java
@Entity
public class PaiementPublicite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "societe_publicitaire_id")
    private SocietePublicitaire societePublicitaire;
    
    @ManyToOne
    @JoinColumn(name = "ref_devise_id")
    private RefDevise refDevise;
    
    private Double montant;
    private LocalDateTime datePaiement;
    private String note;
    
    // NOUVEAUX CHAMPS
    private Integer factureMois;    // 1-12
    private Integer factureAnnee;   // ex: 2026
}
```

### 3.2 Repositories modifiés

#### DepartPubliciteRepository.java

**Méthodes existantes modifiées:**

| Méthode | Type retour | Arguments | Changement |
|---------|------------|-----------|------------|
| `sumMontantFactureBySocieteId` | `Double` | `Long societeId` | AVANT: filtre `= 'DIFFUSE'` → APRÈS: `<> 'ANNULE'` |

**Nouvelles méthodes ajoutées:**

| Méthode | Type retour | Arguments |
|---------|------------|-----------|
| `sumMontantFactureBySocieteIdAndPeriode` | `Double` | `Long societeId, Integer mois, Integer annee` |
| `findBySocieteIdAndDateDiffusionBetween` | `List<DepartPublicite>` | `Long societeId, Integer mois, Integer annee` |
| `sumMontantFactureByDepartId` | `Double` | `Long departId` |

#### PaiementPubliciteRepository.java

**Nouvelles méthodes ajoutées:**

| Méthode | Type retour | Arguments |
|---------|------------|-----------|
| `findBySocieteIdAndPeriode` | `List<PaiementPublicite>` | `Long societeId, Integer mois, Integer annee` |
| `sumMontantBySocieteIdAndPeriode` | `Double` | `Long societeId, Integer mois, Integer annee` |

### 3.3 Services modifiés

#### PaiementPubliciteService.java

**Nouvelles méthodes:**

| Méthode | Type retour | Arguments | Description |
|---------|------------|-----------|-------------|
| `getResumeByPeriode` | `ResumePaiementDTO` | `Long societeId, Integer mois, Integer annee` | Calcule totaux facturé/payé/restant pour période |
| `listDiffusionsBySocieteAndPeriode` | `List<DepartPubliciteDTO>` | `Long societeId, Integer mois, Integer annee` | Liste diffusions d'une société pour période |

**Méthode modifiée:**

| Méthode | Type retour | Arguments | Changement |
|---------|------------|-----------|------------|
| `create` | `PaiementPubliciteDTO` | `PaiementPubliciteDTO dto` | Ajout validation montant vs reste à payer période, ajout assignation factureMois/Annee |

#### DepartService.java

**Méthode modifiée:**

| Méthode | Type retour | Arguments | Changement |
|---------|------------|-----------|------------|
| `enrichDepartDTO` | `void` | `DepartDTO dto` | Ajout calcul montantDiffusionsPublicite, récupération devise depuis diffusions |

### 3.4 DTOs modifiés

#### DepartDTO.java

**Nouveaux champs ajoutés:**

```java
public class DepartDTO {
    // ... champs existants ...
    
    // NOUVEAUX CHAMPS PUBLICITÉ
    private String lieuDepartNom;
    private String lieuArriveeNom;
    private Double montantDiffusionsPublicite;
    private String montantDiffusionsPubliciteDeviseCode;
    private String montantDiffusionsPubliciteDeviseSymbole;
    
    // Getters/Setters
}
```

#### PaiementPubliciteDTO.java

**Nouveaux champs ajoutés:**

```java
public class PaiementPubliciteDTO {
    // ... champs existants ...
    
    // NOUVEAUX CHAMPS PÉRIODE
    private Integer factureMois;    // 1-12
    private Integer factureAnnee;   // ex: 2026
    
    // Getters/Setters
}
```

#### ResumePaiementDTO.java (NOUVEAU)

```java
@Data
@Builder
public class ResumePaiementDTO {
    private Double montantTotalFacture;  // Total facturé (diffusions non annulées)
    private Double montantTotalPaye;     // Total déjà payé
    private Double montantRestant;       // Reste à payer
    private String deviseCode;           // Ex: "AR"
    private String deviseSymbole;        // Ex: "Ar"
}
```

### 3.5 Controllers modifiés

#### PaiementPubliciteController.java

**Nouveaux endpoints:**

| HTTP | Endpoint | Méthode | Type retour | Arguments |
|------|----------|---------|-------------|-----------|
| GET | `/api/paiements-publicite/societe/{societeId}/resume` | `getResume` | `ResponseEntity<ResumePaiementDTO>` | `Long societeId, Integer mois, Integer annee` |
| GET | `/api/paiements-publicite/societe/{societeId}/diffusions` | `getDiffusionsByPeriode` | `ResponseEntity<List<DepartPubliciteDTO>>` | `Long societeId, Integer mois, Integer annee` |
| GET | `/api/paiements-publicite/societe/{societeId}` | `getBySocieteAndPeriode` | `ResponseEntity<List<PaiementPubliciteDTO>>` | `Long societeId, Integer mois, Integer annee` |
@GetMapping("/societe/{societeId}/diffusions")
public ResponseEntity<List<DepartPubliciteDTO>> getDiffusionsByPeriode(
        @PathVariable Long societeId,
        @RequestParam(required = false) Integer mois,
        @RequestParam(required = false) Integer annee) {
    
    List<DepartPubliciteDTO> diffusions = paiementPubliciteService
        .listDiffusionsBySocieteAndPeriode(societeId, mois, annee);
    
    return ResponseEntity.ok(diffusions);
}

/**
 * GET /api/paiements-publicite/societe/{societeId}
 * Liste des paiements par période (modifié pour supporter filtrage)
 * @param societeId ID de la société
 * @param mois Mois de la facture (optionnel)
 * @param annee Année de la facture (optionnel)
 * @return Liste de PaiementPubliciteDTO
 */
@GetMapping("/societe/{societeId}")
public ResponseEntity<List<PaiementPubliciteDTO>> getBySocieteAndPeriode(
        @PathVariable Long societeId,
        @RequestParam(required = false) Integer mois,
        @RequestParam(required = false) Integer annee) {
    
    // Si mois/annee fournis, filtrer par période
    // Sinon, retourner tous les paiements de la société
    List<PaiementPubliciteDTO> paiements = paiementPubliciteService
        .listBySocieteAndPeriode(societeId, mois, annee);
    
    return ResponseEntity.ok(paiements);
}
```

---

## 4. Logique métier clé

**AVANT:**
```
Montant total facturé = SUM(montant_facture) WHERE statut_diffusion = 'DIFFUSE'
```

**APRÈS:**
```
Montant total facturé = SUM(montant_facture) WHERE statut_diffusion <> 'ANNULE'
```

**Impact:** Les diffusions PLANIFIE sont maintenant incluses dans les totaux. Seules les diffusions ANNULE sont exclues.

### 4.2 Facturation mensuelle

**Principe:**
- Chaque paiement est associé à une période (mois/année) via `facture_mois` et `facture_annee`
- Les diffusions sont regroupées par mois de `date_diffusion` (EXTRACT MONTH/YEAR)
- Le résumé calcule: Total facturé = diffusions du mois, Total payé = paiements du mois

**Formules:**
```
Total facturé (période) = SUM(montant_facture) 
                          WHERE EXTRACT(MONTH FROM date_diffusion) = mois
                          AND EXTRACT(YEAR FROM date_diffusion) = annee
                          AND statut_diffusion <> 'ANNULE'

Total payé (période) = SUM(montant)
                       WHERE facture_mois = mois
                       AND facture_annee = annee

Reste à payer = Total facturé - Total payé

% Payé = (Total payé / Total facturé) * 100
% Restant = (Reste à payer / Total facturé) * 100
```

### 4.3 Validation paiements

**Règles:**
1. Un paiement ne peut pas dépasser le reste à payer de la période
2. Si pas de période spécifiée, validation sur le total global
3. Paiements partiels autorisés (montant < reste à payer)
4. Montant doit être > 0

**Code de validation:**
```java
if (montantPaiement > resteAPayer) {
    throw new IllegalArgumentException(
        "Montant dépasse le reste à payer de " + resteAPayer + " " + devise
    );
}
```

### 4.4 Enrichissement départs

**Processus:**
1. Récupérer le départ depuis la base
2. Calculer `montantDiffusionsPublicite` = SUM des diffusions non annulées
3. Récupérer la devise depuis la première diffusion du départ
4. Calculer les totaux:
   - `Total CA max + pubs` = chiffreAffairesMax + montantDiffusionsPublicite
   - `Total réel` = chiffreAffaires + montantDiffusionsPublicite

---

## 5. Pages supprimées

Les pages suivantes ont été supprimées et leurs fonctionnalités intégrées dans `paiements-publicite.html`:

1. **Page statistiques CA** (`/statistiques-publicite`)
   - Remplacée par: Cartes de pourcentage dans paiements-publicite.html
   
2. **Page nouveau paiement** (`/paiement-publicite` POST form)
   - Remplacée par: Modal dans paiements-publicite.html

---

## 6. Migration base de données

### Script SQL requis

```sql
-- Ajout des colonnes de période dans paiement_publicite
ALTER TABLE paiement_publicite 
ADD COLUMN facture_mois INTEGER,
ADD COLUMN facture_annee INTEGER;

-- Index pour optimiser les requêtes par période
CREATE INDEX idx_paiement_publicite_periode 
ON paiement_publicite(societe_publicitaire_id, facture_mois, facture_annee);

-- Commentaires
COMMENT ON COLUMN paiement_publicite.facture_mois IS 'Mois de la facture (1-12)';
COMMENT ON COLUMN paiement_publicite.facture_annee IS 'Année de la facture (ex: 2026)';
```

---

## 7. Résumé des couleurs UI

```
┌─────────────────────────────────────────────────────────────┐
│  Cartes de la page paiements-publicite.html                 │
├─────────────────────────────────────────────────────────────┤
│  1. Total facturé          │ bg-info     │ Bleu clair      │
│  2. Déjà payé              │ bg-success  │ Vert            │
│  3. Reste à payer          │ bg-warning  │ Jaune/Orange    │
│  4. Pourcentage déjà payé  │ bg-primary  │ Bleu foncé      │
│  5. Pourcentage reste      │ bg-danger   │ Rouge           │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. Tests recommandés

### 8.1 Tests unitaires

```java
@Test
public void testCalculMontantFactureExcluAnnule() {
    // Given: 1 diffusion DIFFUSE + 1 PLANIFIE + 1 ANNULE
    // When: sumMontantFactureBySocieteId
    // Then: Retourne somme DIFFUSE + PLANIFIE uniquement
}

@Test
public void testResumeByPeriode() {
    // Given: 3 diffusions janvier, 2 paiements janvier
    // When: getResumeByPeriode(societeId, 1, 2026)
    // Then: Total facturé = somme diffusions janvier
    //       Total payé = somme paiements janvier
    //       Reste = différence
}

@Test
public void testPaiementDepasseReste() {
    // Given: Reste à payer = 1000
    // When: Créer paiement de 1500
    // Then: Lever IllegalArgumentException
}
```

### 8.2 Tests intégration

1. **Scénario facture mensuelle complète:**
   - Créer société
   - Créer 5 diffusions pour janvier 2026
   - Vérifier total facturé
   - Créer 2 paiements partiels
   - Vérifier reste à payer
   - Créer paiement final
   - Vérifier solde = 0

2. **Scénario multi-période:**
   - Créer diffusions janvier et février
   - Créer paiements mixtes (certains avec période, d'autres sans)
   - Vérifier filtrage par période
   - Vérifier totaux globaux

3. **Scénario départs avec pubs:**
   - Créer départ avec plusieurs diffusions
   - Vérifier montantDiffusionsPublicite calculé
   - Vérifier totaux CA + pubs
   - Annuler une diffusion
   - Vérifier recalcul des totaux

---

## 9. Points d'attention

### 9.1 Performance

- Les requêtes EXTRACT(MONTH/YEAR) peuvent être lentes sur grandes tables
- Recommandé: Créer index sur `(date_diffusion, statut_diffusion)` pour `depart_publicite`
- Recommandé: Créer index composite sur `(societe_publicitaire_id, facture_mois, facture_annee)` pour `paiement_publicite`

### 9.2 Migration données existantes

- Les paiements existants auront `facture_mois` et `facture_annee` NULL
- Script de migration recommandé pour assigner période basée sur `date_paiement`:

```sql
UPDATE paiement_publicite
SET facture_mois = EXTRACT(MONTH FROM date_paiement),
    facture_annee = EXTRACT(YEAR FROM date_paiement)
WHERE facture_mois IS NULL 
  AND date_paiement IS NOT NULL;
```

### 9.3 Règles métier

- **Changement important:** Inclusion des PLANIFIE dans totaux peut créer décalage avec ancien système
- Validation requise sur montants existants après migration
- Documenter clairement la nouvelle règle aux utilisateurs

---

**Date de création:** 23 Janvier 2026  
**Version:** 1.0  
**Auteur:** Documentation automatique des modifications système publicité
