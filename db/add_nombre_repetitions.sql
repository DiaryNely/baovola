-- Ajout de la colonne nombre_repetitions dans la table depart_publicite
-- Cette colonne stocke le nombre de fois que la publicité sera diffusée durant le voyage

ALTER TABLE depart_publicite
ADD COLUMN IF NOT EXISTS nombre_repetitions INTEGER NOT NULL DEFAULT 1;

COMMENT ON COLUMN depart_publicite.nombre_repetitions IS 'Nombre de fois que la publicité sera diffusée durant le voyage';

-- Mettre à jour les enregistrements existants (si la table existe déjà)
UPDATE depart_publicite
SET nombre_repetitions = 1
WHERE nombre_repetitions IS NULL;
