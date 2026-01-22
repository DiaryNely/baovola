-- =============================================
-- Données de test pour les Publicités
-- =============================================
-- Exemple : CA Décembre 2025
-- Vaniala : 20 diffusions × 100 000 Ar = 2 000 000 Ar
-- Lewis   : 10 diffusions × 100 000 Ar = 1 000 000 Ar
-- TOTAL   : 30 diffusions = 3 000 000 Ar
-- =============================================

BEGIN;

-- Vérifier que les prérequis existent
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM ref_devise WHERE code = 'MGA') THEN
        RAISE EXCEPTION 'La devise MGA n''existe pas. Exécutez d''abord schema_reference.sql';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM depart LIMIT 1) THEN
        RAISE EXCEPTION 'Aucun départ n''existe. Exécutez d''abord data_test.sql';
    END IF;
END $$;

-- ---------- Sociétés publicitaires ----------

-- ---------- Vidéos publicitaires ----------
-- 3 publicités pour Vaniala
INSERT INTO publicite (code, societe_publicitaire_id, titre, description, url_video, duree_secondes, date_debut_validite, date_fin_validite, actif) VALUES
('PUB-VANIALA-001', 
    (SELECT id FROM societe_publicitaire WHERE code = 'SOC-PUB-001'), 
    'Vaniala - Produits Laitiers Bio',
    'Publicité pour les nouveaux yaourts bio de Vaniala',
    'https://cdn.vaniala.mg/videos/pub-yaourt-bio-2025.mp4',
    30,
    '2025-11-01',
    '2026-01-31',
    TRUE),

('PUB-VANIALA-002', 
    (SELECT id FROM societe_publicitaire WHERE code = 'SOC-PUB-001'), 
    'Vaniala - Fromage Artisanal',
    'Découvrez notre gamme de fromages artisanaux',
    'https://cdn.vaniala.mg/videos/pub-fromage-2025.mp4',
    25,
    '2025-11-15',
    '2026-02-28',
    TRUE),

('PUB-VANIALA-003', 
    (SELECT id FROM societe_publicitaire WHERE code = 'SOC-PUB-001'), 
    'Vaniala - Beurre Premium',
    'Le meilleur beurre de Madagascar',
    'https://cdn.vaniala.mg/videos/pub-beurre-2025.mp4',
    20,
    '2025-12-01',
    '2026-03-31',
    TRUE);

-- 2 publicités pour Lewis
INSERT INTO publicite (code, societe_publicitaire_id, titre, description, url_video, duree_secondes, date_debut_validite, date_fin_validite, actif) VALUES
('PUB-LEWIS-001', 
    (SELECT id FROM societe_publicitaire WHERE code = 'SOC-PUB-002'), 
    'Lewis - Smartphones 2025',
    'La nouvelle gamme de smartphones Lewis',
    'https://cdn.lewis.mg/videos/pub-smartphones-2025.mp4',
    35,
    '2025-11-01',
    '2026-01-31',
    TRUE),

('PUB-LEWIS-002', 
    (SELECT id FROM societe_publicitaire WHERE code = 'SOC-PUB-002'), 
    'Lewis - Accessoires High-Tech',
    'Accessoires de qualité pour tous vos appareils',
    'https://cdn.lewis.mg/videos/pub-accessoires-2025.mp4',
    28,
    '2025-12-01',
    '2026-02-28',
    TRUE);

-- ---------- Diffusions pour Décembre 2025 ----------
-- Vaniala : 20 diffusions réparties sur différents départs
-- Lewis : 10 diffusions réparties sur différents départs

-- -- Récupérer quelques départs pour les tests
-- DO $$
-- DECLARE
--     v_depart_ids BIGINT[];
--     v_tarif_id BIGINT;
--     v_devise_id BIGINT;
--     v_pub_vaniala_1 BIGINT;
--     v_pub_vaniala_2 BIGINT;
--     v_pub_vaniala_3 BIGINT;
--     v_pub_lewis_1 BIGINT;
--     v_pub_lewis_2 BIGINT;
--     i INTEGER;
-- BEGIN
--     -- Récupérer le tarif actif
--     SELECT id INTO v_tarif_id FROM tarif_publicite WHERE actif = TRUE LIMIT 1;
--     SELECT id INTO v_devise_id FROM ref_devise WHERE code = 'MGA';
    
--     -- Récupérer les IDs des publicités
--     SELECT id INTO v_pub_vaniala_1 FROM publicite WHERE code = 'PUB-VANIALA-001';
--     SELECT id INTO v_pub_vaniala_2 FROM publicite WHERE code = 'PUB-VANIALA-002';
--     SELECT id INTO v_pub_vaniala_3 FROM publicite WHERE code = 'PUB-VANIALA-003';
--     SELECT id INTO v_pub_lewis_1 FROM publicite WHERE code = 'PUB-LEWIS-001';
--     SELECT id INTO v_pub_lewis_2 FROM publicite WHERE code = 'PUB-LEWIS-002';
    
--     -- Récupérer des départs existants
--     SELECT ARRAY_AGG(id) INTO v_depart_ids FROM (SELECT id FROM depart LIMIT 30) AS sub;
    
--     -- Vaniala - 20 diffusions en décembre 2025
--     -- 8 diffusions de PUB-VANIALA-001
--     FOR i IN 1..8 LOOP
--         INSERT INTO depart_publicite (
--             depart_id, publicite_id, tarif_publicite_id, 
--             date_diffusion, montant_facture, ref_devise_id, statut_diffusion
--         ) VALUES (
--             v_depart_ids[i], v_pub_vaniala_1, v_tarif_id,
--             TIMESTAMP '2025-12-01' + (i || ' days')::INTERVAL,
--             100000.00, v_devise_id, 'DIFFUSE'
--         );
--     END LOOP;
    
--     -- 7 diffusions de PUB-VANIALA-002
--     FOR i IN 9..15 LOOP
--         INSERT INTO depart_publicite (
--             depart_id, publicite_id, tarif_publicite_id, 
--             date_diffusion, montant_facture, ref_devise_id, statut_diffusion
--         ) VALUES (
--             v_depart_ids[i], v_pub_vaniala_2, v_tarif_id,
--             TIMESTAMP '2025-12-05' + ((i-8) || ' days')::INTERVAL,
--             100000.00, v_devise_id, 'DIFFUSE'
--         );
--     END LOOP;
    
--     -- 5 diffusions de PUB-VANIALA-003
--     FOR i IN 16..20 LOOP
--         INSERT INTO depart_publicite (
--             depart_id, publicite_id, tarif_publicite_id, 
--             date_diffusion, montant_facture, ref_devise_id, statut_diffusion
--         ) VALUES (
--             v_depart_ids[i], v_pub_vaniala_3, v_tarif_id,
--             TIMESTAMP '2025-12-15' + ((i-15) || ' days')::INTERVAL,
--             100000.00, v_devise_id, 'DIFFUSE'
--         );
--     END LOOP;
    
--     -- Lewis - 10 diffusions en décembre 2025
--     -- 6 diffusions de PUB-LEWIS-001
--     FOR i IN 21..26 LOOP
--         INSERT INTO depart_publicite (
--             depart_id, publicite_id, tarif_publicite_id, 
--             date_diffusion, montant_facture, ref_devise_id, statut_diffusion
--         ) VALUES (
--             v_depart_ids[i], v_pub_lewis_1, v_tarif_id,
--             TIMESTAMP '2025-12-03' + ((i-20) || ' days')::INTERVAL,
--             100000.00, v_devise_id, 'DIFFUSE'
--         );
--     END LOOP;
    
--     -- 4 diffusions de PUB-LEWIS-002
--     FOR i IN 27..30 LOOP
--         INSERT INTO depart_publicite (
--             depart_id, publicite_id, tarif_publicite_id, 
--             date_diffusion, montant_facture, ref_devise_id, statut_diffusion
--         ) VALUES (
--             v_depart_ids[i], v_pub_lewis_2, v_tarif_id,
--             TIMESTAMP '2025-12-10' + ((i-26) || ' days')::INTERVAL,
--             100000.00, v_devise_id, 'DIFFUSE'
--         );
--     END LOOP;
    
--     RAISE NOTICE '✅ 30 diffusions créées pour décembre 2025';
-- END $$;

COMMIT;

-- ---------- Requêtes de vérification ----------

-- 1. Nombre total de publicités
SELECT 'Total publicités:', COUNT(*) FROM publicite;

-- 2. Nombre de diffusions par société
SELECT 
    sp.nom AS societe,
    COUNT(dp.id) AS nombre_diffusions
FROM societe_publicitaire sp
LEFT JOIN publicite p ON sp.id = p.societe_publicitaire_id
LEFT JOIN depart_publicite dp ON p.id = dp.publicite_id
WHERE EXTRACT(YEAR FROM dp.date_diffusion) = 2025
  AND EXTRACT(MONTH FROM dp.date_diffusion) = 12
  AND dp.statut_diffusion = 'DIFFUSE'
GROUP BY sp.nom;

-- 3. CA mensuel de décembre 2025 (vue prédéfinie)
SELECT 
    societe_nom,
    nombre_diffusions,
    ca_mensuel,
    devise_code
FROM v_ca_mensuel_diffusions
WHERE annee = 2025 AND mois = 12
ORDER BY societe_nom;

-- 4. CA total décembre 2025
SELECT 
    'TOTAL CA Décembre 2025' AS description,
    SUM(ca_mensuel) AS montant_total,
    'MGA' AS devise
FROM v_ca_mensuel_diffusions
WHERE annee = 2025 AND mois = 12;

-- 5. Détail des diffusions de décembre 2025
SELECT 
    sp.nom AS societe,
    p.titre AS publicite,
    dp.date_diffusion,
    dp.montant_facture,
    dp.statut_diffusion
FROM depart_publicite dp
JOIN publicite p ON dp.publicite_id = p.id
JOIN societe_publicitaire sp ON p.societe_publicitaire_id = sp.id
WHERE EXTRACT(YEAR FROM dp.date_diffusion) = 2025
  AND EXTRACT(MONTH FROM dp.date_diffusion) = 12
ORDER BY sp.nom, dp.date_diffusion;

-- =============================================
-- RÉSULTAT ATTENDU :
-- =============================================
-- Vaniala : 20 diffusions × 100 000 Ar = 2 000 000 Ar
-- Lewis   : 10 diffusions × 100 000 Ar = 1 000 000 Ar
-- ------------------------------------------------
-- TOTAL CA Décembre 2025       = 3 000 000 Ar
-- =============================================
