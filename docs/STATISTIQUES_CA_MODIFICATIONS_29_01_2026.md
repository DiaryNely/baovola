# Modifications du 29 Janvier 2026 : Module Statistiques Chiffre d'Affaires

## 1. MCD - Modèle Conceptuel de Données

### Tables Impliquées

```
┌──────────────────────────────────────┐
│         PAIEMENT                     │
├──────────────────────────────────────┤
│ PK  id                    BIGSERIAL  │
│ FK  reservation_id        BIGINT     │
│ FK  ref_paiement_statut_id BIGINT    │
│     montant               DECIMAL    │
│     created_at            TIMESTAMP  │
└──────────────────────────────────────┘
         │
         │ N
         ▼ 1
┌──────────────────────────────────────┐
│       RESERVATION                    │
├──────────────────────────────────────┤
│ PK  id                    BIGSERIAL  │
│ FK  depart_id             BIGINT     │
│     ...                              │
└──────────────────────────────────────┘


┌──────────────────────────────────────┐
│    PAIEMENT_PUBLICITE                │
├──────────────────────────────────────┤
│ PK  id                    BIGSERIAL  │
│ FK  societe_publicitaire_id BIGINT   │
│     montant               DECIMAL    │
│     date_paiement         DATE       │
│     created_at            TIMESTAMP  │
└──────────────────────────────────────┘


┌──────────────────────────────────────┐
│      DEPART_PUBLICITE                │
├──────────────────────────────────────┤
│ PK  id                    BIGSERIAL  │
│ FK  depart_id             BIGINT     │
│ FK  publicite_id          BIGINT     │
│     montant_facture       DECIMAL    │
│     date_diffusion        TIMESTAMP  │
│     created_at            TIMESTAMP  │
└──────────────────────────────────────┘


┌──────────────────────────────────────┐
│       STOCK_DEPART                   │
├──────────────────────────────────────┤
│ PK  id                    BIGSERIAL  │
│ FK  depart_id             BIGINT     │
│ FK  produit_id            BIGINT     │
│     quantite_initiale     INTEGER    │
│     quantite_vendue       INTEGER    │
│     quantite_disponible   INTEGER    │
│     prix_unitaire         DECIMAL    │
│     created_at            TIMESTAMP  │
└──────────────────────────────────────┘
         │
         │ 1
         ▼ N
┌──────────────────────────────────────┐
│      VENTE_PRODUIT                   │
├──────────────────────────────────────┤
│ PK  id                    BIGSERIAL  │
│ FK  stock_depart_id       BIGINT     │
│ FK  client_id             BIGINT     │
│     quantite              INTEGER    │
│     prix_unitaire         DECIMAL    │
│     montant_total         DECIMAL    │
│     date_vente            TIMESTAMP  │
│     created_at            TIMESTAMP  │
└──────────────────────────────────────┘
```

### Cardinalités

- `PAIEMENT (N) ─── (1) RESERVATION` : Un paiement appartient à une réservation
- `PAIEMENT_PUBLICITE (N) ─── (1) SOCIETE_PUBLICITAIRE` : Un paiement appartient à une société
- `DEPART_PUBLICITE (N) ─── (1) DEPART` : Une diffusion appartient à un départ
- `STOCK_DEPART (N) ─── (1) DEPART` : Un stock appartient à un départ
- `VENTE_PRODUIT (N) ─── (1) STOCK_DEPART` : Une vente provient d'un stock

## 2. Maquettes ASCII des Pages

### Page Principale : `/statistiques/chiffre-affaires`

```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                     STATISTIQUES CHIFFRE D'AFFAIRES                           ║
╚═══════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────────────────────────────────────────────────────────┐
│ 🔍 Filtrer par période                                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   Mois: [Janvier ▼]    Année: [2026 ▼]    [Afficher]                       │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ ℹ️  Légende                                                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│  CA Théorique : Montant total attendu (facturé)                             │
│  CA Réel : Montant effectivement encaissé                                   │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ 📊 Chiffre d'Affaires - Janvier 2026                                        │
├─────────────────┬───────────────────┬───────────────────┬──────────────────┤
│ Type            │ CA Réservations   │ CA Diffusions Pub │ CA Ventes Produits│
├─────────────────┼───────────────────┼───────────────────┼──────────────────┤
│ CA Théorique    │  5,000,000 MGA    │  2,000,000 MGA    │  1,500,000 MGA   │
│ CA Réel (Payé)  │  4,200,000 MGA    │  1,800,000 MGA    │    900,000 MGA   │
└─────────────────┴───────────────────┴───────────────────┴──────────────────┘
                                                         Total Théorique: 8,500,000 MGA
                                                         Total Réel: 6,900,000 MGA

┌─────────────────────────────────────────────────────────────────────────────┐
│ 💰 CHIFFRE D'AFFAIRES THÉORIQUE          │ 💵 CHIFFRE D'AFFAIRES RÉEL       │
│                                           │                                  │
│        8,500,000 MGA                      │        6,900,000 MGA             │
└───────────────────────────────────────────┴──────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ 📈 Taux de Recouvrement                                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│  Réservations: 84.0%  │  Publicité: 90.0%  │  Global: 81.2%                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Navigation Sidebar

```
┌────────────────────────┐
│ VENTES                 │
├────────────────────────┤
│ 📦 Produits            │
│ 🛒 Stock & Ventes      │
├────────────────────────┤
│ STATISTIQUES           │
├────────────────────────┤
│ 📈 Chiffre d'Affaires  │  ← NOUVEAU
└────────────────────────┘
```

## 3. Architecture Métier

### 3.1. DTO (Data Transfer Object)

#### ChiffreAffairesStatsDTO.java

```
Package: com.taxi_brousse.dto
```

**Attributs :**
- `BigDecimal caReservationsTheorique` : CA théorique des réservations
- `BigDecimal caReservationsReel` : CA réel des réservations (payé)
- `BigDecimal caDiffusionsTheorique` : CA théorique des diffusions publicité
- `BigDecimal caDiffusionsReel` : CA réel des diffusions publicité (payé)
- `BigDecimal caVentesProduitsTheorique` : CA théorique des ventes produits (valeur stock)
- `BigDecimal caVentesProduitsReel` : CA réel des ventes produits (ventes effectives)
- `BigDecimal totalTheorique` : Total CA théorique
- `BigDecimal totalReel` : Total CA réel
- `Integer mois` : Mois de la période
- `Integer annee` : Année de la période
- `String deviseCode` : Code devise (MGA)

**Annotations :**
- `@Data` : Génère getters/setters
- `@NoArgsConstructor` : Constructeur sans arguments
- `@AllArgsConstructor` : Constructeur avec tous les arguments

### 3.2. Service

#### ChiffreAffairesStatsService.java

```
Package: com.taxi_brousse.service
```

**Dépendances injectées :**
- `PaiementRepository paiementRepository`
- `PaiementPubliciteRepository paiementPubliciteRepository`
- `VenteProduitRepository venteProduitRepository`
- `StockDepartRepository stockDepartRepository`

**Méthodes :**

##### `getStatistiquesMois(Integer mois, Integer annee) : ChiffreAffairesStatsDTO`

**Arguments :**
- `mois` : Le mois à analyser (1-12)
- `annee` : L'année à analyser

**Retour :** DTO contenant toutes les statistiques

**Logique métier :**

1. **Calcul des dates**
   ```
   startDate = YearMonth.of(annee, mois).atDay(1).atStartOfDay()
   endDate = YearMonth.of(annee, mois).atEndOfMonth().atTime(23, 59, 59)
   ```

2. **CA Réservations**
   - **Théorique** : `paiementRepository.sumMontantTotalByDateRange(startDate, endDate)`
     - Somme de tous les paiements (attendus) dans la période
   - **Réel** : `paiementRepository.sumMontantPayeByDateRange(startDate, endDate)`
     - Somme des paiements avec statut `EFFECTUE` uniquement

3. **CA Diffusions Publicité**
   - **Théorique** : `paiementPubliciteRepository.sumMontantTotalByDateRange(startDate, endDate)`
     - Somme des `montant_facture` de toutes les diffusions dans la période
     - Basé sur la table `depart_publicite` et le champ `date_diffusion`
   - **Réel** : `paiementPubliciteRepository.sumMontantPayeByDateRange(startDate, endDate)`
     - Somme des paiements publicité effectués dans la période
     - Basé sur la table `paiement_publicite` et le champ `date_paiement`

4. **CA Ventes Produits**
   - **Théorique** : `stockDepartRepository.calculateTotalStockValueByDateRange(startDate, endDate)`
     - Formule : `SUM(quantite_initiale * prix_unitaire)`
     - Représente la valeur maximale si tout le stock est vendu
   - **Réel** : `venteProduitRepository.findByDateRange(startDate, endDate).stream().map(v -> v.getMontantTotal()).reduce(ZERO, add)`
     - Somme des `montant_total` de toutes les ventes dans la période

5. **Calculs des totaux**
   ```
   totalTheorique = caReservationsTheorique + caDiffusionsTheorique + caVentesProduitsTheorique
   totalReel = caReservationsReel + caDiffusionsReel + caVentesProduitsReel
   ```

### 3.3. Controller

#### ChiffreAffairesStatsController.java

```
Package: com.taxi_brousse.controller
Route: /statistiques/chiffre-affaires
```

**Dépendances injectées :**
- `ChiffreAffairesStatsService statsService`

**Méthodes :**

##### `index(Integer mois, Integer annee, Model model) : String`

**Arguments :**
- `mois` : Mois sélectionné (optionnel, défaut : mois actuel)
- `annee` : Année sélectionnée (optionnel, défaut : année actuelle)
- `model` : Model Spring MVC

**Retour :** `"taxi_brousse/statistiques/chiffre_affaires"` (nom de la vue)

**Logique :**
1. Valeurs par défaut si paramètres non fournis
   ```java
   if (mois == null) mois = LocalDate.now().getMonthValue()
   if (annee == null) annee = LocalDate.now().getYear()
   ```

2. Appel service pour récupérer les statistiques
   ```java
   ChiffreAffairesStatsDTO stats = statsService.getStatistiquesMois(mois, annee)
   ```

3. Ajout au model
   ```java
   model.addAttribute("stats", stats)
   model.addAttribute("selectedMois", mois)
   model.addAttribute("selectedAnnee", annee)
   ```

### 3.4. Repositories (Méthodes ajoutées)

#### PaiementRepository.java

**Nouvelles méthodes :**

##### `sumMontantTotalByDateRange(LocalDateTime startDate, LocalDateTime endDate) : BigDecimal`

**Requête JPQL :**
```sql
SELECT COALESCE(SUM(p.montant), 0) 
FROM Paiement p 
WHERE p.createdAt BETWEEN :startDate AND :endDate
```

**Description :** Somme de tous les paiements (théorique) dans la période

##### `sumMontantPayeByDateRange(LocalDateTime startDate, LocalDateTime endDate) : BigDecimal`

**Requête JPQL :**
```sql
SELECT COALESCE(SUM(p.montant), 0) 
FROM Paiement p 
WHERE p.createdAt BETWEEN :startDate AND :endDate 
  AND p.refPaiementStatut.code = 'EFFECTUE'
```

**Description :** Somme des paiements effectivement payés dans la période

#### PaiementPubliciteRepository.java

**Nouvelles méthodes :**

##### `sumMontantTotalByDateRange(LocalDateTime startDate, LocalDateTime endDate) : BigDecimal`

**Requête JPQL :**
```sql
SELECT COALESCE(SUM(d.montantFacture), 0) 
FROM DepartPublicite d 
WHERE d.dateDiffusion BETWEEN :startDate AND :endDate
```

**Description :** Somme des montants facturés pour les diffusions dans la période

##### `sumMontantPayeByDateRange(LocalDateTime startDate, LocalDateTime endDate) : BigDecimal`

**Requête JPQL :**
```sql
SELECT COALESCE(SUM(p.montant), 0) 
FROM PaiementPublicite p 
WHERE p.datePaiement BETWEEN CAST(:startDate AS date) AND CAST(:endDate AS date)
```

**Description :** Somme des paiements publicité effectués dans la période

#### StockDepartRepository.java

**Nouvelle méthode :**

##### `calculateTotalStockValueByDateRange(LocalDateTime startDate, LocalDateTime endDate) : BigDecimal`

**Requête JPQL :**
```sql
SELECT COALESCE(SUM(s.quantiteInitiale * s.prixUnitaire), 0) 
FROM StockDepart s 
WHERE s.createdAt BETWEEN :startDate AND :endDate
```

**Description :** Calcule la valeur totale du stock initial dans la période (CA théorique produits)

### 3.5. Vue Thymeleaf

#### chiffre_affaires.html

```
Template: taxi_brousse/statistiques/chiffre_affaires.html
```

**Sections principales :**

1. **Filtres de période**
   - Select mois (1-12, labels en français)
   - Select année (2024-2027)
   - Bouton "Afficher"

2. **Légende**
   - Explication CA Théorique vs CA Réel

3. **Tableau des statistiques**
   - 2 lignes (CA Théorique, CA Réel)
   - 3 colonnes (Réservations, Publicité, Produits)
   - Colonne supplémentaire pour les totaux
   - Formatage des nombres avec séparateurs de milliers

4. **Cards récapitulatives**
   - Card Total Théorique (bleue)
   - Card Total Réel (verte)

5. **Section Taux de Recouvrement**
   - Calcul : `(CA Réel / CA Théorique) * 100`
   - Par catégorie et global
   - Affichage conditionnel (N/A si théorique = 0)

**Expressions Thymeleaf clés :**
```html
<!-- Formatage montant -->
<span th:text="${#numbers.formatDecimal(stats.totalReel, 0, 'COMMA', 0, 'POINT')}">0</span>

<!-- Calcul taux recouvrement -->
<span th:if="${stats.totalTheorique > 0}"
      th:text="${#numbers.formatDecimal((stats.totalReel / stats.totalTheorique) * 100, 1, 'POINT', 2, 'COMMA')} + '%'">
</span>

<!-- Mois en français -->
<span th:text="${stats.mois == 1 ? 'Janvier' : stats.mois == 2 ? 'Février' : ... } + ' ' + ${stats.annee}">
</span>
```

## 4. Flux de Données

### Workflow d'affichage des statistiques

```
┌─────────────┐
│   Utilisateur│
│   accède à   │
│     /statistiques/chiffre-affaires
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│  ChiffreAffairesStatsController     │
│  - Récupère mois/année (défaut: now)│
│  - Appelle statsService             │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  ChiffreAffairesStatsService        │
│  1. Calcule startDate/endDate       │
│  2. Query PaiementRepository        │
│  3. Query PaiementPubliciteRepo     │
│  4. Query StockDepartRepository     │
│  5. Query VenteProduitRepository    │
│  6. Agrège dans DTO                 │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  Repositories (requêtes SQL)        │
│  - SUM sur paiement                 │
│  - SUM sur depart_publicite         │
│  - SUM sur paiement_publicite       │
│  - SUM sur stock_depart             │
│  - SUM sur vente_produit            │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  ChiffreAffairesStatsDTO            │
│  - caReservationsTheorique          │
│  - caReservationsReel               │
│  - caDiffusionsTheorique            │
│  - caDiffusionsReel                 │
│  - caVentesProduitsTheorique        │
│  - caVentesProduitsReel             │
│  - totalTheorique                   │
│  - totalReel                        │
└──────┬──────────────────────────────┘
       │
       ▼
┌─────────────────────────────────────┐
│  Vue Thymeleaf                      │
│  - Affiche tableau                  │
│  - Affiche cards                    │
│  - Calcule taux recouvrement        │
└─────────────────────────────────────┘
```

## 5. Règles de Gestion

### 5.1. CA Réservations

- **Théorique** : Tous les paiements créés dans la période, quel que soit leur statut
- **Réel** : Uniquement les paiements avec `ref_paiement_statut.code = 'EFFECTUE'`
- **Filtre** : `paiement.created_at BETWEEN startDate AND endDate`

### 5.2. CA Diffusions Publicité

- **Théorique** : Montants facturés pour les diffusions (table `depart_publicite`)
  - Basé sur `date_diffusion` : quand le service est rendu
  - Champ utilisé : `montant_facture`
- **Réel** : Paiements effectués par les sociétés (table `paiement_publicite`)
  - Basé sur `date_paiement` : quand le paiement est effectué
  - Champ utilisé : `montant`
- **Note** : Le CA réel peut être inférieur au théorique en cas d'impayés

### 5.3. CA Ventes Produits

- **Théorique** : Valeur du stock initial mis à disposition
  - Formule : `SUM(stock_depart.quantite_initiale * stock_depart.prix_unitaire)`
  - Filtre : `stock_depart.created_at BETWEEN startDate AND endDate`
  - Représente le CA maximum si tout est vendu
- **Réel** : Montant des ventes effectivement réalisées
  - Formule : `SUM(vente_produit.montant_total)`
  - Filtre : `vente_produit.date_vente BETWEEN startDate AND endDate`
  - Les ventes sont considérées comme payées immédiatement (pas de crédit)

### 5.4. Période de calcul

- **Période** : Du 1er jour du mois à 00:00:00 au dernier jour du mois à 23:59:59
- **Timezone** : Timezone système (pas de gestion multi-timezone)
- **Devise** : Toujours MGA (Ariary malgache)

### 5.5. Taux de recouvrement

- **Formule** : `(CA Réel / CA Théorique) * 100`
- **Affichage** : 
  - Pourcentage avec 2 décimales
  - "N/A" si CA Théorique = 0
- **Catégories** :
  - Réservations : `caReservationsReel / caReservationsTheorique`
  - Publicité : `caDiffusionsReel / caDiffusionsTheorique`
  - Global : `totalReel / totalTheorique`
- **Note** : Pas de taux pour les produits (considérés comme toujours 100% si vendus)

## 6. Navigation

### Lien ajouté dans fragments.html

```html
<hr class="sidebar-divider">
<div class="sidebar-heading">Statistiques</div>
<li class="nav-item">
    <a class="nav-link" href="/statistiques/chiffre-affaires">
        <i class="fas fa-fw fa-chart-line"></i>
        <span>Chiffre d'Affaires</span>
    </a>
</li>
```

**Position** : Après la section "Ventes", avant la fermeture du sidebar

## 7. Corrections Techniques Appliquées

### 7.1. Erreur initiale : Entité inexistante

**Problème** : La requête référençait `DiffusionPublicite` qui n'existe pas

**Solution** : Correction vers `DepartPublicite` (l'entité réelle)

### 7.2. Erreur Thymeleaf : createDate

**Problème** : `#temporals.createDate()` n'existe pas en Thymeleaf

**Solution** : Remplacement par expression ternaire conditionnelle
```
${stats.mois == 1 ? 'Janvier' : stats.mois == 2 ? 'Février' : ...}
```

### 7.3. Logique CA Produits

**Évolution** : Initialement, CA théorique = CA réel (ventes)

**Correction** : 
- CA théorique = valeur stock initial (quantité × prix)
- CA réel = ventes effectuées
- Plus cohérent avec les autres catégories

## 8. Tests Recommandés

### 8.1. Tests Unitaires

- ☐ `ChiffreAffairesStatsService.getStatistiquesMois()` avec mois vide
- ☐ `ChiffreAffairesStatsService.getStatistiquesMois()` avec données
- ☐ Calculs des totaux (théorique et réel)
- ☐ Gestion des nulls dans les SUM

### 8.2. Tests d'Intégration

- ☐ Affichage page avec filtres mois/année
- ☐ Calcul correct du taux de recouvrement
- ☐ Formatage des nombres (séparateurs de milliers)
- ☐ Affichage conditionnel "N/A"

### 8.3. Tests Fonctionnels

- ☐ Navigation depuis sidebar
- ☐ Changement de mois/année
- ☐ Vérification des montants par rapport à la base
- ☐ Cohérence entre théorique et réel

## 9. Points d'Attention

### 9.1. Performance

- Les requêtes SUM peuvent être lourdes sur de grandes périodes
- Recommandation : Ajouter des index sur les colonnes de date
  - `paiement.created_at`
  - `depart_publicite.date_diffusion`
  - `paiement_publicite.date_paiement`
  - `stock_depart.created_at`
  - `vente_produit.date_vente`

### 9.2. Évolutions Possibles

- Export PDF des statistiques
- Graphiques de visualisation (charts.js)
- Statistiques annuelles (agrégation par mois)
- Comparaison entre périodes
- Filtres par trajet ou véhicule

### 9.3. Données de Test

Pour tester correctement, créer :
- Des paiements avec statuts variés (EFFECTUE, EN_ATTENTE)
- Des diffusions publicité avec dates différentes
- Des paiements publicité
- Du stock de produits
- Des ventes de produits

---

**Date de création** : 29 Janvier 2026  
**Module** : Statistiques - Chiffre d'Affaires  
**Status** : ✅ Implémenté et fonctionnel
