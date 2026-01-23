# Modifications Système Publicité - 23 Janvier 2026 (Variante)

## Table des matières
1. [MCD des tables impactées](#1-mcd-des-tables-impactées)
2. [Maquettes ASCII des pages](#2-maquettes-ascii-des-pages)
3. [Architecture métier](#3-architecture-métier)

---

## 1. MCD des tables impactées

### Tables existantes modifiées

```
┌─────────────────────────────────────────────────────────────────┐
│                     REGLEMENT_PUBLICITE                         │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                     : BIGINT                             │
│ FK  annonceur_id           : BIGINT                             │
│ FK  devise_ref_id          : BIGINT                             │
│     montant_regle          : NUMERIC(15,2)                      │
│     date_reglement         : TIMESTAMP                          │
│     commentaire            : VARCHAR(400)                       │
│ NEW facture_mois           : INTEGER                            │
│ NEW facture_annee          : INTEGER                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ N
                              │
                              │ 1
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     ANNONCEUR                                   │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                     : BIGINT                             │
│     code                   : VARCHAR(40)                        │
│     raison_sociale         : VARCHAR(200)                       │
│     contact_principal      : VARCHAR(160)                       │
│     est_actif              : BOOLEAN                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 1
                              │
                              │ N
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   DIFFUSION_ANNONCE                             │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                     : BIGINT                             │
│ FK  annonceur_id           : BIGINT                             │
│ FK  voyage_id              : BIGINT                             │
│ FK  spot_id                : BIGINT                             │
│ FK  devise_ref_id          : BIGINT                             │
│     date_programmation     : TIMESTAMP                          │
│     nb_passages            : INTEGER                            │
│     montant_ligne          : NUMERIC(15,2)                      │
│     etat_diffusion         : VARCHAR(20) -- PREVU/LIVRE/ANNULE   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ N
                              │
                              │ 1
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          VOYAGE                                 │
├─────────────────────────────────────────────────────────────────┤
│ PK  id                     : BIGINT                             │
│     code                   : VARCHAR(50)                        │
│ FK  lieu_depart_id         : BIGINT                             │
│ FK  lieu_arrivee_id        : BIGINT                             │
│ FK  bus_id                 : BIGINT                             │
│     date_heure_depart      : TIMESTAMP                          │
│     chiffre_affaires       : NUMERIC(15,2)                      │
│     chiffre_affaires_max   : NUMERIC(15,2)                      │
│     statut                 : VARCHAR(20)                        │
└─────────────────────────────────────────────────────────────────┘
```

### Cardinalités

```
ANNONCEUR ──(1,N)── REGLEMENT_PUBLICITE
    Un annonceur peut régler plusieurs fois
    Un règlement appartient à un annonceur

ANNONCEUR ──(1,N)── DIFFUSION_ANNONCE
    Un annonceur a plusieurs diffusions
    Une diffusion concerne un annonceur

VOYAGE ──(1,N)── DIFFUSION_ANNONCE
    Un voyage diffuse plusieurs annonces
    Une diffusion concerne un voyage
```

---

## 2. Maquettes ASCII des pages

### 2.1 Page Liste des Voyages (voyages.html)

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│  LISTE DES VOYAGES                                                            [+ Nouveau]    │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────────────────┐   │
│  │ Code │ Trajet │ Départ │ Arrivée │ Bus │ Date/Heure │ CA │ Montant pubs             │   │
│  ├─────────────────────────────────────────────────────────────────────────────────────┤   │
│  │ V001 │ TNR-FNR│ Tana   │ Fianara │ B12 │ 23/01 8h   │15K│ 6,000 Ar                │   │
│  └─────────────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                             │
│  CA max + pubs  |  Total réel (pubs + CA)  |  Statut                                      │
└─────────────────────────────────────────────────────────────────────────────────────────────┘

Nouvelles colonnes proposées:
- Montant pubs : somme des diffusions (PREVU + LIVRE, exclu ANNULE)
- Total réel : chiffre_affaires + montant pubs
```

### 2.2 Page Règlements Publicité (reglements-publicite.html)

```
┌─────────────────────────────────────────────────────────────────────────────────────────────┐
│  RÈGLEMENTS PUBLICITÉ                                                                       │
├─────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐    │
│  │ Sélection annonceur                                                               │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │  Annonceur: [▼ AN01 - Orange       ]  Mois: [▼ Janvier]  Année: [2026]           │    │
│  │                                                          [🔍 Charger]             │    │
│  └────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐                │
│  │  Total facturé      │  │  Déjà réglé         │  │  Reste à régler     │                │
│  │  (bg-info/bleu)     │  │  (bg-success/vert)  │  │  (bg-warning/orange)│                │
│  │  180,000 Ar         │  │  120,000 Ar         │  │  60,000 Ar          │                │
│  └─────────────────────┘  └─────────────────────┘  └─────────────────────┘                │
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐    │
│  │ Détails des diffusions (période)                                                   │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ Date        │ Voyage │ Spot        │ Passages │ Montant    │ Statut                 │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ 23/01 08:00 │ V001   │ Promo 4G    │ [3]      │ 12,000 Ar  │ LIVRE                  │    │
│  │ 23/01 14:30 │ V002   │ Promo Box   │ [2]      │ 18,000 Ar  │ PREVU                  │    │
│  └────────────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                             │
│  ┌────────────────────────────────────────────────────────────────────────────────────┐    │
│  │ Historique des règlements                                      [+ Nouveau règlement]│    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ Date             │ Montant      │ Commentaire                                 │    │
│  ├────────────────────────────────────────────────────────────────────────────────────┤    │
│  │ 20/01/26 10:00   │ 60,000 Ar    │ Règlement partiel janvier                   │    │
│  │ 15/01/26 14:30   │ 60,000 Ar    │ Acompte                                    │    │
│  └────────────────────────────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Modal Nouveau Règlement

```
┌──────────────────────────────────────────────┐
│  Nouveau règlement                      [×] │
├──────────────────────────────────────────────┤
│                                              │
│  Montant *                                   │
│  [                                     ]     │
│                                              │
│  Date règlement                              │
│  [                                     ]     │
│                                              │
│  Commentaire                                 │
│  [                                     ]     │
│  [                                     ]     │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ℹ️ Règlement partiel autorisé.         │ │
│  │ Le solde est recalculé automatiquement │ │
│  └────────────────────────────────────────┘ │
│                                              │
│               [Annuler]  [💾 Enregistrer]    │
└──────────────────────────────────────────────┘
```

---

## 3. Architecture métier

### 3.1 Entités modifiées

#### ReglementPublicite.java
```java
@Entity
public class ReglementPublicite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "annonceur_id")
    private Annonceur annonceur;

    @ManyToOne
    @JoinColumn(name = "devise_ref_id")
    private DeviseRef deviseRef;

    private BigDecimal montantRegle;
    private LocalDateTime dateReglement;
    private String commentaire;

    // NOUVEAUX CHAMPS
    private Integer factureMois;
    private Integer factureAnnee;
}
```

### 3.2 Repositories modifiés

#### DiffusionAnnonceRepository.java

| Méthode | Type retour | Arguments | Rôle |
|---------|------------|-----------|------|
| `sumMontantFactureByAnnonceurId` | `BigDecimal` | `Long annonceurId` | Total facturé hors ANNULÉ |
| `sumMontantFactureByAnnonceurIdAndPeriode` | `BigDecimal` | `Long annonceurId, Integer mois, Integer annee` | Total facturé période |
| `findByAnnonceurIdAndDateBetween` | `List<DiffusionAnnonce>` | `Long annonceurId, LocalDateTime debut, LocalDateTime fin` | Détails diffusions |

#### ReglementPubliciteRepository.java

| Méthode | Type retour | Arguments | Rôle |
|---------|------------|-----------|------|
| `findByAnnonceurIdAndPeriode` | `List<ReglementPublicite>` | `Long annonceurId, Integer mois, Integer annee` | Liste des règlements |
| `sumMontantByAnnonceurIdAndPeriode` | `BigDecimal` | `Long annonceurId, Integer mois, Integer annee` | Total réglé période |

### 3.3 Services modifiés

#### ReglementPubliciteService.java

| Méthode | Type retour | Arguments | Description |
|---------|------------|-----------|-------------|
| `getResumeByPeriode` | `ResumeReglementDTO` | `Long annonceurId, Integer mois, Integer annee` | Facturé / réglé / restant |
| `listDiffusionsByAnnonceurAndPeriode` | `List<DiffusionAnnonceDTO>` | `Long annonceurId, Integer mois, Integer annee` | Détails diffusions |
| `create` | `ReglementPubliciteDTO` | `ReglementPubliciteDTO dto` | Validation du dépassement |

### 3.4 DTOs modifiés

#### VoyageDTO.java
```java
public class VoyageDTO {
    private String lieuDepartNom;
    private String lieuArriveeNom;
    private BigDecimal montantDiffusionsAnnonce;
    private String deviseCode;
    private String deviseSymbole;
}
```

#### ReglementPubliciteDTO.java
```java
public class ReglementPubliciteDTO {
    private Integer factureMois;
    private Integer factureAnnee;
}
```

#### ResumeReglementDTO.java (NOUVEAU)
```java
@Data
@Builder
public class ResumeReglementDTO {
    private BigDecimal montantTotalFacture;
    private BigDecimal montantTotalRegle;
    private BigDecimal montantRestant;
    private String deviseCode;
    private String deviseSymbole;
}
```

### 3.5 Controllers modifiés

#### ReglementPubliciteController.java

| HTTP | Endpoint | Méthode | Type retour |
|------|----------|---------|-------------|
| GET | `/api/reglements-publicite/annonceur/{id}/resume` | getResume | `ResumeReglementDTO` |
| GET | `/api/reglements-publicite/annonceur/{id}` | getByAnnonceur | `List<ReglementPubliciteDTO>` |
| GET | `/api/reglements-publicite/annonceur/{id}/diffusions` | getDiffusions | `List<DiffusionAnnonceDTO>` |

---

## 4. Logique métier clé

**AVANT:**
```
Total facturé = SUM(montant_ligne) WHERE etat_diffusion = 'LIVRE'
```

**APRÈS:**
```
Total facturé = SUM(montant_ligne) WHERE etat_diffusion <> 'ANNULE'
```

### 4.2 Facturation mensuelle

```
Total facturé période = SUM(montant_ligne)
                         WHERE EXTRACT(MONTH FROM date_programmation)=mois
                         AND EXTRACT(YEAR FROM date_programmation)=annee
                         AND etat_diffusion <> 'ANNULE'

Total réglé période = SUM(montant_regle)
                      WHERE facture_mois=mois AND facture_annee=annee

Reste = Total facturé - Total réglé
```

### 4.3 Validation des règlements

- Montant règlement <= reste à payer
- Règlements partiels autorisés
- Montant > 0

---

## 5. Pages supprimées

1. **Statistiques CA** remplacée par **Règlements**
2. **Nouveau paiement** remplacée par **Modal règlement**

---

## 6. Migration base de données

```sql
ALTER TABLE reglement_publicite
ADD COLUMN facture_mois INTEGER,
ADD COLUMN facture_annee INTEGER;

CREATE INDEX idx_reglement_publicite_periode
ON reglement_publicite(annonceur_id, facture_mois, facture_annee);
```

---

## 7. Résumé des couleurs UI

```
┌─────────────────────────────────────────────────────────────┐
│  Cartes de la page reglements-publicite.html                │
├─────────────────────────────────────────────────────────────┤
│  1. Total facturé          │ bg-info     │ Bleu clair        │
│  2. Déjà réglé             │ bg-success  │ Vert              │
│  3. Reste à régler         │ bg-warning  │ Orange            │
└─────────────────────────────────────────────────────────────┘
```

---

## 8. Tests recommandés

1. Règlement partiel puis complet sur période
2. Multi-période avec diffusions et règlements
3. Diffusion annulée exclue des totaux

---

**Date de création:** 23 Janvier 2026  
**Version:** 1.0  
**Auteur:** Document alternatif (variante)
