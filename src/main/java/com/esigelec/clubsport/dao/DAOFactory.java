package com.esigelec.clubsport.dao;

/**
 * DAOFactory — point d'entrée unique pour instancier tous les DAO.
 * Les Servlets passent exclusivement par cette factory.
 *
 * Chemin : src/main/java/com/esigelec/clubsport/dao/DAOFactory.java
 */
public class DAOFactory {

    private DAOFactory() {}

    // ── DAO données de référence ──────────────────────────────────────
    public static ReferentielDAO  getReferentielDAO()  { return new ReferentielDAO(); }

    // ── DAO recherche et statistiques ─────────────────────────────────
    public static SearchClubsDAO  getSearchClubsDAO()  { return new SearchClubsDAO(); }
    public static StatsDAO        getStatsDAO()        { return new StatsDAO(); }

    // ── DAO espace club ───────────────────────────────────────────────
    public static EspaceClubDAO   getEspaceClubDAO()   { return new EspaceClubDAO(); }
    public static HorairesDAO     getHorairesDAO()     { return new HorairesDAO(); }
    public static ActualiteDAO    getActualiteDAO()    { return new ActualiteDAO(); }
}