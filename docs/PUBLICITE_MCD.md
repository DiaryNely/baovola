# MCD - Système de Publicité Taxi-Brousse

## Modèle Conceptuel de Données (ASCII)

```
┌────────────────────────────────────┐
│    SOCIETE_PUBLICITAIRE            │
├────────────────────────────────────┤
│ PK id                              │
│    code (UNIQUE)                   │
│    nom                             │
│    telephone                       │
│    email                           │
│    adresse                         │
│    contact_nom                     │
│    contact_telephone               │
│    actif                           │
│    created_at                      │
└────────────────────────────────────┘
           │ 1
           │ crée
           │ N
           ▼
┌────────────────────────────────────┐
│        PUBLICITE                   │
├────────────────────────────────────┤
│ PK id                              │
│    code (UNIQUE)                   │
│ FK societe_publicitaire_id         │
│    titre                           │
│    description                     │
│    url_video                       │
│    duree_secondes                  │
│    date_debut_validite             │
│    date_fin_validite               │
│    actif                           │
│    created_at                      │
└────────────────────────────────────┘
           │ 1
           │ diffusée sur
           │ N
           ▼
┌────────────────────────────────────┐
│     DEPART_PUBLICITE               │
│     (Table de liaison)             │
├────────────────────────────────────┤
│ PK id                              │
│ FK depart_id ────────────────┐     │
│ FK publicite_id              │     │
│ FK tarif_publicite_id        │     │
│ FK ref_devise_id             │     │
│    date_diffusion            │     │
│    montant_facture           │     │
│    statut_diffusion          │     │
│    notes                     │     │
│    created_at                │     │
└────────────────────────────────────┘
           │                         │
           │ N                       │ N
           │                         │
           ▼                         │
┌────────────────────────────────────┐│
│     TARIF_PUBLICITE                ││
├────────────────────────────────────┤│
│ PK id                              ││
│ FK ref_devise_id                   ││
│    montant (100 000 Ar par défaut) ││
│    date_debut                      ││
│    date_fin                        ││
│    description                     ││
│    actif                           ││
│    created_at                      ││
└────────────────────────────────────┘│
                                      │
           ┌──────────────────────────┘
           │
           ▼
┌────────────────────────────────────┐
│           DEPART                   │
│       (Table existante)            │
├────────────────────────────────────┤
│ PK id                              │
│    code                            │
│ FK cooperative_id                  │
│ FK trajet_id                       │
│ FK vehicule_id                     │
│    date_heure_depart               │
│    date_heure_arrivee_estimee      │
│    ref_depart_statut_id            │
│    ...                             │
└────────────────────────────────────┘
```

---

## Relations et Cardinalités

### 1. SOCIETE_PUBLICITAIRE → PUBLICITE
**Cardinalité :** `1:N` (one-to-many)
- Une société peut créer plusieurs publicités (vidéos)
- Une publicité appartient à une seule société

**Exemple :**
- Vaniala → PUB-VANIALA-001 (Yaourts Bio)
- Vaniala → PUB-VANIALA-002 (Fromage Artisanal)
- Vaniala → PUB-VANIALA-003 (Beurre Premium)

---

### 2. PUBLICITE → DEPART_PUBLICITE
**Cardinalité :** `1:N` (one-to-many)
- Une publicité peut être diffusée sur plusieurs départs
- Une diffusion concerne une seule publicité

**Exemple :**
- PUB-VANIALA-001 diffusée 8 fois en décembre 2025

---

### 3. DEPART → DEPART_PUBLICITE
**Cardinalité :** `1:N` (one-to-many)
- Un départ peut avoir plusieurs publicités diffusées
- Une diffusion est associée à un seul départ

**Exemple :**
- Départ TANA-ANTSIRABE du 01/12/2025 :
  - Diffusion PUB-VANIALA-001
  - Diffusion PUB-LEWIS-001

---

### 4. TARIF_PUBLICITE → DEPART_PUBLICITE
**Cardinalité :** `1:N` (one-to-many)
- Un tarif peut être utilisé pour plusieurs diffusions
- Une diffusion utilise un tarif spécifique (snapshot au moment de la diffusion)

**Avantage :** Conservation de l'historique des prix

---

## Règles Métier

### Contraintes de Domaine

**SOCIETE_PUBLICITAIRE :**
- `code` : UNIQUE, NOT NULL
- `nom` : NOT NULL
- `actif` : Boolean (TRUE par défaut)

**PUBLICITE :**
- `code` : UNIQUE, NOT NULL
- `date_fin_validite >= date_debut_validite` (CHECK)
- `duree_secondes > 0` (si renseigné)

**TARIF_PUBLICITE :**
- `montant > 0` (CHECK)
- `date_fin >= date_debut` (si renseigné)
- Un seul tarif actif à la fois (`date_fin = NULL`)

**DEPART_PUBLICITE :**
- `montant_facture >= 0` (CHECK)
- `statut_diffusion IN ('PLANIFIE', 'DIFFUSE', 'ANNULE')`

---

### Contraintes de Gestion

1. **Facturation :**
   - Seules les diffusions avec `statut_diffusion = 'DIFFUSE'` comptent dans le CA
   - Le `montant_facture` est une copie du tarif au moment de la diffusion
   - Garantit la cohérence même si le tarif change

2. **Validité des Publicités :**
   - Une publicité ne peut être diffusée que pendant sa période de validité
   - `date_diffusion` doit être entre `date_debut_validite` et `date_fin_validite`

3. **Tarification :**
   - Le tarif appliqué est celui en vigueur à la `date_diffusion`
   - Utiliser la fonction `get_tarif_publicite_actuel(date)` pour récupérer le tarif

---

## Flux de Données - Exemple de Calcul CA

### Scénario : CA Décembre 2025

```
┌──────────────────────────────────────────────────────────┐
│ ÉTAPE 1 : Créer les sociétés publicitaires              │
└──────────────────────────────────────────────────────────┘
    INSERT INTO societe_publicitaire (code, nom)
    VALUES ('SOC-PUB-001', 'Vaniala');
    
    INSERT INTO societe_publicitaire (code, nom)
    VALUES ('SOC-PUB-002', 'Lewis');

┌──────────────────────────────────────────────────────────┐
│ ÉTAPE 2 : Créer les publicités (vidéos)                 │
└──────────────────────────────────────────────────────────┘
    -- Pour Vaniala
    INSERT INTO publicite (code, societe_publicitaire_id, titre)
    VALUES ('PUB-VANIALA-001', 1, 'Yaourts Bio');
    
    -- Pour Lewis
    INSERT INTO publicite (code, societe_publicitaire_id, titre)
    VALUES ('PUB-LEWIS-001', 1, 'Smartphones 2025');

┌──────────────────────────────────────────────────────────┐
│ ÉTAPE 3 : Planifier les diffusions                      │
└──────────────────────────────────────────────────────────┘
    -- Vaniala : 20 diffusions
    FOR i IN 1..20 LOOP
        INSERT INTO depart_publicite (
            depart_id, publicite_id, tarif_publicite_id,
            date_diffusion, montant_facture, statut_diffusion
        ) VALUES (
            <depart_id>, <pub_vaniala_id>, <tarif_id>,
            '2025-12-01'::TIMESTAMP + (i || ' days')::INTERVAL,
            100000.00, 'DIFFUSE'
        );
    END LOOP;
    
    -- Lewis : 10 diffusions
    FOR i IN 1..10 LOOP
        INSERT INTO depart_publicite (...)
        VALUES (..., 100000.00, 'DIFFUSE');
    END LOOP;

┌──────────────────────────────────────────────────────────┐
│ ÉTAPE 4 : Calculer le CA mensuel                        │
└──────────────────────────────────────────────────────────┘
    SELECT 
        societe_nom,
        nombre_diffusions,
        ca_mensuel
    FROM v_ca_mensuel_diffusions
    WHERE annee = 2025 AND mois = 12;

┌──────────────────────────────────────────────────────────┐
│ RÉSULTAT                                                 │
├──────────────────────────────────────────────────────────┤
│ Vaniala  │ 20 diffusions │ 2 000 000 Ar                │
│ Lewis    │ 10 diffusions │ 1 000 000 Ar                │
│──────────────────────────────────────────────────────────│
│ TOTAL    │ 30 diffusions │ 3 000 000 Ar                │
└──────────────────────────────────────────────────────────┘
```

---

## Index de Performance

```sql
-- Recherche par départ
CREATE INDEX idx_depart_publicite_depart 
ON depart_publicite(depart_id);

-- Recherche par publicité
CREATE INDEX idx_depart_publicite_publicite 
ON depart_publicite(publicite_id);

-- Recherche par date (statistiques)
CREATE INDEX idx_depart_publicite_date 
ON depart_publicite(date_diffusion);

-- Jointure société → diffusions
CREATE INDEX idx_depart_publicite_societe 
ON depart_publicite(publicite_id, date_diffusion);

-- Publicités par société
CREATE INDEX idx_publicite_societe 
ON publicite(societe_publicitaire_id);

-- Publicités actives
CREATE INDEX idx_publicite_dates 
ON publicite(date_debut_validite, date_fin_validite) 
WHERE actif = TRUE;

-- Tarif actuel
CREATE INDEX idx_tarif_publicite_actif 
ON tarif_publicite(date_debut, date_fin) 
WHERE actif = TRUE;
```

---

## Volumétrie Estimée

**Hypothèses :**
- 100 départs par jour
- 2 publicités en moyenne par départ
- 365 jours par an

**Volumes annuels :**
- `societe_publicitaire` : ~50 sociétés
- `publicite` : ~200 vidéos actives
- `tarif_publicite` : ~10 lignes (historique)
- `depart_publicite` : **73 000 diffusions/an** (100 × 2 × 365)

**Croissance de la table principale :**
| Année | Diffusions | Taille estimée |
|-------|-----------|----------------|
| An 1  | 73 000    | ~15 MB         |
| An 2  | 146 000   | ~30 MB         |
| An 5  | 365 000   | ~75 MB         |

---

## Vues Matérialisées (Optimisation Future)

Pour améliorer les performances des statistiques :

```sql
-- Vue matérialisée : CA mensuel
CREATE MATERIALIZED VIEW mv_ca_mensuel AS
SELECT * FROM v_ca_mensuel_diffusions;

-- Rafraîchir une fois par jour
REFRESH MATERIALIZED VIEW mv_ca_mensuel;

-- Index sur la vue matérialisée
CREATE INDEX ON mv_ca_mensuel(annee, mois);
```

---

**Version du MCD :** 1.0  
**Date :** 22 janvier 2026  
**Auteur :** Système de Gestion Taxi-Brousse
