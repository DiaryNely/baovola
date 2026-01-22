# Livrables – Tarif Publicitaire & Diffusions

## 1) MCD (ASCII) – Tables impliquées

┌──────────────────────────────────────────────┐
│ MCD – Gestion Publicité                      │
└──────────────────────────────────────────────┘

[ SOCIETE_PUBLICITAIRE ] 1 ───────< N [ PUBLICITE ]
[ TARIF_PUBLICITE ]      1 ───────< N [ DEPART_PUBLICITE ] >────── 1 [ DEPART ]
[ PUBLICITE ]            1 ───────< N [ DEPART_PUBLICITE ]
[ REF_DEVISE ]           1 ───────< N [ DEPART_PUBLICITE ]

┌──────────────────────────────────────────────┐
│ SOCIETE_PUBLICITAIRE                         │
├──────────────────────────────────────────────┤
│ PK id                                        │
│ nom                                          │
│ contact, email, telephone, adresse…          │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│ PUBLICITE                                    │
├──────────────────────────────────────────────┤
│ PK id                                        │
│ titre                                        │
│ contenu_media / url_video                    │
│ date_debut_validite                          │
│ date_fin_validite (nullable)                 │
│ actif                                        │
│ FK societe_publicitaire_id → SOCIETE_PUBLICITAIRE.id │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│ TARIF_PUBLICITE                               │
├──────────────────────────────────────────────┤
│ PK id                                        │
│ montant (tarif unitaire)                     │
│ date_debut                                   │
│ date_fin (nullable)                          │
│ actif                                        │
│ FK ref_devise_id → REF_DEVISE.id             │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│ DEPART                                       │
├──────────────────────────────────────────────┤
│ PK id                                        │
│ code                                         │
│ date_heure_depart                            │
│ date_heure_arrivee_estimee                   │
│ FK trajet_id → TRAJET.id                     │
│ FK vehicule_id → VEHICULE.id                 │
│ FK statut_id → REF_DEPART_STATUT.id          │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│ DEPART_PUBLICITE                             │
├──────────────────────────────────────────────┤
│ PK id                                        │
│ date_diffusion                               │
│ nombre_repetitions                           │
│ montant_facture                              │
│ statut_diffusion                             │
│ created_at                                   │
│ FK depart_id → DEPART.id                     │
│ FK publicite_id → PUBLICITE.id               │
│ FK tarif_publicite_id → TARIF_PUBLICITE.id   │
│ FK ref_devise_id → REF_DEVISE.id             │
└──────────────────────────────────────────────┘

Cardinalités :
- SOCIETE_PUBLICITAIRE 1 ─── N PUBLICITE
- PUBLICITE 1 ─── N DEPART_PUBLICITE
- TARIF_PUBLICITE 1 ─── N DEPART_PUBLICITE
- DEPART 1 ─── N DEPART_PUBLICITE
- REF_DEVISE 1 ─── N DEPART_PUBLICITE


## 2) Maquettes ASCII – Pages impactées

┌──────────────────────────────────────────────┐
│ Page: Tarifs Publicité                        │
├──────────────────────────────────────────────┤
│ [Bouton + Nouveau Tarif]                      │
│ -------------------------------------------- │
│ | Date début | Date fin | Montant | Actif |  │
│ | ...                                       | │
│ -------------------------------------------- │
│ [Modal: Création/édition tarif]               │
│  - Date début                                 │
│  - Date fin                                   │
│  - Montant (tarif unitaire)                   │
│  - Devise                                     │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│ Page: Diffusions Publicité                   │
├──────────────────────────────────────────────┤
│ [Sélecteur Départ]                            │
│ [Infos Départ] [Statistiques]                 │
│ -------------------------------------------- │
│ | Publicité | Société | Date | Répétitions |  │
│ | Montant | Statut | Actions                | │
│ -------------------------------------------- │
│ [Modal: Ajouter publicité]                    │
│  - Publicité                                  │
│  - Date diffusion (optionnel)                 │
│  - Nombre de répétitions                      │
│  * Montant = tarif unitaire × répétitions     │
└──────────────────────────────────────────────┘

┌──────────────────────────────────────────────┐
│ Page: Statistiques CA Publicité               │
├──────────────────────────────────────────────┤
│ [Filtre: Mois] [Filtre: Année] [Appliquer]    │
│ -------------------------------------------- │
│ | Total répétitions | CA total | Publicités | │
│ -------------------------------------------- │
│ | Publicité | Société | Répétitions |         │
│ | Tarif unitaire (badge orange) | Montant |  │
│ -------------------------------------------- │
└──────────────────────────────────────────────┘


## 3) Métier – Classes, méthodes, logique

### Entités
- `DepartPublicite` : association diffusion publicitaire pour un départ
  - Champs clés : `dateDiffusion`, `nombreRepetitions`, `montantFacture`, `statutDiffusion`

### Services
1) `TarifPubliciteService`
- `findAll() : List<TarifPubliciteDTO>`
- `findById(Long id) : TarifPubliciteDTO`
- `findTarifActuel() : TarifPubliciteDTO`
- `findTarifEnVigueur(LocalDate date) : TarifPubliciteDTO`
- `create(TarifPubliciteDTO dto) : TarifPubliciteDTO`
- `update(Long id, TarifPubliciteDTO dto) : TarifPubliciteDTO`
  - Logique: met à jour le tarif unitaire puis **recalcule les montants** des diffusions liées.
- `delete(Long id) : void`
- `desactiver(Long id) : TarifPubliciteDTO`
- `cloturerTarifActuel(LocalDate dateFin) : TarifPubliciteDTO`

2) `DepartPubliciteService`
- `findByDepartId(Long departId) : List<DepartPubliciteDTO>`
- `findByPubliciteId(Long publiciteId) : List<DepartPubliciteDTO>`
- `findById(Long id) : DepartPubliciteDTO`
- `create(DepartPubliciteDTO dto) : DepartPubliciteDTO`
  - Logique: validation publicité active + valide à la date du départ
  - Calcul: `montantFacture = tarifUnitaire × nombreRepetitions`
- `delete(Long id) : void` (refus si statut = DIFFUSE)
- `updateStatut(Long id, String statut) : DepartPubliciteDTO`
- `getStatsByDepart(Long departId) : DepartPubliciteStatsDTO`
  - Logique: somme des répétitions + somme des montants

3) `PubliciteStatsService`
- `getCaParMois(int mois, int annee) : PubliciteCaStatsDTO`
- `getCaParMoisCourant() : PubliciteCaStatsDTO`
  - Logique: agrégation CA par publicité sur période (dateDiffusion)

### Repositories (impactés)
- `DepartPubliciteRepository`
  - `findByDepartId(Long departId)`
  - `findByPubliciteId(Long publiciteId)`
  - `findByPeriode(LocalDateTime dateDebut, LocalDateTime dateFin)`
  - `findCaParPublicite(LocalDateTime dateDebut, LocalDateTime dateFin)`
  - `recalculerMontantsParTarif(Long tarifId, BigDecimal montantUnitaire)`

### Endpoints REST impactés
- `GET /api/depart-publicites/depart/{departId}`
- `GET /api/depart-publicites/publicite/{publiciteId}`
- `GET /api/depart-publicites/{id}`
- `POST /api/depart-publicites`
- `DELETE /api/depart-publicites/{id}`
- `PUT /api/depart-publicites/{id}/statut/{statut}`
- `GET /api/depart-publicites/depart/{departId}/stats`
- `GET /api/publicites/actives`
- `GET /api/publicites/valides?date=yyyy-MM-dd`
- `GET /api/publicites/{id}`
- `GET /api/tarifs-publicite` (CRUD tarifs)
- `GET /api/statistiques-publicite/ca?mois=MM&annee=YYYY`

### Pages impactées (routes MVC)
- `/tarifs-publicite`
- `/diffusions-publicite`
- `/statistiques-publicite`

---
Fin du document.
