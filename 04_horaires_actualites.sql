USE clubs_sportifs;

-- ============================================================
-- TABLE horaires
-- Liée à espace_club via id_espace
-- Créée après espace_club qui existe déjà
-- ============================================================
CREATE TABLE IF NOT EXISTS horaires (
    id_horaire        INT AUTO_INCREMENT PRIMARY KEY,
    id_espace         INT          NOT NULL,
    jour              ENUM('Lundi','Mardi','Mercredi','Jeudi',
                           'Vendredi','Samedi','Dimanche') NOT NULL,
    heure_debut       TIME         NOT NULL,
    heure_fin         TIME         NOT NULL,
    activite          VARCHAR(100) DEFAULT NULL,
    niveau            VARCHAR(50)  DEFAULT NULL,
    date_creation     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_horaires_espace FOREIGN KEY (id_espace)
        REFERENCES espace_club(id_espace) ON DELETE CASCADE,
    INDEX idx_horaires_espace (id_espace),
    INDEX idx_horaires_jour   (jour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE actualites
-- Liée à espace_club via id_espace
-- ============================================================
CREATE TABLE IF NOT EXISTS actualites (
    id_actualite      INT AUTO_INCREMENT PRIMARY KEY,
    id_espace         INT          NOT NULL,
    titre             VARCHAR(200) NOT NULL,
    contenu           TEXT         NOT NULL,
    categorie         ENUM('Tournoi','Entraînement','Recrutement',
                           'Résultat','Information')
                      NOT NULL DEFAULT 'Information',
    date_publication  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_actualites_espace FOREIGN KEY (id_espace)
        REFERENCES espace_club(id_espace) ON DELETE CASCADE,
    INDEX idx_actualites_espace (id_espace),
    INDEX idx_actualites_date   (date_publication)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SELECT 'Tables horaires et actualites créées.' AS statut;