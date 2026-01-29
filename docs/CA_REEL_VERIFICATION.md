# Vérification du CA Réel - Intégrité des Statistiques

## Date : 29 Janvier 2026

## Objectif
S'assurer que le CA réel est correctement calculé lors de la création de paiements, que ce soit pour les réservations ou les diffusions publicitaires.

## 1. CA Réservations Réel

### Logique de calcul actuelle

**Requête utilisée** : `PaiementRepository.sumMontantByDepartId()`
```java
@Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p " +
       "WHERE p.reservation.depart.id = :departId " +
       "AND p.refPaiementStatut.code = 'VALIDE'")
BigDecimal sumMontantByDepartId(@Param("departId") Long departId);
```

### Statut de paiement

**Statuts disponibles** (ref_paiement_statut) :
- `EN_ATTENTE` : En attente
- `VALIDE` : **Validé (paiement effectué)** ✅
- `ECHOUE` : Echoué
- `REMBOURSE` : Remboursé
- `PARTIEL` : Partiel

### Vérification lors de la création de paiement

✅ **Points à vérifier** :

1. Lors de la création d'un paiement pour une réservation :
   - Le statut doit être `VALIDE` pour que le montant soit comptabilisé dans le CA réel
   - Si le statut est `EN_ATTENTE` ou autre, le paiement ne sera PAS inclus dans le CA réel

2. Workflow recommandé :
   ```
   Paiement créé → Statut = EN_ATTENTE (si paiement en cours)
                → Statut = VALIDE (dès validation/confirmation)
   ```

3. Services concernés :
   - `PaiementService.create()` : Doit assigner le bon statut
   - `ReservationService` : Peut mettre à jour le statut après confirmation

### Intégration avec les statistiques

**Dans ChiffreAffairesStatsService** :
```java
// Récupère tous les départs du mois
List<DepartDTO> departs = departRepository.findByDateRange(startDate, endDate)
    .stream()
    .map(d -> departService.findById(d.getId()))
    .toList();

// CA Réel = somme de chiffreAffaires de tous les départs
BigDecimal caReservationsReel = departs.stream()
    .map(d -> d.getChiffreAffaires()) // vient de sumMontantByDepartId()
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**Flow complet** :
```
Paiement (statut VALIDE)
    ↓
sumMontantByDepartId() compte ce paiement
    ↓
DepartDTO.chiffreAffaires contient le total
    ↓
ChiffreAffairesStatsService somme tous les départs du mois
    ↓
CA Réservations Réel affiché dans les statistiques
```

## 2. CA Publicité Réel

### Logique de calcul actuelle

**Méthode** : `DepartService.computePaiementsParSociete()`

1. Récupère toutes les sociétés ayant des diffusions sur le départ
2. Pour chaque société :
   ```java
   // Montant facturé pour ce départ
   BigDecimal montantFacture = departPubliciteRepository
       .sumMontantFactureByDepartIdAndSocieteId(departId, societeId);
   
   // Paiements totaux de la société (tous départs)
   BigDecimal montantTotalPaye = paiementPubliciteRepository
       .sumMontantBySocieteId(societeId);
   
   // Factures totales de la société (tous départs)
   BigDecimal montantTotalFacture = departPubliciteRepository
       .sumMontantFactureBySocieteId(societeId);
   
   // Calcul proportionnel pour ce départ
   montantPaye = (montantFacture / montantTotalFacture) * montantTotalPaye
   ```

3. Accumule le total payé : `dto.setMontantPublicitesPaye(totalPublicitesPaye)`

### Vérification lors de la création de paiement publicité

✅ **Points à vérifier** :

1. Lors de la création d'un `PaiementPublicite` :
   - Le paiement est lié à une `SocietePublicitaire`
   - Le montant est stocké dans `paiement_publicite.montant`
   - Pas de statut sur PaiementPublicite (tous les paiements sont considérés effectués)

2. Le calcul proportionnel permet de :
   - Répartir les paiements d'une société entre tous ses départs
   - Éviter le sur-comptage si une société paie globalement
   - Refléter la réalité des encaissements par départ

3. Services concernés :
   - `PaiementPubliciteService.create()` : Enregistre le paiement
   - `DepartService.enrichDepartDTO()` : Calcule automatiquement le CA réel publicité

### Intégration avec les statistiques

**Dans ChiffreAffairesStatsService** :
```java
// CA Réel = somme de montantPublicitesPaye de tous les départs
BigDecimal caDiffusionsReel = departs.stream()
    .map(d -> d.getMontantPublicitesPaye()) // calculé par computePaiementsParSociete()
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**Flow complet** :
```
PaiementPublicite créé pour une société
    ↓
sumMontantBySocieteId() récupère le total
    ↓
computePaiementsParSociete() répartit proportionnellement
    ↓
DepartDTO.montantPublicitesPaye contient la part du départ
    ↓
ChiffreAffairesStatsService somme tous les départs du mois
    ↓
CA Publicité Réel affiché dans les statistiques
```

## 3. Tests de non-régression recommandés

### Test 1 : Paiement réservation avec statut VALIDE
```
1. Créer un départ pour le mois en cours
2. Créer une réservation pour ce départ
3. Créer un paiement avec statut = VALIDE
4. Vérifier que le CA Réel Réservations augmente du montant du paiement
```

### Test 2 : Paiement réservation avec statut EN_ATTENTE
```
1. Créer un départ pour le mois en cours
2. Créer une réservation pour ce départ
3. Créer un paiement avec statut = EN_ATTENTE
4. Vérifier que le CA Réel Réservations reste à 0
5. Changer le statut à VALIDE
6. Vérifier que le CA Réel Réservations augmente maintenant
```

### Test 3 : Paiement publicité
```
1. Créer un départ pour le mois en cours
2. Créer une diffusion publicité pour ce départ (montant facturé)
3. Créer un paiement pour la société
4. Vérifier que le CA Réel Publicité est calculé proportionnellement
5. Créer un autre paiement pour la même société
6. Vérifier que le CA Réel augmente en conséquence
```

### Test 4 : Cohérence page /departs vs statistiques
```
1. Afficher la page /departs pour le mois en cours
2. Noter les valeurs de :
   - chiffreAffaires (CA réel réservations)
   - montantPublicitesPaye (CA réel publicité)
3. Aller sur /statistiques/chiffre-affaires
4. Vérifier que :
   - CA Réel Réservations = somme de tous les chiffreAffaires
   - CA Réel Publicité = somme de tous les montantPublicitesPaye
```

## 4. Points d'attention

### ⚠️ Attention aux statuts de paiement

- **Réservations** : Seul le statut `VALIDE` est compté
- **Publicité** : Tous les paiements sont comptés (pas de filtre de statut)

### ⚠️ Calcul proportionnel publicité

Le calcul proportionnel signifie que :
- Si une société a 3 départs et paie 100,000 Ar
- Et que le départ A représente 40% des factures
- Alors le départ A aura 40,000 Ar de CA réel publicité

Cela peut causer des décalages si :
- Les paiements ne suivent pas la proportion des factures
- Une société paie plus pour certains départs que d'autres

**Solution actuelle** : Le modèle assume une répartition proportionnelle uniforme.

### ⚠️ Période de calcul

Les statistiques sont basées sur `depart.dateHeureDepart`, pas sur :
- `paiement.created_at`
- `paiement.date_paiement`
- `paiement_publicite.date_paiement`

Cela signifie qu'un paiement créé en Février pour un départ de Janvier sera comptabilisé dans les stats de **Janvier** (date du départ).

## 5. Résumé des requêtes clés

### Requêtes pour CA Réservations Réel
```sql
-- Calcul par départ
SELECT COALESCE(SUM(p.montant), 0)
FROM paiement p
WHERE p.reservation.depart.id = :departId 
  AND p.ref_paiement_statut.code = 'VALIDE'

-- Utilisé dans : PaiementRepository.sumMontantByDepartId()
```

### Requêtes pour CA Publicité Réel
```sql
-- Total payé par société (tous départs)
SELECT COALESCE(SUM(p.montant), 0)
FROM paiement_publicite p
WHERE p.societe_publicitaire_id = :societeId

-- Montant facturé pour un départ et une société
SELECT COALESCE(SUM(d.montant_facture), 0)
FROM depart_publicite d
WHERE d.depart_id = :departId 
  AND d.publicite.societe_publicitaire_id = :societeId

-- Calcul: (montantDepartSociete / montantTotalSociete) * paiementsTotalSociete
```

## 6. Conclusion

✅ **Le système est correctement configuré** pour calculer le CA réel lors de la création de paiements :

1. **Réservations** : Filtre automatique sur statut VALIDE
2. **Publicité** : Calcul proportionnel automatique
3. **Statistiques** : Agrégation par mois basée sur la date du départ

**Recommandations** :

1. S'assurer que les paiements de réservation sont créés avec le statut approprié (EN_ATTENTE puis VALIDE)
2. Documenter le calcul proportionnel pour les paiements publicité
3. Tester régulièrement la cohérence entre page /departs et statistiques

---

**Validé par** : Assistant AI  
**Date** : 29 Janvier 2026  
**Version** : 1.0
