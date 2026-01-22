# Système de Gestion des Publicités - Documentation

## 📺 Vue d'Ensemble

Nouvelle fonctionnalité permettant aux sociétés publicitaires de diffuser des vidéos publicitaires sur les écrans installés dans les véhicules de taxi-brousse.

**Modèle économique :** Facturation par diffusion  
**Tarif standard :** 100 000 Ar par diffusion  
**Période de facturation :** Mensuelle

---

## 🎯 Cas d'Usage

### Exemple : Décembre 2025

**Sociétés clientes :**
- **Vaniala** : 20 diffusions × 100 000 Ar = **2 000 000 Ar**
- **Lewis** : 10 diffusions × 100 000 Ar = **1 000 000 Ar**

**CA Total Décembre 2025 :** **3 000 000 Ar**

---

## 📊 Modèle de Données

### Tables Principales

#### 1. `societe_publicitaire`
Sociétés qui achètent de l'espace publicitaire

```sql
CREATE TABLE societe_publicitaire (
    id BIGINT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(200) NOT NULL,
    telephone VARCHAR(30),
    email VARCHAR(120),
    adresse TEXT,
    contact_nom VARCHAR(150),
    contact_telephone VARCHAR(30),
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Exemples :**
- `SOC-PUB-001` - Vaniala (produits laitiers)
- `SOC-PUB-002` - Lewis (high-tech)

---

#### 2. `tarif_publicite`
Historique des tarifs de diffusion

```sql
CREATE TABLE tarif_publicite (
    id BIGINT PRIMARY KEY,
    ref_devise_id BIGINT NOT NULL,
    montant DECIMAL(12,2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    description TEXT,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Caractéristiques :**
- Tarif par défaut : **100 000 Ar**
- Support de l'historique (date_debut/date_fin)
- Un seul tarif actif à la fois (date_fin = NULL)

---

#### 3. `publicite`
Vidéos publicitaires créées par les sociétés

```sql
CREATE TABLE publicite (
    id BIGINT PRIMARY KEY,
    code VARCHAR(80) UNIQUE NOT NULL,
    societe_publicitaire_id BIGINT NOT NULL,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    url_video VARCHAR(500),
    duree_secondes INTEGER,
    date_debut_validite DATE NOT NULL,
    date_fin_validite DATE NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Exemples :**
- `PUB-VANIALA-001` - Yaourts Bio (30 secondes)
- `PUB-LEWIS-001` - Smartphones 2025 (35 secondes)

**Validations :**
- `date_fin_validite >= date_debut_validite`
- Durée recommandée : 15-60 secondes

---

#### 4. `depart_publicite` ⭐ TABLE CLÉ
Chaque ligne = UNE DIFFUSION d'une publicité sur un départ

```sql
CREATE TABLE depart_publicite (
    id BIGINT PRIMARY KEY,
    depart_id BIGINT NOT NULL,              -- Quel départ
    publicite_id BIGINT NOT NULL,           -- Quelle pub
    tarif_publicite_id BIGINT NOT NULL,     -- Quel tarif appliqué
    date_diffusion TIMESTAMP NOT NULL,      -- Quand
    montant_facture DECIMAL(12,2) NOT NULL, -- Combien facturé
    ref_devise_id BIGINT NOT NULL,          -- Devise
    statut_diffusion VARCHAR(30) NOT NULL,  -- État
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Statuts possibles :**
- `PLANIFIE` - Diffusion prévue
- `DIFFUSE` - Diffusion effectuée (comptabilisée dans le CA)
- `ANNULE` - Diffusion annulée (non facturée)

**Clés étrangères :**
- `depart_id` → `depart(id)` - Sur quel trajet/départ
- `publicite_id` → `publicite(id)` - Quelle vidéo
- `tarif_publicite_id` → `tarif_publicite(id)` - Prix au moment de la diffusion
- `ref_devise_id` → `ref_devise(id)` - MGA, EUR, USD

---

## 🔍 Vues Analytiques

### 1. `v_statistiques_diffusions_par_societe`
Vue globale par société

```sql
CREATE VIEW v_statistiques_diffusions_par_societe AS
SELECT 
    sp.id AS societe_id,
    sp.code AS societe_code,
    sp.nom AS societe_nom,
    COUNT(dp.id) AS nombre_diffusions,
    SUM(CASE WHEN dp.statut_diffusion = 'DIFFUSE' 
        THEN dp.montant_facture ELSE 0 END) AS ca_total,
    MIN(dp.date_diffusion) AS premiere_diffusion,
    MAX(dp.date_diffusion) AS derniere_diffusion,
    rd.code AS devise_code
FROM societe_publicitaire sp
LEFT JOIN publicite p ON sp.id = p.societe_publicitaire_id
LEFT JOIN depart_publicite dp ON p.id = dp.publicite_id
LEFT JOIN ref_devise rd ON dp.ref_devise_id = rd.id
GROUP BY sp.id, sp.code, sp.nom, rd.code;
```

**Utilisation :**
```sql
SELECT * FROM v_statistiques_diffusions_par_societe;
```

**Résultat :**
| societe_nom | nombre_diffusions | ca_total | devise_code |
|-------------|-------------------|----------|-------------|
| Vaniala | 20 | 2 000 000 | MGA |
| Lewis | 10 | 1 000 000 | MGA |

---

### 2. `v_ca_mensuel_diffusions`
CA mensuel détaillé

```sql
CREATE VIEW v_ca_mensuel_diffusions AS
SELECT 
    sp.id AS societe_id,
    sp.nom AS societe_nom,
    EXTRACT(YEAR FROM dp.date_diffusion) AS annee,
    EXTRACT(MONTH FROM dp.date_diffusion) AS mois,
    COUNT(dp.id) FILTER (WHERE dp.statut_diffusion = 'DIFFUSE') AS nombre_diffusions,
    SUM(dp.montant_facture) FILTER (WHERE dp.statut_diffusion = 'DIFFUSE') AS ca_mensuel,
    rd.code AS devise_code
FROM societe_publicitaire sp
JOIN publicite p ON sp.id = p.societe_publicitaire_id
JOIN depart_publicite dp ON p.id = dp.publicite_id
JOIN ref_devise rd ON dp.ref_devise_id = rd.id
GROUP BY sp.id, sp.nom, annee, mois, rd.code
ORDER BY annee DESC, mois DESC, societe_nom;
```

**Utilisation :**
```sql
-- CA de décembre 2025
SELECT * FROM v_ca_mensuel_diffusions 
WHERE annee = 2025 AND mois = 12;

-- CA total d'un mois
SELECT 
    'Total CA' AS description,
    SUM(ca_mensuel) AS montant_total
FROM v_ca_mensuel_diffusions
WHERE annee = 2025 AND mois = 12;
```

---

## 🛠️ Fonctions Helper

### `get_tarif_publicite_actuel(date)`
Récupère le tarif en vigueur à une date donnée

```sql
SELECT * FROM get_tarif_publicite_actuel('2025-12-15');
```

**Résultat :**
| id | montant | devise_code |
|----|---------|-------------|
| 1 | 100000.00 | MGA |

---

## 📈 Requêtes Utiles

### 1. Liste des diffusions d'un départ
```sql
SELECT 
    p.titre AS publicite,
    sp.nom AS societe,
    dp.date_diffusion,
    dp.montant_facture,
    dp.statut_diffusion
FROM depart_publicite dp
JOIN publicite p ON dp.publicite_id = p.id
JOIN societe_publicitaire sp ON p.societe_publicitaire_id = sp.id
WHERE dp.depart_id = 123
ORDER BY dp.date_diffusion;
```

### 2. Publicités les plus diffusées
```sql
SELECT 
    p.titre,
    sp.nom AS societe,
    COUNT(dp.id) AS nb_diffusions,
    SUM(dp.montant_facture) AS ca_total
FROM publicite p
JOIN societe_publicitaire sp ON p.societe_publicitaire_id = sp.id
LEFT JOIN depart_publicite dp ON p.id = dp.publicite_id
WHERE dp.statut_diffusion = 'DIFFUSE'
GROUP BY p.id, p.titre, sp.nom
ORDER BY nb_diffusions DESC
LIMIT 10;
```

### 3. CA par mois (tous les mois)
```sql
SELECT 
    annee,
    mois,
    SUM(nombre_diffusions) AS total_diffusions,
    SUM(ca_mensuel) AS total_ca,
    devise_code
FROM v_ca_mensuel_diffusions
GROUP BY annee, mois, devise_code
ORDER BY annee DESC, mois DESC;
```

### 4. Diffusions planifiées à venir
```sql
SELECT 
    d.code AS depart_code,
    d.date_heure_depart,
    p.titre AS publicite,
    sp.nom AS societe,
    dp.montant_facture
FROM depart_publicite dp
JOIN depart d ON dp.depart_id = d.id
JOIN publicite p ON dp.publicite_id = p.id
JOIN societe_publicitaire sp ON p.societe_publicitaire_id = sp.id
WHERE dp.statut_diffusion = 'PLANIFIE'
  AND dp.date_diffusion >= CURRENT_TIMESTAMP
ORDER BY dp.date_diffusion;
```

---

## 🚀 Installation

### 1. Créer les tables
```bash
psql -U postgres -d taxi_brousse -f db/publicite_v2.sql
```

### 2. Charger les données de test
```bash
psql -U postgres -d taxi_brousse -f db/data_publicite_test.sql
```

### 3. Vérifier l'installation
```sql
-- Compter les diffusions par société
SELECT * FROM v_ca_mensuel_diffusions 
WHERE annee = 2025 AND mois = 12;
```

**Résultat attendu :**
```
 societe_id | societe_nom | annee | mois | nombre_diffusions | ca_mensuel | devise_code 
------------+-------------+-------+------+-------------------+------------+-------------
          1 | Vaniala     |  2025 |   12 |                20 | 2000000.00 | MGA
          2 | Lewis       |  2025 |   12 |                10 | 1000000.00 | MGA
```

---

## 🎨 Intégration Backend (TODO)

### Entités Java à créer

#### 1. `SocietePublicitaire.java`
```java
@Entity
@Table(name = "societe_publicitaire")
@Getter @Setter
public class SocietePublicitaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(nullable = false, length = 200)
    private String nom;
    
    private String telephone;
    private String email;
    private String adresse;
    private String contactNom;
    private String contactTelephone;
    
    @Column(nullable = false)
    private Boolean actif = true;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "societePublicitaire")
    private List<Publicite> publicites;
}
```

#### 2. `Publicite.java`
```java
@Entity
@Table(name = "publicite")
@Getter @Setter
public class Publicite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 80)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "societe_publicitaire_id", nullable = false)
    private SocietePublicitaire societePublicitaire;
    
    @Column(nullable = false, length = 200)
    private String titre;
    
    private String description;
    private String urlVideo;
    private Integer dureeSecondes;
    
    @Column(nullable = false)
    private LocalDate dateDebutValidite;
    
    @Column(nullable = false)
    private LocalDate dateFinValidite;
    
    private Boolean actif = true;
    private LocalDateTime createdAt;
}
```

#### 3. `TarifPublicite.java`
```java
@Entity
@Table(name = "tarif_publicite")
@Getter @Setter
public class TarifPublicite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;
    
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    private LocalDate dateFin;
    private String description;
    private Boolean actif = true;
    private LocalDateTime createdAt;
}
```

#### 4. `DepartPublicite.java`
```java
@Entity
@Table(name = "depart_publicite")
@Getter @Setter
public class DepartPublicite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "depart_id", nullable = false)
    private Depart depart;
    
    @ManyToOne
    @JoinColumn(name = "publicite_id", nullable = false)
    private Publicite publicite;
    
    @ManyToOne
    @JoinColumn(name = "tarif_publicite_id", nullable = false)
    private TarifPublicite tarifPublicite;
    
    @Column(nullable = false)
    private LocalDateTime dateDiffusion;
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantFacture;
    
    @ManyToOne
    @JoinColumn(name = "ref_devise_id", nullable = false)
    private RefDevise refDevise;
    
    @Column(nullable = false, length = 30)
    private String statutDiffusion = "PLANIFIE";
    
    private String notes;
    private LocalDateTime createdAt;
}
```

### Repositories à créer

```java
public interface SocietePublicitaireRepository extends JpaRepository<SocietePublicitaire, Long> {
    Optional<SocietePublicitaire> findByCode(String code);
    List<SocietePublicitaire> findByActifTrue();
}

public interface PubliciteRepository extends JpaRepository<Publicite, Long> {
    List<Publicite> findBySocietePublicitaireId(Long societeId);
    List<Publicite> findByActifTrueAndDateDebutValiditeLessThanEqualAndDateFinValiditeGreaterThanEqual(
        LocalDate date1, LocalDate date2);
}

public interface DepartPubliciteRepository extends JpaRepository<DepartPublicite, Long> {
    List<DepartPublicite> findByDepartId(Long departId);
    List<DepartPublicite> findByPubliciteId(Long publiciteId);
    
    @Query("SELECT dp FROM DepartPublicite dp WHERE " +
           "YEAR(dp.dateDiffusion) = :annee AND MONTH(dp.dateDiffusion) = :mois")
    List<DepartPublicite> findByAnneeAndMois(@Param("annee") int annee, @Param("mois") int mois);
}
```

---

## 📊 Endpoints API à créer

```
GET    /api/societes-publicitaires           # Liste toutes
GET    /api/societes-publicitaires/{id}      # Détails
POST   /api/societes-publicitaires           # Créer
PUT    /api/societes-publicitaires/{id}      # Modifier
DELETE /api/societes-publicitaires/{id}      # Supprimer

GET    /api/publicites                       # Liste toutes
GET    /api/publicites/societe/{id}          # Par société
GET    /api/publicites/actives               # Actives actuellement
POST   /api/publicites                       # Créer
PUT    /api/publicites/{id}                  # Modifier
DELETE /api/publicites/{id}                  # Supprimer

GET    /api/diffusions                       # Liste toutes
GET    /api/diffusions/depart/{id}           # Par départ
GET    /api/diffusions/publicite/{id}        # Par publicité
GET    /api/diffusions/statistiques          # Stats globales
GET    /api/diffusions/ca-mensuel            # CA mensuel
POST   /api/diffusions                       # Planifier une diffusion
PUT    /api/diffusions/{id}/statut           # Changer statut
DELETE /api/diffusions/{id}                  # Annuler

GET    /api/tarifs-publicite                 # Liste historique
GET    /api/tarifs-publicite/actuel          # Tarif actuel
POST   /api/tarifs-publicite                 # Créer nouveau tarif
```

---

## ✅ Checklist d'Implémentation

### Base de Données
- [x] Tables créées (societe_publicitaire, publicite, tarif_publicite, depart_publicite)
- [x] Contraintes et index
- [x] Vues analytiques
- [x] Fonction helper
- [x] Données de test

### Backend (À faire)
- [ ] Entités JPA (4 classes)
- [ ] Repositories (4 interfaces)
- [ ] Services métier (4 classes)
- [ ] DTOs (4 classes)
- [ ] Mappers (4 classes)
- [ ] Contrôleurs REST (3 classes)

### Frontend (À faire)
- [ ] Page gestion sociétés publicitaires
- [ ] Page gestion publicités (vidéos)
- [ ] Page planification diffusions
- [ ] Dashboard statistiques CA
- [ ] Integration avec page départs

---

## 📝 Notes Importantes

1. **Une diffusion = une ligne dans `depart_publicite`**
   - Pour 20 diffusions, il y a 20 lignes dans la table
   - Chaque ligne est facturée au tarif en vigueur

2. **Le montant_facture est une copie**
   - Permet de garder l'historique même si le tarif change
   - Garantit la cohérence de la facturation

3. **Statuts de diffusion**
   - Seules les diffusions `DIFFUSE` comptent dans le CA
   - `PLANIFIE` = prévision
   - `ANNULE` = annulation (non facturée)

4. **Flexibilité des tarifs**
   - Support de plusieurs devises (MGA, EUR, USD)
   - Historique complet des changements de prix
   - Un seul tarif actif à la fois

---

**Dernière mise à jour :** 22 janvier 2026  
**Version du schéma :** 1.0  
**Statut :** Base de données complète, backend à implémenter
