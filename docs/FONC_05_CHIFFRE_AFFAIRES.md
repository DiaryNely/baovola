# Fonctionnalité 05 : Calcul du chiffre d'affaires par départ

> Agrégation des revenus : réservations + publicités + ventes produits

---

## 1. Architecture données

### 1.1 Tables sources
| Table | Description | Contribution CA |
|-------|-------------|-----------------|
| `depart` | Table centrale | Base du calcul |
| `reservation` | Réservations | CA Réservations |
| `reservation_passager` | Détail passagers | Montants tarifs |
| `paiement` | Paiements reçus | CA Réel réservations |
| `depart_publicite` | Diffusions pub | CA Publicité |
| `stock_depart` | Stock produits | CA Potentiel produits |
| `vente_produit` | Ventes effectuées | CA Réel produits |

### 1.2 Colonnes utilisées pour le calcul

#### Depuis `reservation_passager`
| Colonne | Utilisation |
|---------|-------------|
| `montant_tarif` | Tarif appliqué au passager |
| `montant_remise` | Remise appliquée |

#### Depuis `paiement`
| Colonne | Utilisation |
|---------|-------------|
| `montant` | Montant payé |
| `reservation_id` | Liaison réservation |

#### Depuis `depart_publicite`
| Colonne | Utilisation |
|---------|-------------|
| `montant_facture` | Montant facturé |
| `nombre_repetitions` | Inclus dans montant |

#### Depuis `vente_produit`
| Colonne | Utilisation |
|---------|-------------|
| `montant_total` | Vente effectuée |
| `stock_depart_id` | Liaison au départ |

### 1.3 Règles de calcul

```
══════════════════════════════════════════════════════════════
CA RÉSERVATIONS
══════════════════════════════════════════════════════════════
CA Théorique = Σ (tarif_siege × nb_places) pour chaque catégorie
             = Capacité maximale valorisée

CA Réel      = Σ paiements.montant 
               WHERE paiement.reservation.depart_id = depart.id
               AND paiement.statut = 'VALIDE'

══════════════════════════════════════════════════════════════
CA PUBLICITÉ
══════════════════════════════════════════════════════════════
CA Théorique = Σ depart_publicite.montant_facture
             = Total facturé aux sociétés

CA Réel      = Σ paiement_publicite.montant
               WHERE société a une pub diffusée sur ce départ
               (selon période facturée)

══════════════════════════════════════════════════════════════
CA PRODUITS
══════════════════════════════════════════════════════════════
CA Théorique = Σ (stock_depart.quantite_initiale × prix_unitaire)
             = Valeur du stock initial

CA Réel      = Σ vente_produit.montant_total
               WHERE stock_depart.depart_id = depart.id

══════════════════════════════════════════════════════════════
TOTAUX
══════════════════════════════════════════════════════════════
Total Théorique = CA Réservations Théo + CA Pub Théo + CA Produits Théo
Total Réel      = CA Réservations Réel + CA Pub Réel + CA Produits Réel
```

---

## 2. Dessins d'écran (ASCII)

### 2.1 Dashboard Chiffre d'Affaires
```
┌─────────────────────────────────────────────────────────────────────┐
│                 TABLEAU DE BORD - CHIFFRE D'AFFAIRES                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Période: [▼ Janvier 2026_____]  Année: [▼ 2026]                    │
│                                                                     │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │                    RÉSUMÉ MENSUEL                           │    │
│  ├─────────────────────┬───────────────┬───────────────────────┤    │
│  │   Catégorie         │   Théorique   │        Réel           │    │
│  ├─────────────────────┼───────────────┼───────────────────────┤    │
│  │ 🎫 Réservations     │ 15 000 000    │ 12 500 000 (83%)      │    │
│  │ 📺 Publicités       │  3 200 000    │  2 800 000 (87%)      │    │
│  │ 🛒 Produits         │  1 500 000    │    950 000 (63%)      │    │
│  ├─────────────────────┼───────────────┼───────────────────────┤    │
│  │ TOTAL               │ 19 700 000    │ 16 250 000 (82%)      │    │
│  └─────────────────────┴───────────────┴───────────────────────┘    │
│                                                                     │
│  [▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░] 82% Réalisé             │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Liste des départs avec CA
```
┌─────────────────────────────────────────────────────────────────────┐
│                 DÉPARTS - JANVIER 2026                              │
├─────────────────────────────────────────────────────────────────────┤
│  🔍 Filtrer: [____________]   Statut: [▼ Tous_____]                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│ ┌───────────────────────────────────────────────────────────────┐   │
│ │ DEP-001 | 15/01/2026 08:00 | Tana → Antsirabe                 │   │
│ │ ✅ TERMINÉ | 15/18 places | Véhicule: 1234 TBA                │   │
│ ├───────────────┬───────────────┬───────────────────────────────┤   │
│ │ Réservations  │ Publicités    │ Produits                      │   │
│ │ 750 000 MGA   │ 300 000 MGA   │ 125 000 MGA                   │   │
│ ├───────────────┴───────────────┴───────────────────────────────┤   │
│ │ TOTAL CA: 1 175 000 MGA                          [📊 Détails] │   │
│ └───────────────────────────────────────────────────────────────┘   │
│                                                                     │
│ ┌───────────────────────────────────────────────────────────────┐   │
│ │ DEP-002 | 15/01/2026 14:00 | Tana → Toamasina                 │   │
│ │ 🔵 EN COURS | 12/20 places | Véhicule: 5678 TBA               │   │
│ ├───────────────┬───────────────┬───────────────────────────────┤   │
│ │ Réservations  │ Publicités    │ Produits                      │   │
│ │ 480 000 MGA   │ 200 000 MGA   │ 50 000 MGA                    │   │
│ ├───────────────┴───────────────┴───────────────────────────────┤   │
│ │ TOTAL CA: 730 000 MGA                            [📊 Détails] │   │
│ └───────────────────────────────────────────────────────────────┘   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.3 Détail CA d'un départ
```
┌─────────────────────────────────────────────────────────────────────┐
│         DÉTAIL CHIFFRE D'AFFAIRES - DEP-2026-01-15-001              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  Trajet: Tana → Antsirabe | Date: 15/01/2026 08:00                  │
│  Véhicule: 1234 TBA (18 places) | Statut: ✅ TERMINÉ                │
│                                                                     │
│  ═══ RÉSERVATIONS ══════════════════════════════════════════════    │
│  │ Catégorie │ Places │ Tarif    │ CA Théo    │ Vendues │ CA Réel│  │
│  ├───────────┼────────┼──────────┼────────────┼─────────┼────────┤  │
│  │ VIP       │   3    │ 75 000   │   225 000  │    3    │225 000 │  │
│  │ PREMIUM   │   6    │ 60 000   │   360 000  │    5    │285 000 │  │
│  │ STANDARD  │   9    │ 50 000   │   450 000  │    7    │320 000 │  │
│  ├───────────┼────────┼──────────┼────────────┼─────────┼────────┤  │
│  │ TOTAL     │  18    │    -     │ 1 035 000  │   15    │830 000 │  │
│  │ Remises appliquées                                  │-80 000 │  │
│  │ NET RÉSERVATIONS                                    │750 000 │  │
│  ════════════════════════════════════════════════════════════════   │
│                                                                     │
│  ═══ PUBLICITÉS ════════════════════════════════════════════════    │
│  │ Publicité          │ Société   │ Répét. │ Montant facturé    │   │
│  ├────────────────────┼───────────┼────────┼────────────────────┤   │
│  │ Promo été 2026     │ Coca-Cola │   2    │     200 000 MGA    │   │
│  │ Telma 4G           │ Telma     │   1    │     100 000 MGA    │   │
│  ├────────────────────┴───────────┴────────┼────────────────────┤   │
│  │ TOTAL PUBLICITÉS                        │     300 000 MGA    │   │
│  ════════════════════════════════════════════════════════════════   │
│                                                                     │
│  ═══ VENTES PRODUITS ═══════════════════════════════════════════    │
│  │ Produit            │ Stock │ Vendus │ P.U.    │ CA Produit   │   │
│  ├────────────────────┼───────┼────────┼─────────┼──────────────┤   │
│  │ Eau minérale 50cl  │  20   │   15   │ 5 000   │    75 000    │   │
│  │ Biscuits           │  30   │   10   │ 2 500   │    25 000    │   │
│  │ Chips              │  15   │   10   │ 2 500   │    25 000    │   │
│  ├────────────────────┴───────┴────────┴─────────┼──────────────┤   │
│  │ TOTAL PRODUITS                                │   125 000    │   │
│  ════════════════════════════════════════════════════════════════   │
│                                                                     │
│  ╔══════════════════════════════════════════════════════════════╗   │
│  ║  CHIFFRE D'AFFAIRES TOTAL:           1 175 000 MGA           ║   │
│  ╚══════════════════════════════════════════════════════════════╝   │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Métier / Logique applicative

### 3.1 Classes utilisées

| Classe | Responsabilité | Emplacement |
|--------|---------------|-------------|
| `ChiffreAffairesStatsController` | Endpoint statistiques | Controller |
| `DashboardFinancierController` | Dashboard global | Controller |
| `DepartController` | CA par départ | Controller |
| `ChiffreAffairesStatsService` | Calcul CA global | Service |
| `DepartService` | CA par départ | Service |

### 3.2 Méthodes principales

#### ChiffreAffairesStatsService
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `getStatistiquesMois` | `Integer mois`, `Integer annee` | `ChiffreAffairesStatsDTO` | CA mensuel |
| `getStatistiquesPeriode` | `LocalDateTime debut`, `LocalDateTime fin` | `ChiffreAffairesStatsDTO` | CA sur période |

#### DepartService (enrichissement CA)
| Méthode | Arguments | Retour | Description |
|---------|-----------|--------|-------------|
| `findById` | `Long id` | `DepartDTO` | Départ avec CA calculé |
| `calculateChiffreAffairesMax` | `Depart` | `BigDecimal` | CA théorique réservations |
| `calculateChiffreAffaires` | `Depart` | `BigDecimal` | CA réel réservations |
| `calculateMontantPublicite` | `Depart` | `BigDecimal` | CA publicités |
| `calculateMontantProduits` | `Depart` | `BigDecimal` | CA produits |

### 3.3 Structure du DTO ChiffreAffairesStatsDTO

```java
public class ChiffreAffairesStatsDTO {
    private Integer mois;
    private Integer annee;
    private String deviseCode;
    
    // CA Réservations
    private BigDecimal caReservationsTheorique;
    private BigDecimal caReservationsReel;
    
    // CA Publicités
    private BigDecimal caDiffusionsTheorique;
    private BigDecimal caDiffusionsReel;
    
    // CA Produits
    private BigDecimal caVentesProduitsTheorique;
    private BigDecimal caVentesProduitsReel;
    
    // Totaux
    private BigDecimal totalTheorique;
    private BigDecimal totalReel;
    
    // Détail par départ
    private List<DepartDTO> departs;
}
```

### 3.4 Logique de calcul

```java
// Dans ChiffreAffairesStatsService
public ChiffreAffairesStatsDTO getStatistiquesPeriode(LocalDateTime start, LocalDateTime end) {
    ChiffreAffairesStatsDTO stats = new ChiffreAffairesStatsDTO();
    
    // Récupérer tous les départs de la période
    List<DepartDTO> departs = departRepository.findByDateRange(start, end)
            .stream()
            .map(d -> departService.findById(d.getId()))
            .toList();
    
    // 1. CA Réservations
    BigDecimal caReservationsTheo = departs.stream()
            .map(d -> d.getChiffreAffairesMax())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    BigDecimal caReservationsReel = departs.stream()
            .map(d -> d.getChiffreAffaires())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // 2. CA Publicités
    BigDecimal caPublicitesTheo = departs.stream()
            .map(d -> d.getMontantDiffusionsPublicite())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // 3. CA Produits
    BigDecimal caProduitsReel = venteProduitRepository.findByDateRange(start, end)
            .stream()
            .map(v -> v.getMontantTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // Agrégation
    stats.setCaReservationsTheorique(caReservationsTheo);
    stats.setCaReservationsReel(caReservationsReel);
    stats.setCaDiffusionsTheorique(caPublicitesTheo);
    stats.setCaVentesProduitsReel(caProduitsReel);
    
    // Totaux
    stats.setTotalTheorique(caReservationsTheo.add(caPublicitesTheo).add(caProduitsTheo));
    stats.setTotalReel(caReservationsReel.add(caPublicitesReel).add(caProduitsReel));
    
    return stats;
}
```

### 3.5 Services impactés
- `ChiffreAffairesStatsService`
- `DepartService`
- `ReservationService`
- `VenteProduitService`
- `DepartPubliciteService`
