Tu es un assistant d’architecture applicative et de génération CRUD.

Contexte :
On a effecture ces modifications une a une pas en une seule fois

Fonctionnalités à couvrir (liste exhaustive) :
1. Réservation de place
   - Critères : date_heure_depart, lieu_depart, lieu_arrivee
2. Gestion des types de place
   - Catégories : standard, premium, VIP
   - Prix variables selon le départ
3. Gestion des remises
   - Catégories passager : enfant, adulte, senior
4. Diffusion publicitaire sur les départs
5. Calcul du chiffre d’affaires par départ
6. Vente de produits extras lors d’un départ

Objectif :
Générer un fichier Markdown complet décrivant l’architecture, les écrans, les CRUD
et la logique métier liés à chacune des fonctionnalités listées ci-dessus,
en parfaite cohérence avec le schéma de données actuel.

Livrables obligatoires (dans UN SEUL fichier Markdown) :

Pour CHAQUE fonctionnalité :

1. Architecture données
   1. Table ou vue principale
   2. Colonnes utilisées
   3. Tables / vues annexes
      - Type de relation (1–N, N–N, etc.)
      - Colonne de liaison
   4. Pour les vues uniquement, détailler :
      3.4.1. Source des données
      3.4.2. Règles de filtrage
      3.4.3. Règles de calcul / agrégation

2. Dessins d’écran (ASCII obligatoire)
   - Liste
   - Détail
   - Modale
   - Étapes spécifiques (paiement, confirmation, diffusion, etc.)

   Exemple de style attendu :
   ┌───────────────────────────┐
   │  Titre                    │
   ├───────────────────────────┤
   │  Contenu                  │
   └───────────────────────────┘

3. Métier / logique applicative
   - Classes utilisées
     - Nom
     - Responsabilité
     - Emplacement (Controller / Service / Domain)
   - Méthodes
     - Nom
     - Arguments
     - Type de retour
     - Description de la logique métier
   - Services impactés

Contraintes :
- Format Markdown obligatoire.
- Maquettes UNIQUEMENT en ASCII.
- Mentionner exclusivement les tables et vues réellement existantes et impactées.
- Ne jamais réintroduire des fonctionnalités supprimées.
- Aucune hypothèse fonctionnelle non justifiée.
- Structure claire, numérotée et homogène pour toutes les fonctionnalités.

Génère le fichier Markdown complet.
