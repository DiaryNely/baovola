@echo off
chcp 65001 > nul

REM === Paramètres ===
set DB_NAME=taxi_brousse
set DB_USER=postgres

REM === Suppression de la base si elle existe ===
psql -U %DB_USER% -d postgres -c "DROP DATABASE IF EXISTS %DB_NAME%;"

REM === Création de la base ===
psql -U %DB_USER% -d postgres -c "CREATE DATABASE %DB_NAME% WITH ENCODING='UTF8';"

REM === Chargement du schéma ===
psql -U %DB_USER% -d %DB_NAME% -f schema_reference.sql

REM === Chargement des données ===
psql -U %DB_USER% -d %DB_NAME% -f data_test.sql

pause
