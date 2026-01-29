@echo off
REM =============================================
REM Script d'installation du module Publicité
REM =============================================

echo.
echo ========================================
echo Installation du Module PUBLICITE
echo ========================================
echo.

REM Configuration de la connexion PostgreSQL
set PGHOST=localhost
set PGPORT=5432
set PGDATABASE=taxi_brousse
set PGUSER=postgres

echo [1/3] Creation des tables publicite...
psql -f publicite_v2.sql
if %ERRORLEVEL% neq 0 (
    echo ERREUR: Echec de la creation des tables
    pause
    exit /b 1
)
echo ✓ Tables creees avec succes
echo.



echo [3/3] Verification...
psql -c "SELECT * FROM v_ca_mensuel_diffusions WHERE annee = 2025 AND mois = 12;"
echo.

echo ========================================
echo Installation terminee avec succes!
echo ========================================
echo.
echo Resultats attendus pour decembre 2025:
echo - Vaniala: 20 diffusions = 2 000 000 Ar
echo - Lewis:   10 diffusions = 1 000 000 Ar
echo - TOTAL:   30 diffusions = 3 000 000 Ar
echo.

pause
