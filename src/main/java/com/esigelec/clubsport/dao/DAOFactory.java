package com.esigelec.clubsport.dao;

/**
 * DAOFactory — point d'entrée unique pour instancier tous les DAO.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/dao/DAOFactory.java
 */
public class DAOFactory {

    private DAOFactory() {}

    // ── Données de référence ──────────────────────────────────────────
    public static ReferentielDAO  getReferentielDAO()  { return new ReferentielDAO(); }

    // ── Recherche et statistiques ─────────────────────────────────────
    public static SearchClubsDAO  getSearchClubsDAO()  { return new SearchClubsDAO(); }
    public static StatsDAO        getStatsDAO()        { return new StatsDAO(); }

    // ── Espace club ───────────────────────────────────────────────────
    public static EspaceClubDAO   getEspaceClubDAO()   { return new EspaceClubDAO(); }
    public static HorairesDAO     getHorairesDAO()     { return new HorairesDAO(); }
    public static ActualiteDAO    getActualiteDAO()    { return new ActualiteDAO(); }

    // ── Interactions (nouvelles tâches) ───────────────────────────────
    public static CommentaireDAO  getCommentaireDAO()  { return new CommentaireDAO(); }
    public static LikeDAO         getLikeDAO()         { return new LikeDAO(); }
    public static AbonnementDAO   getAbonnementDAO()   { return new AbonnementDAO(); }
}