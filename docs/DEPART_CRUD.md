# CRUD Complet des Départs - Documentation

## Vue d'ensemble

Ce document décrit l'implémentation complète du CRUD (Create, Read, Update, Delete) pour la gestion des départs dans l'application Taxi Brousse.

## Architecture

### 1. Couche Backend (API REST)

#### Controller REST: `DepartController.java`
**Endpoint base:** `/api/departs`

| Méthode | Endpoint | Description | Corps de la requête |
|---------|----------|-------------|---------------------|
| GET | `/api/departs` | Liste tous les départs | - |
| GET | `/api/departs/{id}` | Récupère un départ par ID | - |
| GET | `/api/departs/disponibles` | Liste les départs disponibles | - |
| GET | `/api/departs/trajet/{trajetId}` | Liste les départs d'un trajet | - |
| POST | `/api/departs` | Crée un nouveau départ | DepartDTO |
| PUT | `/api/departs/{id}` | Modifie un départ existant | DepartDTO |
| DELETE | `/api/departs/{id}` | Supprime un départ | - |
| GET | `/api/departs/{id}/tarif` | Récupère le tarif d'un départ | - |

#### Controller Référence: `ReferenceController.java`
**Endpoint base:** `/api/reference`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/reference/depart-statuts` | Liste les statuts actifs |
| GET | `/api/reference/depart-statuts/all` | Liste tous les statuts |
| GET | `/api/reference/depart-statuts/{id}` | Récupère un statut par ID |

#### Service: `DepartService.java`

Le service enrichit les DTOs avec:
- **placesOccupees**: Nombre de places réservées
- **placesDisponibles**: Nombre de places libres
- **chiffreAffaires**: Somme des paiements validés

#### Validations Métier

1. **Création:**
   - La date/heure de départ doit être dans le futur
   - Tous les champs obligatoires doivent être renseignés
   - Les relations (coopérative, trajet, véhicule, lieux, statut) doivent exister

2. **Modification:**
   - Même validation que la création
   - Le départ doit exister

3. **Suppression:**
   - Le départ ne doit pas avoir de réservations existantes
   - Protection automatique via contraintes de clé étrangère

### 2. Couche Frontend (Interface Web)

#### Page: `departs.html`

La page contient:
- **Formulaire de recherche** avec 7 critères (lieu départ, lieu arrivée, coopérative, trajet, statut, dates)
- **Tableau de liste** avec colonnes: ID, Code, Trajet, Véhicule, Dates, Chiffre d'affaires, Statut, Actions
- **3 Modals** pour les opérations CRUD

#### Modal 1: Création/Modification de Départ

**ID Modal:** `#departModal`

**Champs du formulaire:**
- Coopérative * (select)
- Trajet * (select)
- Véhicule * (select)
- Statut * (select)
- Lieu Départ * (select)
- Lieu Arrivée * (select)
- Date/Heure Départ * (datetime-local)
- Date/Heure Arrivée Estimée (datetime-local, optionnel)
- Capacité Override (number, optionnel)

**Validation:**
- Validation HTML5 native
- Classe `was-validated` pour afficher les erreurs
- Messages d'erreur personnalisés via `invalid-feedback`

#### Modal 2: Visualisation des Détails

**ID Modal:** `#viewDepartModal`

**Sections affichées:**
1. **Informations Générales**: Code, Coopérative, Statut
2. **Trajet et Véhicule**: Trajet, Véhicule, Lieux
3. **Horaires**: Date/Heure départ et arrivée estimée
4. **Capacité et CA**: Places totales/occupées/disponibles, Chiffre d'affaires

**Affichage:**
- Badges colorés pour les statuts:
  - `badge-success`: TERMINE
  - `badge-info`: EN_COURS
  - `badge-danger`: ANNULE
  - `badge-warning`: PROGRAMME, RETARDE

#### Modal 3: Confirmation de Suppression

**ID Modal:** `#deleteDepartModal`

**Contenu:**
- Message d'avertissement avec icône
- Code du départ à supprimer
- Note sur l'impossibilité de supprimer si réservations existent
- Boutons: Annuler / Supprimer

### 3. JavaScript - Fonctions CRUD

#### Initialisation

```javascript
$(document).ready(function() {
  loadReferenceData(); // Charge les listes déroulantes
});
```

#### Fonction: `loadReferenceData()`

Charge toutes les données de référence en parallèle:
- Coopératives: `/api/cooperatives`
- Trajets: `/api/trajets`
- Véhicules: `/api/vehicules`
- Lieux: `/api/lieux`
- Statuts: `/api/reference/depart-statuts`

#### Fonction: `openDepartModal()`

**Appelée par:** Bouton "Nouveau Départ"

**Actions:**
- Réinitialise le formulaire
- Change le titre du modal: "Nouveau Départ"
- Vide l'ID caché `#departId`
- Recharge les listes déroulantes

#### Fonction: `viewDepart(event, departId)`

**Appelée par:** Bouton "Voir" (icône œil)

**Actions:**
1. Récupère les données: `GET /api/departs/{id}`
2. Remplit tous les champs d'affichage
3. Formate les dates: `toLocaleString('fr-FR')`
4. Applique les badges colorés selon le statut
5. Formate le chiffre d'affaires avec 2 décimales
6. Affiche le modal `#viewDepartModal`

#### Fonction: `editDepart(event, departId)`

**Appelée par:** Bouton "Modifier" (icône crayon)

**Actions:**
1. Récupère les données: `GET /api/departs/{id}`
2. Change le titre du modal: "Modifier le Départ"
3. Remplit tous les champs du formulaire
4. Formate les dates pour `datetime-local`: `yyyy-MM-ddTHH:mm`
5. Stocke l'ID dans `#departId`
6. Affiche le modal `#departModal`

#### Fonction: `saveDepart()`

**Appelée par:** Bouton "Enregistrer" du modal

**Actions:**
1. Valide le formulaire (HTML5 validation)
2. Construit l'objet `departData` avec tous les champs
3. Détermine la méthode:
   - `POST /api/departs` si nouveau (pas d'ID)
   - `PUT /api/departs/{id}` si modification
4. Envoie la requête AJAX avec `Content-Type: application/json`
5. Affiche un message de succès
6. Recharge la page: `location.reload()`

**Structure de departData:**
```json
{
  "cooperativeId": 1,
  "trajetId": 2,
  "vehiculeId": 3,
  "lieuDepartId": 4,
  "lieuArriveeId": 5,
  "refDepartStatutId": 1,
  "dateHeureDepart": "2026-01-15T08:00",
  "dateHeureArriveeEstimee": "2026-01-15T14:00",
  "capaciteOverride": 20
}
```

#### Fonction: `confirmDeleteDepart(event, departId)`

**Appelée par:** Bouton "Supprimer" (icône poubelle)

**Actions:**
1. Récupère les données: `GET /api/departs/{id}`
2. Affiche le code dans `#deleteCode`
3. Stocke l'ID dans `#deleteDepartId`
4. Affiche le modal `#deleteDepartModal`

#### Fonction: `deleteDepart()`

**Appelée par:** Bouton "Supprimer" du modal de confirmation

**Actions:**
1. Récupère l'ID depuis `#deleteDepartId`
2. Envoie la requête: `DELETE /api/departs/{id}`
3. Affiche un message de succès
4. Recharge la page: `location.reload()`
5. En cas d'erreur (ex: réservations existantes), affiche le message d'erreur

### 4. Gestion des Erreurs

#### Backend (DepartService)

- `ResourceNotFoundException`: Départ, coopérative, trajet, véhicule, lieu ou statut non trouvé
- `IllegalArgumentException`: Date de départ dans le passé
- `DataIntegrityViolationException`: Suppression impossible (réservations existantes)

#### Frontend (JavaScript)

- Validation HTML5 avec messages personnalisés
- Gestion des erreurs AJAX avec `xhr.responseJSON?.message`
- Messages `alert()` pour informer l'utilisateur

### 5. Flux de Données

#### Création d'un Départ

```
1. Utilisateur → Clic "Nouveau Départ"
2. JavaScript → openDepartModal()
3. JavaScript → loadReferenceData() → GET /api/{cooperatives,trajets,vehicules,lieux,statuts}
4. Backend → Retourne les listes
5. JavaScript → Remplit les selects
6. Utilisateur → Remplit le formulaire
7. Utilisateur → Clic "Enregistrer"
8. JavaScript → saveDepart() → POST /api/departs
9. Backend → DepartService.create()
10. Backend → Valide les données
11. Backend → Génère un code unique
12. Backend → Sauvegarde en base
13. Backend → Retourne DepartDTO enrichi
14. JavaScript → Affiche succès et recharge la page
```

#### Modification d'un Départ

```
1. Utilisateur → Clic "Modifier" sur une ligne
2. JavaScript → editDepart(departId) → GET /api/departs/{id}
3. Backend → Retourne DepartDTO enrichi
4. JavaScript → Remplit le formulaire avec les données
5. Utilisateur → Modifie les champs
6. Utilisateur → Clic "Enregistrer"
7. JavaScript → saveDepart() → PUT /api/departs/{id}
8. Backend → DepartService.update()
9. Backend → Valide les données
10. Backend → Met à jour en base
11. Backend → Retourne DepartDTO enrichi
12. JavaScript → Affiche succès et recharge la page
```

#### Visualisation d'un Départ

```
1. Utilisateur → Clic "Voir" sur une ligne
2. JavaScript → viewDepart(departId) → GET /api/departs/{id}
3. Backend → Retourne DepartDTO enrichi (avec places et CA)
4. JavaScript → Affiche toutes les informations dans le modal
5. Utilisateur → Clic "Fermer"
```

#### Suppression d'un Départ

```
1. Utilisateur → Clic "Supprimer" sur une ligne
2. JavaScript → confirmDeleteDepart(departId) → GET /api/departs/{id}
3. Backend → Retourne DepartDTO
4. JavaScript → Affiche le modal de confirmation avec le code
5. Utilisateur → Clic "Supprimer" dans le modal
6. JavaScript → deleteDepart() → DELETE /api/departs/{id}
7. Backend → DepartService.delete()
8. Backend → Vérifie l'absence de réservations
9. Backend → Supprime le départ
10. JavaScript → Affiche succès et recharge la page
```

### 6. Points Importants

#### Enrichissement des DTOs

Les DTOs de départ sont enrichis côté service avec:
- `placesOccupees`: Calculé via `ReservationRepository.countPassagersByDepartId()`
- `placesDisponibles`: `nombrePlaces - placesOccupees`
- `chiffreAffaires`: Calculé via `PaiementRepository.sumMontantByDepartId()` (statut VALIDE uniquement)

#### Génération du Code

Le code unique est généré automatiquement par le service:
- Format: `DEPyyyyMMddHHmmss{id}` (ex: DEP20260110143045123)
- Généré lors de la création
- Visible dans le DTO retourné

#### Capacité Override

- Si `capaciteOverride` est renseigné → utilise cette valeur
- Sinon → utilise `vehicule.nombrePlaces`
- Permet de limiter les places pour un départ spécifique

#### Statuts de Départ

5 statuts disponibles (ref_depart_statut):
1. **PROGRAMME**: Départ planifié, pas encore commencé
2. **EN_COURS**: Départ en cours de route
3. **TERMINE**: Départ arrivé à destination
4. **ANNULE**: Départ annulé
5. **RETARDE**: Départ retardé

### 7. Tests et Validation

#### Tests Manuels Recommandés

1. **Création:**
   - ✅ Créer un départ avec tous les champs obligatoires
   - ✅ Créer un départ avec capacité override
   - ✅ Créer un départ sans capacité override
   - ❌ Tenter de créer avec une date passée (doit échouer)
   - ❌ Tenter de créer sans champs obligatoires (validation HTML5)

2. **Modification:**
   - ✅ Modifier tous les champs d'un départ
   - ✅ Modifier uniquement la capacité override
   - ✅ Modifier le statut
   - ❌ Tenter de modifier avec date passée

3. **Visualisation:**
   - ✅ Voir un départ sans réservations (CA = 0.00 Ar)
   - ✅ Voir un départ avec réservations et paiements
   - ✅ Vérifier les badges de statut colorés

4. **Suppression:**
   - ✅ Supprimer un départ sans réservations
   - ❌ Tenter de supprimer un départ avec réservations (doit échouer)

### 8. Extensions Possibles

1. **Changement de statut direct:**
   - Ajouter des boutons pour changer rapidement le statut (Démarrer, Terminer, Annuler)

2. **Vue calendrier:**
   - Afficher les départs dans un calendrier mensuel

3. **Notifications:**
   - Envoyer des notifications aux clients lors de changements de statut

4. **Export:**
   - Exporter la liste des départs en PDF/Excel

5. **Duplication:**
   - Dupliquer un départ pour créer un nouveau départ similaire

6. **Filtres avancés:**
   - Sauvegarder les filtres de recherche
   - Filtres prédéfinis (Aujourd'hui, Cette semaine, etc.)

## Fichiers Modifiés

1. **Backend:**
   - `DepartController.java` - API REST complète (déjà existait)
   - `ReferenceController.java` - API pour statuts (nouveau)
   - `DepartService.java` - Logique métier (déjà existait)

2. **Frontend:**
   - `departs.html` - Page complète avec modals et JavaScript

## Dépendances

- jQuery 3.x
- Bootstrap 4.x
- Font Awesome 5.x
- Spring Boot 3.x
- Thymeleaf
