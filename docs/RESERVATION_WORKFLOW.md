# Documentation du Workflow de Réservation
## Gestion Taxi Brousse - Madagascar

---

## Table des Matières
1. [To-Do List : Effectuer une Réservation](#to-do-list--effectuer-une-réservation)
2. [Pages Utilisées](#pages-utilisées)
3. [Tables de la Base de Données](#tables-de-la-base-de-données)
4. [Logique Métier](#logique-métier)
5. [Controllers](#controllers)
6. [Services](#services)
7. [Repositories](#repositories)
8. [Flux de Données](#flux-de-données)

---

## To-Do List : Effectuer une Réservation

### Réservation Simple (Liste des Départs)
1. ✅ Accéder à la page `/reservations-new`
2. ✅ Consulter la liste des départs disponibles
3. ✅ Sélectionner un départ
4. ✅ Vérifier les informations du départ (trajet, date, tarif, places disponibles)
5. ✅ Sélectionner les places (numéros de siège)
6. ✅ Saisir les informations des passagers (nom, prénom pour chaque place)
7. ✅ Saisir ou sélectionner les informations du client
8. ✅ Choisir le mode de paiement (Immédiat, Comptoir, Embarquement)
9. ✅ Si paiement immédiat : saisir le montant et le mode (Espèces, MVola, Carte)
10. ✅ Valider la réservation
11. ✅ Système crée la réservation avec statut approprié
12. ✅ Système émet les billets si paiement complet

### Réservation avec Critères (Recherche Avancée)
1. ✅ Accéder à la page `/reservations-critere`
2. ✅ Sélectionner le lieu de départ
3. ✅ Sélectionner le lieu d'arrivée
4. ✅ Saisir la date de départ
5. ✅ Saisir l'heure de départ (optionnel)
6. ✅ Rechercher les départs correspondants
7. ✅ Sélectionner un départ dans les résultats filtrés
8. ✅ Suivre les étapes 4-12 de la réservation simple

---

## Pages Utilisées

### 1. Page Réservation Simple
- **Fichier** : `src/main/resources/templates/taxi_brousse/reservations_new.html`
- **URL** : `/reservations-new`
- **Description** : Affiche tous les départs disponibles et permet de créer une réservation directement
- **Fonctionnalités** :
  - Liste des départs disponibles (cards)
  - Sélection de départ
  - Grille de sièges interactive
  - Formulaire de réservation multi-étapes
  - Table récapitulative des réservations

### 2. Page Réservation avec Critères
- **Fichier** : `src/main/resources/templates/taxi_brousse/reservation-critere.html`
- **URL** : `/reservations-critere`
- **Description** : Permet de filtrer les départs par critères avant de réserver
- **Fonctionnalités** :
  - Formulaire de recherche par lieu départ/arrivée + date/heure
  - Filtrage dynamique des départs
  - Affichage du départ sélectionné
  - Grille de sièges interactive
  - Formulaire de réservation multi-étapes

### 3. Page Liste des Réservations
- **Fichier** : `src/main/resources/templates/taxi_brousse/reservations.html`
- **URL** : `/reservations`
- **Description** : Consultation et gestion des réservations existantes

---

## Tables de la Base de Données

### Tables Principales

#### 1. **reservation**
```sql
CREATE TABLE reservation (
    id BIGINT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    depart_id BIGINT NOT NULL,
    ref_reservation_statut_id BIGINT NOT NULL,
    date_creation TIMESTAMP NOT NULL,
    montant_total DECIMAL(12,2),
    montant_paye DECIMAL(12,2),
    reste_a_payer DECIMAL(12,2),
    notes TEXT
);
```
- **Rôle** : Stocke les informations principales de la réservation
- **Relations** :
  - `client_id` → `client(id)`
  - `depart_id` → `depart(id)`
  - `ref_reservation_statut_id` → `ref_reservation_statut(id)`

#### 2. **reservation_passager**
```sql
CREATE TABLE reservation_passager (
    id BIGINT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    depart_id BIGINT NOT NULL,
    nom VARCHAR(120) NOT NULL,
    prenom VARCHAR(120),
    numero_siege INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```
- **Rôle** : Stocke les informations de chaque passager d'une réservation
- **Contrainte** : `UNIQUE(depart_id, numero_siege)` - Un siège ne peut être attribué qu'une fois par départ

#### 3. **paiement**
```sql
CREATE TABLE paiement (
    id BIGINT PRIMARY KEY,
    montant DECIMAL(12,2) NOT NULL,
    ref_devise_id BIGINT NOT NULL,
    ref_paiement_methode_id BIGINT NOT NULL,
    ref_paiement_statut_id BIGINT NOT NULL,
    reference_externe VARCHAR(120),
    date_paiement TIMESTAMP NOT NULL,
    reservation_id BIGINT,
    notes TEXT
);
```
- **Rôle** : Enregistre les paiements effectués
- **Relations** :
  - `ref_devise_id` → `ref_devise(id)` (MGA, EUR, USD)
  - `ref_paiement_methode_id` → `ref_paiement_methode(id)` (Espèces, MVola, Carte)
  - `ref_paiement_statut_id` → `ref_paiement_statut(id)` (Validé, En attente, etc.)
  - `reservation_id` → `reservation(id)`

#### 4. **billet**
```sql
CREATE TABLE billet (
    id BIGINT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    ref_billet_statut_id BIGINT NOT NULL,
    numero VARCHAR(120) NOT NULL UNIQUE,
    code VARCHAR(120),
    passager_nom VARCHAR(120),
    passager_prenom VARCHAR(120),
    numero_siege INTEGER,
    date_emission TIMESTAMP NOT NULL,
    contenu_qr TEXT,
    created_at TIMESTAMP NOT NULL
);
```
- **Rôle** : Billets émis pour chaque passager d'une réservation confirmée
- **Format numéro** : `BIL-{reservationId}-{numeroSiege}-{timestamp}`

#### 5. **depart**
```sql
CREATE TABLE depart (
    id BIGINT PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    cooperative_id BIGINT NOT NULL,
    trajet_id BIGINT NOT NULL,
    vehicule_id BIGINT NOT NULL,
    lieu_depart_id BIGINT,
    lieu_arrivee_id BIGINT,
    ref_depart_statut_id BIGINT NOT NULL,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee_estimee TIMESTAMP,
    capacite_override INTEGER
);
```
- **Rôle** : Départs programmés pour les trajets

#### 6. **client**
```sql
CREATE TABLE client (
    id BIGINT PRIMARY KEY,
    nom VARCHAR(120) NOT NULL,
    prenom VARCHAR(120),
    telephone VARCHAR(30),
    email VARCHAR(120),
    numero_cin VARCHAR(50)
);
```
- **Rôle** : Informations des clients effectuant les réservations

#### 7. **tarif**
```sql
CREATE TABLE tarif (
    id BIGINT PRIMARY KEY,
    cooperative_id BIGINT NOT NULL,
    trajet_id BIGINT NOT NULL,
    ref_vehicule_type_id BIGINT NOT NULL,
    ref_devise_id BIGINT NOT NULL,
    montant DECIMAL(12,2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE
);
```
- **Rôle** : Tarification par coopérative, trajet et type de véhicule

### Tables de Référence

#### ref_reservation_statut
- `CONFIRMEE` : Réservation confirmée (paiement complet)
- `EN_ATTENTE` : En attente de paiement (comptoir/embarquement)
- `ANNULEE` : Réservation annulée
- `BROUILLON` : Brouillon (non utilisé actuellement)
- `EXPIREE` : Réservation expirée

#### ref_paiement_methode
- `ESPECES` : Paiement en espèces
- `MVOLA` : Paiement mobile MVola
- `ORANGE_MONEY` : Paiement Orange Money
- `AIRTEL_MONEY` : Paiement Airtel Money
- `CARTE` : Paiement par carte bancaire
- `VIREMENT` : Virement bancaire

#### ref_paiement_statut
- `VALIDE` : Paiement validé
- `EN_ATTENTE` : Paiement en attente
- `ECHOUE` : Paiement échoué
- `REMBOURSE` : Paiement remboursé
- `PARTIEL` : Paiement partiel

#### ref_billet_statut
- `EMIS` : Billet émis
- `UTILISE` : Billet utilisé
- `ANNULE` : Billet annulé
- `EXPIRE` : Billet expiré

#### ref_devise
- `MGA` : Ariary Malagasy (devise par défaut)
- `EUR` : Euro
- `USD` : Dollar US

---

## Logique Métier

### Requêtes Principales

#### 1. Récupération des Départs Disponibles
```java
// DepartRepository.java
List<Depart> findAll()
```
- **Filtrage applicatif** : Date > maintenant && Statut = PROGRAMME
- **Enrichissement** : Places occupées, places disponibles

#### 2. Recherche de Tarif Applicable
```java
// TarifRepository.java
@Query("SELECT t FROM Tarif t " +
       "WHERE t.cooperative.id = :cooperativeId " +
       "AND t.trajet.id = :trajetId " +
       "AND t.refVehiculeType.id = :vehiculeTypeId " +
       "AND t.dateDebut <= :dateReference " +
       "AND (t.dateFin IS NULL OR t.dateFin >= :dateReference) " +
       "ORDER BY t.dateDebut DESC")
Optional<Tarif> findApplicableTarif(
    @Param("cooperativeId") Long cooperativeId,
    @Param("trajetId") Long trajetId,
    @Param("vehiculeTypeId") Long vehiculeTypeId,
    @Param("dateReference") LocalDate dateReference
)
```
- **Critères** : Coopérative + Trajet + Type véhicule + Date dans période de validité
- **Tri** : Par date de début décroissante (tarif le plus récent)

#### 3. Vérification Places Disponibles
```java
// ReservationRepository.java
@Query("SELECT COUNT(rp) FROM ReservationPassager rp WHERE rp.depart.id = :departId")
Long countPassagersByDepartId(@Param("departId") Long departId)
```
- **Utilisation** : Compare avec la capacité du véhicule

#### 4. Vérification Siège Déjà Occupé
```java
// ReservationPassagerRepository.java
boolean existsByDepartIdAndNumeroSiege(Long departId, Integer numeroSiege)
```
- **Contrainte** : Un siège ne peut être réservé qu'une fois par départ

#### 5. Récupération des Lieux (pour recherche avec critères)
```java
// LieuRepository.java
List<Lieu> findAll()
```
- **Utilisation** : Popule les dropdowns de recherche

---

## Controllers

### ReservationController

**Fichier** : `src/main/java/com/taxi_brousse/controller/ReservationController.java`

#### Endpoints REST

##### 1. Créer une Réservation
```java
@PostMapping("/api/reservations")
public ResponseEntity<ReservationDTO> creerReservation(
    @RequestBody CreerReservationRequest request
)
```
- **Méthode HTTP** : POST
- **URL** : `/api/reservations`
- **Body** : `CreerReservationRequest`
  ```json
  {
    "clientId": 1,
    "departId": 5,
    "passagers": [
      {"nom": "RAKOTO", "prenom": "Jean", "numeroSiege": 1},
      {"nom": "RABE", "prenom": "Marie", "numeroSiege": 2}
    ],
    "modePaiement": "PAIEMENT_IMMEDIAT",
    "paiementInfo": {
      "montant": 50000.00,
      "modePaiement": "ESPECES"
    }
  }
  ```
- **Retour** : `ResponseEntity<ReservationDTO>` (201 Created)
- **Logique** :
  1. Validation des données
  2. Appel `reservationService.creerReservation(request)`
  3. Retour de la réservation créée

##### 2. Obtenir les Places Disponibles
```java
@GetMapping("/api/reservations/depart/{departId}/places-disponibles")
public ResponseEntity<List<Integer>> getPlacesDisponibles(
    @PathVariable Long departId
)
```
- **Méthode HTTP** : GET
- **URL** : `/api/reservations/depart/{departId}/places-disponibles`
- **Retour** : `List<Integer>` - Liste des numéros de sièges disponibles
- **Logique** :
  1. Récupère la capacité du véhicule
  2. Récupère les sièges occupés
  3. Retourne la différence

##### 3. Lister les Réservations
```java
@GetMapping("/api/reservations")
public ResponseEntity<List<ReservationDTO>> getAllReservations()
```
- **Méthode HTTP** : GET
- **URL** : `/api/reservations`
- **Retour** : `List<ReservationDTO>`

##### 4. Obtenir une Réservation
```java
@GetMapping("/api/reservations/{id}")
public ResponseEntity<ReservationDTO> getReservation(
    @PathVariable Long id
)
```
- **Méthode HTTP** : GET
- **URL** : `/api/reservations/{id}`
- **Retour** : `ReservationDTO`

##### 5. Annuler une Réservation
```java
@DeleteMapping("/api/reservations/{id}")
public ResponseEntity<Void> annulerReservation(
    @PathVariable Long id
)
```
- **Méthode HTTP** : DELETE
- **URL** : `/api/reservations/{id}`
- **Retour** : 204 No Content
- **Logique** : Change le statut à ANNULEE

#### Endpoints de Pages Web

##### 1. Page Réservations
```java
@GetMapping("/reservations")
public String showReservations(Model model)
```
- **URL** : `/reservations`
- **Retour** : `reservations.html`

##### 2. Page Nouvelle Réservation
```java
@GetMapping("/reservations-new")
public String showNewReservation(Model model)
```
- **URL** : `/reservations-new`
- **Retour** : `reservations_new.html`

##### 3. Page Réservation avec Critères
```java
@GetMapping("/reservations-critere")
public String showReservationCritere(Model model)
```
- **URL** : `/reservations-critere`
- **Retour** : `reservation-critere.html`

---

### DepartController

**Fichier** : `src/main/java/com/taxi_brousse/controller/DepartController.java`

#### Endpoints REST

##### 1. Lister les Départs Disponibles
```java
@GetMapping("/api/departs/disponibles")
public ResponseEntity<List<DepartDTO>> getDepartsDisponibles()
```
- **Méthode HTTP** : GET
- **URL** : `/api/departs/disponibles`
- **Retour** : `List<DepartDTO>`
- **Filtres** : Date > maintenant && Statut = PROGRAMME

##### 2. Obtenir un Départ
```java
@GetMapping("/api/departs/{id}")
public ResponseEntity<DepartDTO> getDepart(
    @PathVariable Long id
)
```
- **Méthode HTTP** : GET
- **URL** : `/api/departs/{id}`
- **Retour** : `DepartDTO` enrichi (places occupées/disponibles)

##### 3. Obtenir le Tarif d'un Départ
```java
@GetMapping("/api/departs/{id}/tarif")
public ResponseEntity<BigDecimal> getTarifByDepart(
    @PathVariable Long id
)
```
- **Méthode HTTP** : GET
- **URL** : `/api/departs/{id}/tarif`
- **Retour** : `BigDecimal` - Montant du tarif
- **Logique** : Recherche avec coopérative + trajet + type véhicule + date

---

### LieuController

**Fichier** : `src/main/java/com/taxi_brousse/controller/LieuController.java`

#### Endpoints REST

##### 1. Lister les Lieux
```java
@GetMapping("/api/lieux")
public ResponseEntity<List<LieuDTO>> getAllLieux()
```
- **Méthode HTTP** : GET
- **URL** : `/api/lieux`
- **Retour** : `List<LieuDTO>`
- **Utilisation** : Popule les dropdowns de recherche

##### 2. Obtenir un Lieu
```java
@GetMapping("/api/lieux/{id}")
public ResponseEntity<LieuDTO> getLieu(
    @PathVariable Long id
)
```
- **Méthode HTTP** : GET
- **URL** : `/api/lieux/{id}`
- **Retour** : `LieuDTO`

---

### ClientController

**Fichier** : `src/main/java/com/taxi_brousse/controller/ClientController.java`

#### Endpoints REST

##### 1. Rechercher un Client
```java
@GetMapping("/api/clients/search")
public ResponseEntity<List<ClientDTO>> searchClients(
    @RequestParam(required = false) String telephone,
    @RequestParam(required = false) String nom
)
```
- **Méthode HTTP** : GET
- **URL** : `/api/clients/search?telephone=034...&nom=RAKOTO`
- **Retour** : `List<ClientDTO>`

##### 2. Créer un Client
```java
@PostMapping("/api/clients")
public ResponseEntity<ClientDTO> createClient(
    @RequestBody ClientDTO clientDTO
)
```
- **Méthode HTTP** : POST
- **URL** : `/api/clients`
- **Body** : `ClientDTO`
- **Retour** : `ClientDTO` (201 Created)

---

## Services

### ReservationService

**Fichier** : `src/main/java/com/taxi_brousse/service/ReservationService.java`

#### Méthodes Principales

##### 1. Créer une Réservation
```java
@Transactional
public ReservationDTO creerReservation(CreerReservationRequest request)
```
- **Arguments** :
  - `CreerReservationRequest request` : Données de la réservation
- **Retour** : `ReservationDTO`
- **Logique** :
  1. **Validation du Client** : Vérifie que le client existe
  2. **Validation du Départ** : Vérifie que le départ existe et est futur
  3. **Vérification des Places** :
     - Calcule les places occupées
     - Vérifie que suffisamment de places sont disponibles
  4. **Vérification des Sièges** :
     - Pour chaque passager, vérifie que le siège n'est pas déjà pris
  5. **Calcul du Tarif** :
     - Recherche le tarif applicable (coopérative + trajet + type véhicule + date)
     - Calcule montant total = tarif × nombre de passagers
  6. **Détermination du Statut** :
     - Si `PAIEMENT_IMMEDIAT` : Statut = `CONFIRMEE`
     - Si `COMPTOIR` ou `EMBARQUEMENT` : Statut = `EN_ATTENTE`
  7. **Création de la Réservation** :
     - Génère un code unique
     - Définit les montants (total, payé, reste à payer)
     - Sauvegarde en base
  8. **Création des Passagers** :
     - Pour chaque passager du request
     - Crée un `ReservationPassager` avec nom, prénom, numéro siège
     - Sauvegarde en base
  9. **Traitement du Paiement** (si immédiat) :
     - Appelle `creerPaiement()`
  10. **Émission des Billets** (si confirmée) :
      - Appelle `emettreBillets()`
  11. **Retour** : Réservation enrichie avec passagers

##### 2. Créer un Paiement
```java
private Paiement creerPaiement(Reservation reservation, PaiementInfo paiementInfo)
```
- **Arguments** :
  - `Reservation reservation` : Réservation concernée
  - `PaiementInfo paiementInfo` : Informations de paiement
- **Retour** : `Paiement`
- **Logique** :
  1. **Récupération de la Devise** : MGA par défaut
  2. **Mapping du Mode de Paiement** :
     - `ESPECES` → `ESPECES`
     - `MOBILE_MONEY` → `MVOLA`
     - `CARTE_BANCAIRE` → `CARTE`
  3. **Récupération de la Méthode** : Via `RefPaiementMethodeRepository`
  4. **Récupération du Statut** : `VALIDE`
  5. **Création du Paiement** :
     - Définit montant, devise, méthode, statut
     - Lie à la réservation
     - Sauvegarde en base
  6. **Retour** : Paiement créé

##### 3. Émettre les Billets
```java
private void emettreBillets(Reservation reservation, List<ReservationPassager> passagers)
```
- **Arguments** :
  - `Reservation reservation` : Réservation concernée
  - `List<ReservationPassager> passagers` : Liste des passagers
- **Retour** : `void`
- **Logique** :
  1. **Récupération du Statut Billet** : `EMIS`
  2. **Pour chaque passager** :
     - Vérifie qu'un billet n'existe pas déjà pour ce siège
     - Crée un billet :
       - Génère numéro unique : `BIL-{resaId}-{siege}-{timestamp}`
       - Définit code, numéro, nom, prénom, siège
       - Définit date d'émission
       - Lie au statut `EMIS`
       - Sauvegarde en base

##### 4. Générer un Numéro de Billet
```java
private String genererNumeroBillet(Reservation reservation, ReservationPassager passager)
```
- **Arguments** :
  - `Reservation reservation`
  - `ReservationPassager passager`
- **Retour** : `String` - Format : `BIL-{resaId}-{siege}-{yyyyMMddHHmmss}`

##### 5. Obtenir les Places Disponibles
```java
public List<Integer> getPlacesDisponibles(Long departId)
```
- **Arguments** :
  - `Long departId` : ID du départ
- **Retour** : `List<Integer>` - Numéros de sièges disponibles
- **Logique** :
  1. Récupère le départ
  2. Détermine la capacité (override ou capacité du véhicule)
  3. Récupère les sièges occupés
  4. Retourne les numéros non occupés (1 à capacité)

##### 6. Annuler une Réservation
```java
public void annulerReservation(Long id)
```
- **Arguments** :
  - `Long id` : ID de la réservation
- **Retour** : `void`
- **Logique** :
  1. Récupère la réservation
  2. Vérifie que le départ n'est pas passé
  3. Change le statut à `ANNULEE`
  4. Sauvegarde

##### 7. Mapper Mode de Paiement
```java
private String mapModeToMethodeCode(String modePaiement)
```
- **Arguments** :
  - `String modePaiement` : Mode depuis le frontend
- **Retour** : `String` - Code pour la table de référence
- **Mapping** :
  - `ESPECES` → `ESPECES`
  - `MOBILE_MONEY` → `MVOLA`
  - `CARTE_BANCAIRE` → `CARTE`
  - `VIREMENT` → `VIREMENT`
  - Autres → `ESPECES` (défaut)

---

### DepartService

**Fichier** : `src/main/java/com/taxi_brousse/service/DepartService.java`

#### Méthodes Principales

##### 1. Lister les Départs Disponibles
```java
public List<DepartDTO> findDepartsDisponibles()
```
- **Arguments** : Aucun
- **Retour** : `List<DepartDTO>`
- **Logique** :
  1. Récupère tous les départs
  2. Filtre : date > maintenant && statut = PROGRAMME
  3. Enrichit avec places occupées/disponibles
  4. Retourne la liste

##### 2. Obtenir un Départ
```java
public DepartDTO findById(Long id)
```
- **Arguments** :
  - `Long id` : ID du départ
- **Retour** : `DepartDTO` enrichi

##### 3. Obtenir le Tarif d'un Départ
```java
public Tarif getTarifByDepartId(Long departId)
```
- **Arguments** :
  - `Long departId` : ID du départ
- **Retour** : `Tarif`
- **Logique** :
  1. Récupère le départ
  2. Extrait coopérative ID, trajet ID, type véhicule ID
  3. Valide que toutes les infos sont présentes
  4. Recherche tarif applicable via `tarifRepository.findApplicableTarif()`
  5. Retourne le tarif ou erreur si non trouvé

##### 4. Enrichir un DepartDTO
```java
private DepartDTO enrichDepartDTO(DepartDTO dto)
```
- **Arguments** :
  - `DepartDTO dto` : DTO à enrichir
- **Retour** : `DepartDTO` enrichi
- **Logique** :
  1. Compte les places occupées via `reservationRepository.countPassagersByDepartId()`
  2. Calcule places disponibles = capacité - occupées
  3. Définit les valeurs dans le DTO
  4. Retourne le DTO

---

## Repositories

### ReservationRepository
```java
public interface ReservationRepository extends JpaRepository<Reservation, Long>
```
- **Méthodes** :
  - `findAll()` : Liste toutes les réservations
  - `findById(Long id)` : Trouve par ID
  - `findByClientId(Long clientId)` : Trouve par client
  - `findByDepartId(Long departId)` : Trouve par départ
  - `countPassagersByDepartId(Long departId)` : Compte passagers par départ
  - `save(Reservation)` : Sauvegarde/mise à jour

### ReservationPassagerRepository
```java
public interface ReservationPassagerRepository extends JpaRepository<ReservationPassager, Long>
```
- **Méthodes** :
  - `findByReservationId(Long reservationId)` : Trouve passagers d'une réservation
  - `findByDepartId(Long departId)` : Trouve passagers d'un départ
  - `existsByDepartIdAndNumeroSiege(Long departId, Integer numeroSiege)` : Vérifie si siège occupé
  - `save(ReservationPassager)` : Sauvegarde

### TarifRepository
```java
public interface TarifRepository extends JpaRepository<Tarif, Long>
```
- **Méthodes** :
  - `findTop1ByTrajetIdOrderByIdAsc(Long trajetId)` : Trouve tarif par trajet (fallback)
  - `findApplicableTarif(...)` : Recherche tarif applicable (requête JPQL)
  - `save(Tarif)` : Sauvegarde

### PaiementRepository
```java
public interface PaiementRepository extends JpaRepository<Paiement, Long>
```
- **Méthodes** :
  - `findByReservationId(Long reservationId)` : Trouve paiements d'une réservation
  - `save(Paiement)` : Sauvegarde

### BilletRepository
```java
public interface BilletRepository extends JpaRepository<Billet, Long>
```
- **Méthodes** :
  - `findByReservationId(Long reservationId)` : Trouve billets d'une réservation
  - `existsByReservationIdAndNumeroSiege(...)` : Vérifie si billet existe
  - `save(Billet)` : Sauvegarde

### DepartRepository
```java
public interface DepartRepository extends JpaRepository<Depart, Long>
```
- **Méthodes** :
  - `findAll()` : Liste tous les départs
  - `findById(Long id)` : Trouve par ID
  - `save(Depart)` : Sauvegarde

### ClientRepository
```java
public interface ClientRepository extends JpaRepository<Client, Long>
```
- **Méthodes** :
  - `findById(Long id)` : Trouve par ID
  - `findByTelephoneContaining(String telephone)` : Recherche par téléphone
  - `findByNomContainingIgnoreCase(String nom)` : Recherche par nom
  - `save(Client)` : Sauvegarde

### LieuRepository
```java
public interface LieuRepository extends JpaRepository<Lieu, Long>
```
- **Méthodes** :
  - `findAll()` : Liste tous les lieux
  - `findById(Long id)` : Trouve par ID

### Repositories de Référence

#### RefReservationStatutRepository
```java
Optional<RefReservationStatut> findTop1ByCodeOrderByIdAsc(String code)
```
- **Codes** : `CONFIRMEE`, `EN_ATTENTE`, `ANNULEE`

#### RefPaiementMethodeRepository
```java
Optional<RefPaiementMethode> findTop1ByCodeOrderByIdAsc(String code)
```
- **Codes** : `ESPECES`, `MVOLA`, `CARTE`, etc.

#### RefPaiementStatutRepository
```java
Optional<RefPaiementStatut> findTop1ByCodeOrderByIdAsc(String code)
```
- **Codes** : `VALIDE`, `EN_ATTENTE`, `ECHOUE`

#### RefBilletStatutRepository
```java
Optional<RefBilletStatut> findTop1ByCodeOrderByIdAsc(String code)
```
- **Codes** : `EMIS`, `UTILISE`, `ANNULE`, `EXPIRE`

#### RefDeviseRepository
```java
Optional<RefDevise> findTop1ByCodeOrderByIdAsc(String code)
```
- **Codes** : `MGA`, `EUR`, `USD`

---

## Flux de Données

### Flux Complet : Créer une Réservation avec Paiement Immédiat

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT (Frontend)                            │
│                     reservations_new.html                            │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 │ 1. POST /api/reservations
                                 │    Body: CreerReservationRequest
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      ReservationController                           │
│                 creerReservation(@RequestBody)                       │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 │ 2. Appel service
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       ReservationService                             │
│                    creerReservation(request)                         │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 1. Validation Client                                         │  │
│  │    clientRepository.findById(request.clientId)               │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 2. Validation Départ                                         │  │
│  │    departRepository.findById(request.departId)               │  │
│  │    Vérifie: dateHeureDepart > maintenant                     │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 3. Vérification Places                                       │  │
│  │    reservationRepository.countPassagersByDepartId()          │  │
│  │    Vérifie: placesOccupées + nouvelles ≤ capacité           │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 4. Vérification Sièges                                       │  │
│  │    Pour chaque passager:                                     │  │
│  │      reservationPassagerRepository                           │  │
│  │        .existsByDepartIdAndNumeroSiege()                     │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 5. Calcul Tarif                                              │  │
│  │    departService.getTarifByDepartId()                        │  │
│  │      ↓                                                        │  │
│  │    tarifRepository.findApplicableTarif(                      │  │
│  │      cooperativeId, trajetId, vehiculeTypeId, date)          │  │
│  │    montantTotal = tarif × nbPassagers                        │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 6. Détermination Statut                                      │  │
│  │    Si PAIEMENT_IMMEDIAT:                                     │  │
│  │      refReservationStatutRepository                          │  │
│  │        .findTop1ByCodeOrderByIdAsc("CONFIRMEE")              │  │
│  │    Sinon (COMPTOIR/EMBARQUEMENT):                            │  │
│  │      refReservationStatutRepository                          │  │
│  │        .findTop1ByCodeOrderByIdAsc("EN_ATTENTE")             │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 7. Création Réservation                                      │  │
│  │    Reservation reservation = new Reservation()               │  │
│  │    - code = "RES-" + timestamp                               │  │
│  │    - client, depart, statut                                  │  │
│  │    - montantTotal, montantPaye, resteAPayer                  │  │
│  │    reservationRepository.save(reservation)                   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 8. Création Passagers                                        │  │
│  │    Pour chaque passager du request:                          │  │
│  │      ReservationPassager passager = new ...                  │  │
│  │      - reservation, depart                                   │  │
│  │      - nom, prenom, numeroSiege                              │  │
│  │      reservationPassagerRepository.save(passager)            │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 9. Traitement Paiement (si IMMEDIAT)                         │  │
│  │    creerPaiement(reservation, request.paiementInfo)          │  │
│  │      ↓                                                        │  │
│  │    refDeviseRepository.findTop1ByCodeOrderByIdAsc("MGA")     │  │
│  │    mapModeToMethodeCode(paiementInfo.modePaiement)           │  │
│  │    refPaiementMethodeRepository                              │  │
│  │      .findTop1ByCodeOrderByIdAsc(code)                       │  │
│  │    refPaiementStatutRepository                               │  │
│  │      .findTop1ByCodeOrderByIdAsc("VALIDE")                   │  │
│  │    Paiement paiement = new Paiement()                        │  │
│  │    paiementRepository.save(paiement)                         │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 10. Émission Billets (si CONFIRMEE)                          │  │
│  │    emettreBillets(reservation, passagers)                    │  │
│  │      ↓                                                        │  │
│  │    refBilletStatutRepository                                 │  │
│  │      .findTop1ByCodeOrderByIdAsc("EMIS")                     │  │
│  │    Pour chaque passager:                                     │  │
│  │      billetRepository                                        │  │
│  │        .existsByReservationIdAndNumeroSiege()                │  │
│  │      Si n'existe pas:                                        │  │
│  │        Billet billet = new Billet()                          │  │
│  │        - numero = "BIL-{resaId}-{siege}-{ts}"                │  │
│  │        - reservation, statut EMIS                            │  │
│  │        - passagerNom, passagerPrenom, numeroSiege            │  │
│  │        - dateEmission                                        │  │
│  │        billetRepository.save(billet)                         │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                                                      │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │ 11. Enrichissement & Retour                                  │  │
│  │    reservationMapper.toDTO(savedReservation)                 │  │
│  │    enrichReservationDTO(dto)                                 │  │
│  │    return dto                                                │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 │ 3. Retour ReservationDTO
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      ReservationController                           │
│              return ResponseEntity.status(201).body(dto)             │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 │ 4. Response 201 Created + JSON
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT (Frontend)                            │
│                  Affiche confirmation + billets                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Flux Simplifié : Obtenir les Places Disponibles

```
CLIENT                    Controller                Service                Repository
  │                          │                        │                       │
  │  GET /api/reservations/  │                        │                       │
  │  depart/5/places-       │                        │                       │
  │  disponibles             │                        │                       │
  ├─────────────────────────►│                        │                       │
  │                          │ getPlacesDisponibles() │                       │
  │                          ├───────────────────────►│                       │
  │                          │                        │ findById(5)           │
  │                          │                        ├──────────────────────►│
  │                          │                        │◄──────────────────────┤
  │                          │                        │   Depart              │
  │                          │                        │                       │
  │                          │                        │ countPassagersByDepartId(5)
  │                          │                        ├──────────────────────►│
  │                          │                        │◄──────────────────────┤
  │                          │                        │   3 (occupées)        │
  │                          │                        │                       │
  │                          │                        │ findByDepartId(5)     │
  │                          │                        ├──────────────────────►│
  │                          │                        │◄──────────────────────┤
  │                          │                        │ [pass1, pass2, pass3] │
  │                          │                        │                       │
  │                          │                        │ Calcul:               │
  │                          │                        │ Capacité = 15         │
  │                          │                        │ Occupés = [1,3,5]     │
  │                          │                        │ Disponibles =         │
  │                          │                        │ [2,4,6,7...15]        │
  │                          │◄───────────────────────┤                       │
  │                          │   List<Integer>        │                       │
  │◄─────────────────────────┤                        │                       │
  │  [2,4,6,7,8,9,10,11,    │                        │                       │
  │   12,13,14,15]           │                        │                       │
```

---

## Résumé des Validations et Règles Métier

### Validations lors de la Création de Réservation

1. **Client** : Doit exister dans la base
2. **Départ** : 
   - Doit exister
   - Date de départ doit être future
3. **Places** :
   - Nombre de places demandées ≤ places disponibles
   - Places disponibles = Capacité véhicule - Places déjà réservées
4. **Sièges** :
   - Chaque numéro de siège doit être ≥ 1
   - Chaque numéro de siège doit être ≤ capacité
   - Chaque siège ne peut être réservé qu'une fois par départ (contrainte UNIQUE)
5. **Tarif** :
   - Un tarif doit exister pour : coopérative + trajet + type véhicule + date
   - Date départ doit être dans la période de validité du tarif
6. **Paiement** (si immédiat) :
   - Montant payé doit être ≥ montant total

### Règles de Statut

| Mode Paiement | Statut Réservation | Billets Émis |
|---------------|-------------------|--------------|
| PAIEMENT_IMMEDIAT | CONFIRMEE | Oui |
| COMPTOIR | EN_ATTENTE | Non |
| EMBARQUEMENT | EN_ATTENTE | Non |

### Règles de Tarification

- Un tarif est identifié par : **Coopérative + Trajet + Type Véhicule + Date**
- Si plusieurs tarifs correspondent, le plus récent est sélectionné
- Si pas de tarif avec type véhicule, fallback sur trajet uniquement

### Règles d'Annulation

- Une réservation ne peut être annulée que si le départ n'est pas encore passé
- L'annulation change le statut à `ANNULEE`
- Les billets associés peuvent être marqués `ANNULE` (à implémenter)

---

## Technologies Utilisées

- **Backend** : Spring Boot 3.x, Java 17
- **Base de données** : PostgreSQL 15
- **ORM** : Hibernate / JPA
- **Frontend** : Thymeleaf, JavaScript ES6, Bootstrap 4
- **Build** : Maven

---

## Auteurs

Projet réalisé dans le cadre de la formation en développement d'applications web.

---

**Date de création** : Janvier 2026  
**Version** : 1.0
