-- =============================================
-- Données de test pour Taxi-brousse (Madagascar)
-- =============================================

BEGIN;

-- ---------- Catégories Passager ----------
INSERT INTO ref_passager_categorie (code, libelle, ordre, actif) VALUES
('ADULTE', 'Adulte', 1, true),
('ENFANT', 'Enfant', 2, true),
('SENIOR', 'Senior', 3, true)
ON CONFLICT (code) DO NOTHING;

-- ---------- Catégories Siège ----------
INSERT INTO ref_siege_categorie (code, libelle, ordre, actif) VALUES
('STANDARD', 'Standard', 1, true),
('VIP', 'VIP', 2, true),
('PREMIUM', 'Premium', 3, true)
ON CONFLICT (code) DO NOTHING;



-- ---------- Lieux ----------
INSERT INTO lieu (nom, description, latitude, longitude, ref_lieu_type_id) VALUES
('Gare Routière Analakely', 'Principale gare routière de Tana', -18.913611, 47.521111, (SELECT id FROM ref_lieu_type WHERE code='GARE_ROUTIERE')),
('Gare Routière Soarano', 'Gare secondaire au centre ville', -18.900000, 47.523333, (SELECT id FROM ref_lieu_type WHERE code='GARE_ROUTIERE')),
('Antsirabe', 'Ville thermale à 170km au sud de Tana', -19.866667, 47.033333, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Ambositra', 'Capitale de l''artisanat malgache', -20.533333, 47.250000, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Fianarantsoa', 'Capitale de la région Haute Matsiatra', -21.450000, 47.083333, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Toamasina', 'Principal port de Madagascar', -18.149167, 49.402500, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Mahajanga', 'Grande ville côtière au nord-ouest', -15.716667, 46.316667, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Antsohihy', 'Ville sur la RN6', -14.880000, 47.990000, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Ambanja', 'Ville du nord-ouest', -13.683333, 48.450000, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Ambatolampy', 'Ville de passage vers le sud', -19.383333, 47.433333, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Behenjy', 'Point d''arrêt sur RN4', -19.166667, 47.850000, (SELECT id FROM ref_lieu_type WHERE code='POINT_ARRET')),
('Moramanga', 'Carrefour important vers l''est', -18.950000, 48.216667, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Brickaville', 'Ville étape vers Toamasina', -18.816667, 49.066667, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Port Bergé', 'Ville sur la route du nord', -15.583333, 47.616667, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Maevatanana', 'Chef-lieu de région', -16.950000, 46.833333, (SELECT id FROM ref_lieu_type WHERE code='VILLE')),
('Gare Routière Fasankarana', 'Gare routière Fasankarana à Antananarivo', -18.920000, 47.528000, (SELECT id FROM ref_lieu_type WHERE code='GARE_ROUTIERE')),
('Gare Routière Ambolomadinika', 'Gare routière Ambolomadinika à Toamasina', -18.143000, 49.398000, (SELECT id FROM ref_lieu_type WHERE code='GARE_ROUTIERE'));

-- ---------- Coopératives ----------
INSERT INTO cooperative (code, nom, telephone, email, adresse) VALUES
('COOP-TANA-001', 'Coopérative Analakely Express', '034 12 345 67', 'contact@analakely-express.mg', 'Analakely, Antananarivo'),
('COOP-TANA-002', 'Transport Soarano Vitesse', '033 98 765 43', 'info@soarano-vitesse.mg', 'Soarano, Antananarivo'),
('COOP-SUD-001', 'Coopérative Route du Sud', '032 11 222 33', 'coop.sud@gmail.com', 'Antsirabe'),
('COOP-EST-001', 'Transport Côte Est', '034 55 666 77', 'cote.est.transport@yahoo.fr', 'Toamasina'),
('COOP-NORD-001', 'Majunga Express', '033 44 333 22', 'majunga.express@outlook.com', 'Mahajanga');

-- ---------- Trajets ----------
INSERT INTO trajet (code, libelle, lieu_depart_id, lieu_arrivee_id, distance_km, duree_estimee_min, actif) VALUES
('TJ-TANA-ANTSIRABE', 'Antananarivo - Antsirabe', 
    (SELECT id FROM lieu WHERE nom='Gare Routière Analakely'), 
    (SELECT id FROM lieu WHERE nom='Antsirabe'), 
    169.00, 240, true),
('TJ-TANA-FIANAR', 'Antananarivo - Fianarantsoa',
    (SELECT id FROM lieu WHERE nom='Gare Routière Analakely'),
    (SELECT id FROM lieu WHERE nom='Fianarantsoa'),
    408.00, 540, true),
('TJ-TANA-TOAMASINA', 'Antananarivo - Toamasina',
    (SELECT id FROM lieu WHERE nom='Gare Routière Soarano'),
    (SELECT id FROM lieu WHERE nom='Toamasina'),
    369.00, 480, true),
('TJ-TANA-MAHAJANGA', 'Antananarivo - Mahajanga',
    (SELECT id FROM lieu WHERE nom='Gare Routière Analakely'),
    (SELECT id FROM lieu WHERE nom='Mahajanga'),
    575.00, 660, true),
('TJ-ANTSIRABE-FIANAR', 'Antsirabe - Fianarantsoa',
    (SELECT id FROM lieu WHERE nom='Antsirabe'),
    (SELECT id FROM lieu WHERE nom='Fianarantsoa'),
    239.00, 300, true);

-- ---------- Escales des trajets ----------
INSERT INTO trajet_escale (trajet_id, lieu_id, ordre, duree_arret_min) VALUES
-- Escales Tana-Antsirabe
((SELECT id FROM trajet WHERE code='TJ-TANA-ANTSIRABE'), (SELECT id FROM lieu WHERE nom='Ambatolampy'), 1, 15),

-- Escales Tana-Fianarantsoa
((SELECT id FROM trajet WHERE code='TJ-TANA-FIANAR'), (SELECT id FROM lieu WHERE nom='Ambatolampy'), 1, 15),
((SELECT id FROM trajet WHERE code='TJ-TANA-FIANAR'), (SELECT id FROM lieu WHERE nom='Antsirabe'), 2, 30),
((SELECT id FROM trajet WHERE code='TJ-TANA-FIANAR'), (SELECT id FROM lieu WHERE nom='Ambositra'), 3, 20),

-- Escales Tana-Toamasina
((SELECT id FROM trajet WHERE code='TJ-TANA-TOAMASINA'), (SELECT id FROM lieu WHERE nom='Moramanga'), 1, 20),
((SELECT id FROM trajet WHERE code='TJ-TANA-TOAMASINA'), (SELECT id FROM lieu WHERE nom='Brickaville'), 2, 15),

-- Escales Tana-Mahajanga
((SELECT id FROM trajet WHERE code='TJ-TANA-MAHAJANGA'), (SELECT id FROM lieu WHERE nom='Maevatanana'), 1, 30),
((SELECT id FROM trajet WHERE code='TJ-TANA-MAHAJANGA'), (SELECT id FROM lieu WHERE nom='Port Bergé'), 2, 25),

-- Escales Antsirabe-Fianarantsoa
((SELECT id FROM trajet WHERE code='TJ-ANTSIRABE-FIANAR'), (SELECT id FROM lieu WHERE nom='Ambositra'), 1, 20);

-- ---------- Association Coopérative-Trajet ----------
INSERT INTO cooperative_trajet (cooperative_id, trajet_id, actif) VALUES
((SELECT id FROM cooperative WHERE code='COOP-TANA-001'), (SELECT id FROM trajet WHERE code='TJ-TANA-ANTSIRABE'), true),
((SELECT id FROM cooperative WHERE code='COOP-TANA-001'), (SELECT id FROM trajet WHERE code='TJ-TANA-FIANAR'), true),
((SELECT id FROM cooperative WHERE code='COOP-TANA-002'), (SELECT id FROM trajet WHERE code='TJ-TANA-TOAMASINA'), true),
((SELECT id FROM cooperative WHERE code='COOP-TANA-002'), (SELECT id FROM trajet WHERE code='TJ-TANA-MAHAJANGA'), true),
((SELECT id FROM cooperative WHERE code='COOP-SUD-001'), (SELECT id FROM trajet WHERE code='TJ-TANA-ANTSIRABE'), true),
((SELECT id FROM cooperative WHERE code='COOP-SUD-001'), (SELECT id FROM trajet WHERE code='TJ-ANTSIRABE-FIANAR'), true),
((SELECT id FROM cooperative WHERE code='COOP-EST-001'), (SELECT id FROM trajet WHERE code='TJ-TANA-TOAMASINA'), true),
((SELECT id FROM cooperative WHERE code='COOP-NORD-001'), (SELECT id FROM trajet WHERE code='TJ-TANA-MAHAJANGA'), true);


-- ---------- Chauffeurs ----------
INSERT INTO chauffeur (nom, prenom, telephone, numero_permis, date_naissance, date_embauche) VALUES
('RAKOTO', 'Jean', '034 11 111 11', 'PERM-001-2015', '1985-03-15', '2018-01-10'),
('RASOANAIVO', 'Paul', '033 22 222 22', 'PERM-002-2016', '1990-07-22', '2019-02-15'),
('ANDRIAMIHAJA', 'Michel', '032 33 333 33', 'PERM-003-2014', '1982-11-08', '2017-05-20'),
('RAZAFINDRAKOTO', 'Pierre', '034 44 444 44', 'PERM-004-2017', '1988-05-30', '2020-03-12'),
('RANDRIANARISOA', 'Luc', '033 55 555 55', 'PERM-005-2018', '1992-09-14', '2021-01-08'),
('RAHARISON', 'Joseph', '032 66 666 66', 'PERM-006-2015', '1986-12-25', '2018-11-22'),
('RABEMANANJARA', 'Marc', '034 77 777 77', 'PERM-007-2019', '1991-04-18', '2021-06-05'),
('RANDRIAMANANA', 'André', '033 88 888 88', 'PERM-008-2016', '1987-08-09', '2019-09-17'),
('RANAIVO', 'François', '032 99 999 99', 'PERM-009-2017', '1989-01-27', '2020-07-30'),
('RABEARIVELO', 'Daniel', '034 10 101 01', 'PERM-010-2018', '1993-06-11', '2021-04-14'),
('ANDRIANASOLO', 'Olivier', '033 20 202 02', 'PERM-011-2019', '1984-10-03', '2018-08-25'),
('RAKOTONIRINA', 'Henri', '032 30 303 03', 'PERM-012-2015', '1990-02-19', '2019-12-10');




-- ---------- Clients ----------
INSERT INTO client (nom, prenom, telephone, email, numero_cin) VALUES
('RAMAROSON', 'Sophie', '034 10 111 10', 'sophie.ramaroson@gmail.com', '101234567890'),
('RAKOTOMALALA', 'Marie', '033 20 222 20', 'marie.rakoto@yahoo.fr', '102345678901'),
('ANDRIANIRINA', 'Julie', '032 30 333 30', 'julie.andria@outlook.com', '103456789012'),
('RAZAFY', 'Voahangy', '034 40 444 40', 'voahangy.razafy@gmail.com', '104567890123'),
('RAHARISON', 'Lanto', '033 50 555 50', 'lanto.raharison@yahoo.fr', '105678901234'),
('RANDRIA', 'Miora', '032 60 666 60', NULL, '106789012345'),
('RABE', 'Fara', '034 70 777 70', 'fara.rabe@gmail.com', '107890123456'),
('RAKOTO', 'Nirina', '033 80 888 80', 'nirina.rakoto@outlook.com', '108901234567'),
('ANDRIANINA', 'Herisoa', '032 90 999 90', NULL, '109012345678'),
('RASOLOFO', 'Tiana', '034 11 011 11', 'tiana.rasolofo@gmail.com', '110123456789');


COMMIT;

-- ---------- Requêtes de vérification ----------
SELECT 'Lieux créés:', COUNT(*) FROM lieu;
SELECT 'Coopératives créées:', COUNT(*) FROM cooperative;
SELECT 'Trajets créés:', COUNT(*) FROM trajet;
SELECT 'Escales créées:', COUNT(*) FROM trajet_escale;
SELECT 'Chauffeurs créés:', COUNT(*) FROM chauffeur;
SELECT 'Clients créés:', COUNT(*) FROM client;





-- Script de mise à jour pour implémenter le système de paiement hybride
-- À exécuter dans PostgreSQL

-- 1. Ajout des colonnes pour la gestion des paiements dans la table reservation
ALTER TABLE reservation 
ADD COLUMN IF NOT EXISTS montant_total DECIMAL(12,2),
ADD COLUMN IF NOT EXISTS montant_paye DECIMAL(12,2) DEFAULT 0,
ADD COLUMN IF NOT EXISTS reste_a_payer DECIMAL(12,2);

-- 2. Ajout du statut EN_ATTENTE pour les réservations en attente de paiement
INSERT INTO ref_reservation_statut (code, libelle, actif)
VALUES ('EN_ATTENTE', 'En attente de paiement', true)
ON CONFLICT (code) DO NOTHING;

-- 3. Ajout de la colonne reservation_id dans paiement
ALTER TABLE paiement 
ADD COLUMN IF NOT EXISTS reservation_id BIGINT REFERENCES reservation(id);

-- 4. Ajout de la colonne notes dans paiement
ALTER TABLE paiement 
ADD COLUMN IF NOT EXISTS notes TEXT;

-- 5. Ajout des colonnes dans billet pour les informations des passagers
ALTER TABLE billet
ADD COLUMN IF NOT EXISTS code VARCHAR(120),
ADD COLUMN IF NOT EXISTS passager_nom VARCHAR(120),
ADD COLUMN IF NOT EXISTS passager_prenom VARCHAR(120),
ADD COLUMN IF NOT EXISTS numero_siege INTEGER;

-- 6. Ajout de la colonne symbole dans ref_devise
ALTER TABLE ref_devise 
ADD COLUMN IF NOT EXISTS symbole VARCHAR(10);

-- 7. Mise à jour des données existantes
UPDATE reservation 
SET montant_paye = 0,
    reste_a_payer = montant_total
WHERE montant_paye IS NULL AND montant_total IS NOT NULL;

-- 8. Mise à jour des symboles de devises courantes
UPDATE ref_devise SET symbole = 'Ar' WHERE code = 'MGA';
UPDATE ref_devise SET symbole = '€' WHERE code = 'EUR';
UPDATE ref_devise SET symbole = '$' WHERE code = 'USD';

-- Vérification des modifications
SELECT 'Modifications appliquées avec succès' AS statut;

