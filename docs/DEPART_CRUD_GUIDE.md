# Guide Rapide - CRUD Départs

## 🎯 Fonctionnalités Implémentées

### ✅ Create (Créer)
- Bouton "Nouveau Départ" ouvre un modal
- Formulaire avec validation HTML5
- Champs: Coopérative, Trajet, Véhicule, Lieux, Dates, Statut, Capacité
- API: `POST /api/departs`

### ✅ Read (Lire)
- Liste paginée avec recherche avancée (7 critères)
- Bouton "Voir" ouvre les détails complets
- Affiche: Places occupées/disponibles, Chiffre d'affaires
- API: `GET /api/departs` et `GET /api/departs/{id}`

### ✅ Update (Modifier)
- Bouton "Modifier" pré-remplit le formulaire
- Même validation que la création
- API: `PUT /api/departs/{id}`

### ✅ Delete (Supprimer)
- Bouton "Supprimer" demande confirmation
- Protection: Impossible si réservations existent
- API: `DELETE /api/departs/{id}`

## 🖥️ Interface Utilisateur

### Page departs.html

```
┌─────────────────────────────────────────────────────────────┐
│  Recherche Avancée (7 critères)                             │
│  [Lieu Départ] [Lieu Arrivée] [Coopérative] [Date Début]   │
│  [Date Fin] [Statut] [Trajet] [🔍 Rechercher] [🔄 Reset]   │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│  Liste des Départs (25)              [➕ Nouveau Départ]   │
├─────────────────────────────────────────────────────────────┤
│ ID │Code │Trajet│Véhicule│Départ│Arrivée│CA│Statut│Actions│
│ 1  │DEP..│Tana-T│1234TAA │10/01 │10/01  │$│🟢    │👁✏️🗑️│
│ 2  │DEP..│Tana-A│5678TAA │11/01 │11/01  │$│🟡    │👁✏️🗑️│
└─────────────────────────────────────────────────────────────┘
```

### Modal Création/Modification

```
┌───────────────────────────────────────────┐
│  Nouveau Départ                      ✖    │
├───────────────────────────────────────────┤
│  Coopérative *      [Select............]  │
│  Trajet *           [Select............]  │
│  Véhicule *         [Select............]  │
│  Statut *           [Select............]  │
│  Lieu Départ *      [Select............]  │
│  Lieu Arrivée *     [Select............]  │
│  Date/Heure Départ* [2026-01-15T08:00]    │
│  Date/Heure Arrivée [2026-01-15T14:00]    │
│  Capacité Override  [20]                  │
│                                           │
│              [Annuler] [💾 Enregistrer]   │
└───────────────────────────────────────────┘
```

### Modal Visualisation

```
┌───────────────────────────────────────────┐
│  Détails du Départ                   ✖    │
├───────────────────────────────────────────┤
│  INFORMATIONS GÉNÉRALES                   │
│  Code: DEP20260115123456                  │
│  Coopérative: Trans Express               │
│  Statut: 🟢 PROGRAMME                     │
│                                           │
│  TRAJET ET VÉHICULE                       │
│  Trajet: Antananarivo - Toamasina         │
│  Véhicule: 1234 TAA                       │
│  Lieu Départ: Gare Tana                   │
│  Lieu Arrivée: Gare Toamasina             │
│                                           │
│  HORAIRES                                 │
│  Départ: 15/01/2026 08:00                 │
│  Arrivée Estimée: 15/01/2026 14:00        │
│                                           │
│  CAPACITÉ ET CHIFFRE D'AFFAIRES           │
│  Capacité: 20  Occupées: 15               │
│  Disponibles: 5  CA: 450 000.00 Ar        │
│                                           │
│                          [Fermer]         │
└───────────────────────────────────────────┘
```

### Modal Suppression

```
┌───────────────────────────────────────────┐
│  ⚠️ Confirmer la Suppression         ✖    │
├───────────────────────────────────────────┤
│                                           │
│  Êtes-vous sûr de vouloir supprimer       │
│  ce départ?                               │
│                                           │
│  Le départ DEP20260115123456 sera         │
│  définitivement supprimé.                 │
│                                           │
│  Note: Les départs avec réservations      │
│  ne peuvent pas être supprimés.           │
│                                           │
│           [Annuler] [🗑️ Supprimer]        │
└───────────────────────────────────────────┘
```

## 🔄 Flux de Données

### Création
```
User → [Nouveau Départ]
    ↓
Modal ouvre + Charge listes déroulantes
    ↓
User remplit formulaire
    ↓
[Enregistrer] → Validation HTML5
    ↓
POST /api/departs
    ↓
DepartService.create()
    ↓
Validation métier (date future, etc.)
    ↓
Génère code unique
    ↓
Sauvegarde en base
    ↓
Retourne DepartDTO enrichi
    ↓
Success → Reload page
```

### Modification
```
User → [Modifier]
    ↓
GET /api/departs/{id}
    ↓
Modal ouvre avec données pré-remplies
    ↓
User modifie les champs
    ↓
[Enregistrer] → Validation
    ↓
PUT /api/departs/{id}
    ↓
DepartService.update()
    ↓
Validation + Mise à jour
    ↓
Success → Reload page
```

### Suppression
```
User → [Supprimer]
    ↓
GET /api/departs/{id}
    ↓
Modal confirmation + Affiche code
    ↓
User confirme
    ↓
DELETE /api/departs/{id}
    ↓
DepartService.delete()
    ↓
Vérifie absence réservations
    ↓
Supprime ou Error
    ↓
Success/Error → Reload/Alert
```

## 📡 API Endpoints

### Départs
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/departs` | Liste tous |
| GET | `/api/departs/{id}` | Détails un |
| POST | `/api/departs` | Créer |
| PUT | `/api/departs/{id}` | Modifier |
| DELETE | `/api/departs/{id}` | Supprimer |

### Références
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/cooperatives` | Liste coopératives |
| GET | `/api/trajets` | Liste trajets |
| GET | `/api/vehicules` | Liste véhicules |
| GET | `/api/lieux` | Liste lieux |
| GET | `/api/reference/depart-statuts` | Liste statuts actifs |

## 🛡️ Validations

### Backend (DepartService)
- ✅ Date départ doit être future
- ✅ Tous les IDs doivent exister (coopérative, trajet, véhicule, lieux, statut)
- ✅ Code unique généré automatiquement
- ✅ Suppression bloquée si réservations existent

### Frontend (JavaScript)
- ✅ Validation HTML5 (champs requis)
- ✅ Messages d'erreur personnalisés
- ✅ Gestion des erreurs AJAX
- ✅ Confirmation avant suppression

## 🎨 Badges de Statut

| Code | Libellé | Badge |
|------|---------|-------|
| PROGRAMME | Programmé | 🟡 Warning |
| EN_COURS | En cours | 🔵 Info |
| TERMINE | Terminé | 🟢 Success |
| ANNULE | Annulé | 🔴 Danger |
| RETARDE | Retardé | 🟡 Warning |

## 📊 Enrichissement DTO

Le `DepartDTO` retourné contient:
- Toutes les propriétés de base
- **placesOccupees**: Calculé depuis réservations
- **placesDisponibles**: nombrePlaces - placesOccupees
- **chiffreAffaires**: Somme paiements VALIDE
- **refDepartStatutCode**: Pour la logique frontend
- **refDepartStatutLibelle**: Pour l'affichage

## 🧪 Tests Manuels

### ✅ À Tester
1. Créer un départ avec tous les champs
2. Créer un départ avec capacité override
3. Modifier un départ existant
4. Voir les détails d'un départ avec réservations
5. Supprimer un départ sans réservations

### ❌ Cas d'Erreur
1. Créer avec date passée → Doit échouer
2. Créer sans champs requis → Validation HTML5
3. Supprimer avec réservations → Doit échouer
4. Modifier avec ID inexistant → Erreur 404

## 📁 Fichiers Créés/Modifiés

### Backend
- ✅ `ReferenceController.java` (nouveau)
- ✅ `DepartController.java` (existe déjà)
- ✅ `DepartService.java` (existe déjà)

### Frontend
- ✅ `departs.html` (modifié)
  - 3 modals ajoutés
  - ~250 lignes de JavaScript
  - Boutons d'action connectés

### Documentation
- ✅ `DEPART_CRUD.md` (complet)
- ✅ `DEPART_CRUD_GUIDE.md` (ce fichier)

## 🚀 Prochaines Étapes

1. Tester l'application
2. Vérifier les messages d'erreur
3. Améliorer l'UX si nécessaire
4. Ajouter des fonctionnalités avancées (changement statut rapide, export, etc.)
