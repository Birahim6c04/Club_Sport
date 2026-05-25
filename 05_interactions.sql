USE clubs_sportifs;

-- ============================================================
-- TABLE commentaires
-- Un utilisateur connecté commente une actualité
-- ============================================================
CREATE TABLE IF NOT EXISTS commentaires (
    id_commentaire  INT AUTO_INCREMENT PRIMARY KEY,
    id_actualite    INT          NOT NULL,
    id_utilisateur  INT          NOT NULL,
    contenu         TEXT         NOT NULL,
    date_creation   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_actu FOREIGN KEY (id_actualite)
        REFERENCES actualites(id_actualite) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    INDEX idx_comment_actu (id_actualite),
    INDEX idx_comment_user (id_utilisateur),
    INDEX idx_comment_date (date_creation)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE likes
-- Un utilisateur connecté like une actualité (1 seul like par user/actu)
-- ============================================================
CREATE TABLE IF NOT EXISTS likes (
    id_like         INT AUTO_INCREMENT PRIMARY KEY,
    id_actualite    INT          NOT NULL,
    id_utilisateur  INT          NOT NULL,
    date_creation   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_like_actu FOREIGN KEY (id_actualite)
        REFERENCES actualites(id_actualite) ON DELETE CASCADE,
    CONSTRAINT fk_like_user FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    -- Un utilisateur ne peut liker qu'une fois la même actualité
    UNIQUE KEY uq_like (id_actualite, id_utilisateur),
    INDEX idx_like_actu (id_actualite)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE abonnements
-- Un utilisateur s'abonne à un espace club pour suivre ses actus
-- ============================================================
CREATE TABLE IF NOT EXISTS abonnements (
    id_abonnement   INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur  INT          NOT NULL,
    id_espace       INT          NOT NULL,
    date_abonnement TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_abo_user   FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateur(id_utilisateur) ON DELETE CASCADE,
    CONSTRAINT fk_abo_espace FOREIGN KEY (id_espace)
        REFERENCES espace_club(id_espace) ON DELETE CASCADE,
    -- Un utilisateur ne peut s'abonner qu'une fois au même club
    UNIQUE KEY uq_abonnement (id_utilisateur, id_espace),
    INDEX idx_abo_user   (id_utilisateur),
    INDEX idx_abo_espace (id_espace)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SELECT 'Tables commentaires, likes et abonnements créées.' AS statut;