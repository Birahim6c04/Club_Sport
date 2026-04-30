-- =====================================================================
-- Projet Clubs Sportifs - ESIGELEC
-- Base de données MySQL
-- =====================================================================

USE clubs_sportifs;

-- Nettoyage
DROP TABLE IF EXISTS log_recherche;
DROP TABLE IF EXISTS log_connexion;
DROP TABLE IF EXISTS espace_club;
DROP TABLE IF EXISTS utilisateur;
DROP TABLE IF EXISTS licence_stats;
DROP TABLE IF EXISTS club_stats;
DROP TABLE IF EXISTS commune;
DROP TABLE IF EXISTS federation;

-- =====================================================================
-- TABLE federation
-- =====================================================================
CREATE TABLE federation (
    code_federation   VARCHAR(5)   NOT NULL,
    nom_federation    VARCHAR(200) NOT NULL,
    PRIMARY KEY (code_federation)
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE commune
-- =====================================================================
CREATE TABLE commune (
    code_commune   VARCHAR(5)    NOT NULL,
    nom_commune    VARCHAR(100)  NOT NULL,
    code_postal    VARCHAR(5)    DEFAULT NULL,
    departement    VARCHAR(10)   DEFAULT NULL,
    region         VARCHAR(50)   DEFAULT NULL,
    latitude       DECIMAL(10,7) DEFAULT NULL,
    longitude      DECIMAL(10,7) DEFAULT NULL,
    PRIMARY KEY (code_commune),
    INDEX idx_commune_cp (code_postal),
    INDEX idx_commune_region (region),
    INDEX idx_commune_dept (departement)
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE club_stats
-- =====================================================================
CREATE TABLE club_stats (
    code_commune    VARCHAR(5) NOT NULL,
    code_federation VARCHAR(5) NOT NULL,
    annee           SMALLINT   NOT NULL DEFAULT 2019,
    clubs           INT DEFAULT 0,
    epa             INT DEFAULT 0,
    total           INT DEFAULT 0,
    PRIMARY KEY (code_commune, code_federation, annee),
    CONSTRAINT fk_clubstats_commune FOREIGN KEY (code_commune)
        REFERENCES commune(code_commune) ON DELETE CASCADE,
    CONSTRAINT fk_clubstats_fed FOREIGN KEY (code_federation)
        REFERENCES federation(code_federation) ON DELETE CASCADE,
    INDEX idx_clubstats_fed (code_federation)
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE licence_stats
-- =====================================================================
CREATE TABLE licence_stats (
    code_commune    VARCHAR(5) NOT NULL,
    code_federation VARCHAR(5) NOT NULL,
    annee           SMALLINT   NOT NULL DEFAULT 2019,

    f_1_4 INT DEFAULT 0, f_5_9 INT DEFAULT 0, f_10_14 INT DEFAULT 0,
    f_15_19 INT DEFAULT 0, f_20_24 INT DEFAULT 0, f_25_29 INT DEFAULT 0,
    f_30_34 INT DEFAULT 0, f_35_39 INT DEFAULT 0, f_40_44 INT DEFAULT 0,
    f_45_49 INT DEFAULT 0, f_50_54 INT DEFAULT 0, f_55_59 INT DEFAULT 0,
    f_60_64 INT DEFAULT 0, f_65_69 INT DEFAULT 0, f_70_74 INT DEFAULT 0,
    f_75_79 INT DEFAULT 0, f_80_99 INT DEFAULT 0, f_nr INT DEFAULT 0,

    h_1_4 INT DEFAULT 0, h_5_9 INT DEFAULT 0, h_10_14 INT DEFAULT 0,
    h_15_19 INT DEFAULT 0, h_20_24 INT DEFAULT 0, h_25_29 INT DEFAULT 0,
    h_30_34 INT DEFAULT 0, h_35_39 INT DEFAULT 0, h_40_44 INT DEFAULT 0,
    h_45_49 INT DEFAULT 0, h_50_54 INT DEFAULT 0, h_55_59 INT DEFAULT 0,
    h_60_64 INT DEFAULT 0, h_65_69 INT DEFAULT 0, h_70_74 INT DEFAULT 0,
    h_75_79 INT DEFAULT 0, h_80_99 INT DEFAULT 0, h_nr INT DEFAULT 0,

    nr_5_9 INT DEFAULT 0, nr_10_14 INT DEFAULT 0, nr_15_19 INT DEFAULT 0,
    nr_30_34 INT DEFAULT 0, nr_40_44 INT DEFAULT 0, nr_45_49 INT DEFAULT 0,
    nr_70_74 INT DEFAULT 0, nr_nr INT DEFAULT 0,

    total INT DEFAULT 0,

    PRIMARY KEY (code_commune, code_federation, annee),
    CONSTRAINT fk_licstats_commune FOREIGN KEY (code_commune)
        REFERENCES commune(code_commune) ON DELETE CASCADE,
    CONSTRAINT fk_licstats_fed FOREIGN KEY (code_federation)
        REFERENCES federation(code_federation) ON DELETE CASCADE,
    INDEX idx_licstats_fed (code_federation)
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE utilisateur
-- =====================================================================
CREATE TABLE utilisateur (
    id_utilisateur   INT AUTO_INCREMENT PRIMARY KEY,
    login            VARCHAR(80)  NOT NULL UNIQUE,
    mot_de_passe     VARCHAR(255) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    nom              VARCHAR(80)  NOT NULL,
    prenom           VARCHAR(80)  NOT NULL,
    role             ENUM('ADMIN','ELU','PRESIDENT','ENTRAINEUR','LICENCIE') NOT NULL,
    actif            BOOLEAN      NOT NULL DEFAULT TRUE,
    date_creation    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE espace_club
-- =====================================================================
CREATE TABLE espace_club (
    id_espace          INT AUTO_INCREMENT PRIMARY KEY,
    nom_club           VARCHAR(150) NOT NULL,
    code_commune       VARCHAR(5)   DEFAULT NULL,
    code_federation    VARCHAR(5)   DEFAULT NULL,
    id_responsable     INT          DEFAULT NULL,
    adresse            VARCHAR(255) DEFAULT NULL,
    horaires           TEXT         DEFAULT NULL,
    montant_cotisation DECIMAL(8,2) DEFAULT NULL,
    actualites         TEXT         DEFAULT NULL,
    date_creation      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_espace_commune FOREIGN KEY (code_commune)
        REFERENCES commune(code_commune) ON DELETE SET NULL,
    CONSTRAINT fk_espace_fed FOREIGN KEY (code_federation)
        REFERENCES federation(code_federation) ON DELETE SET NULL,
    CONSTRAINT fk_espace_resp FOREIGN KEY (id_responsable)
        REFERENCES utilisateur(id_utilisateur) ON DELETE SET NULL
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE log_connexion
-- =====================================================================
CREATE TABLE log_connexion (
    id_log          BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_tente     VARCHAR(80)  NOT NULL,
    adresse_ip      VARCHAR(45)  NOT NULL,
    succes          BOOLEAN      NOT NULL,
    date_tentative  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_agent      VARCHAR(255) DEFAULT NULL,
    INDEX idx_log_login (login_tente),
    INDEX idx_log_date (date_tentative)
) ENGINE=InnoDB;

-- =====================================================================
-- TABLE log_recherche
-- =====================================================================
CREATE TABLE log_recherche (
    id_log         BIGINT AUTO_INCREMENT PRIMARY KEY,
    login          VARCHAR(80)  DEFAULT NULL,
    adresse_ip     VARCHAR(45)  NOT NULL,
    type_recherche VARCHAR(50)  NOT NULL,
    criteres       TEXT         DEFAULT NULL,
    date_recherche TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_logr_date (date_recherche)
) ENGINE=InnoDB;