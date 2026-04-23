import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Script d'import des données du Ministère des Sports dans la base MySQL.
 *
 * Importe 4 fichiers :
 *   - liste-federations.csv  : liste des 120 fédérations sportives
 *   - clubs-data-2023.csv    : clubs par commune et par fédération
 *   - lic-data-2023.csv      : licences par commune, fédération, âge et sexe
 *   - communes-geo.csv       : coordonnées GPS et codes postaux des communes
 */
public class ImportCSV {

    // ========== CONFIGURATION ==========
    static final String DB_URL  = "jdbc:mysql://localhost:3306/clubs_sportifs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASS = "rootpassword";

    static final String FICHIER_FEDERATIONS = "data/liste-federations.csv";
    static final String FICHIER_CLUBS       = "data/clubs-data-2023.csv";
    static final String FICHIER_LICENCES    = "data/lic-data-2023.csv";
    static final String FICHIER_GEO         = "data/communes-geo.csv";
    // ====================================

    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn.setAutoCommit(false); // On gère les commits nous-mêmes (meilleure performance)
        System.out.println("Connexion OK");

        importFederations(conn);
        importCommunes(conn);
        importCoordonnees(conn);
        importClubs(conn);
        importLicences(conn);

        conn.commit();
        conn.close();
        System.out.println("Import terminé !");
    }

    /**
     * 1. Importe les fédérations sportives.
     * Fichier : liste-federations.csv (colonnes : Libellé fédération ; Code fédération)
     */
    static void importFederations(Connection conn) throws Exception {
        System.out.println("Import fédérations...");

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO federation (code_federation, nom_federation) VALUES (?, ?)");

        BufferedReader br = new BufferedReader(new FileReader(FICHIER_FEDERATIONS));
        br.readLine(); // saute l'en-tête

        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";");
            if (c.length < 2) continue;

            String nomFed  = c[0].trim();
            String codeFed = c[1].trim();
            if (codeFed.isEmpty()) continue;

            ps.setString(1, codeFed);
            ps.setString(2, nomFed);
            ps.addBatch();
            count++;
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " fédérations");
    }

    /**
     * 2. Importe les communes.
     * Source : clubs-data-2023.csv (on extrait les communes uniques)
     * Un HashSet évite d'insérer plusieurs fois la même commune.
     */
    static void importCommunes(Connection conn) throws Exception {
        System.out.println("Import communes...");

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO commune (code_commune, nom_commune, departement, region) VALUES (?, ?, ?, ?)");

        Set<String> dejaVu = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(FICHIER_CLUBS));
        Map<String, Integer> idx = indexerEnTete(br.readLine());

        String ligne;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);

            String code = formaterCodeCommune(lire(c, idx, "code commune"));
            if (code.isEmpty() || !dejaVu.add(code)) continue;

            ps.setString(1, code);
            ps.setString(2, lire(c, idx, "commune"));
            ps.setString(3, lire(c, idx, "département"));
            ps.setString(4, lire(c, idx, "région"));
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + dejaVu.size() + " communes");
    }

    /**
     * 3. Ajoute les coordonnées GPS et les codes postaux aux communes existantes.
     * Source : communes-geo.csv (fichier de data.gouv.fr)
     * Nécessaire pour la recherche par rayon géographique.
     */
    static void importCoordonnees(Connection conn) throws Exception {
        System.out.println("Import coordonnées GPS...");

        PreparedStatement ps = conn.prepareStatement(
            "UPDATE commune SET latitude = ?, longitude = ?, code_postal = ? WHERE code_commune = ?");

        BufferedReader br = new BufferedReader(new FileReader(FICHIER_GEO));
        Map<String, Integer> idx = indexerEnTete(br.readLine());

        int iCode = idx.get("code_commune_insee");
        int iLat  = idx.get("latitude");
        int iLon  = idx.get("longitude");
        int iCP   = idx.get("code_postal");

        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(",", -1);
            if (c.length <= iLon) continue;

            String code = formaterCodeCommune(c[iCode]);
            if (code.isEmpty() || c[iLat].isEmpty() || c[iLon].isEmpty()) continue;

            try {
                ps.setDouble(1, Double.parseDouble(c[iLat]));
                ps.setDouble(2, Double.parseDouble(c[iLon]));
                ps.setString(3, c[iCP].trim());
                ps.setString(4, code);
                ps.addBatch();
                count++;
            } catch (NumberFormatException e) {
                // lat/lon invalides → on ignore
            }
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " communes mises à jour avec GPS");
    }

    /**
     * 4. Importe les statistiques de clubs (nombre de clubs par commune × fédération).
     * Fichier : clubs-data-2023.csv (colonnes : ...Code, Fédération, Clubs, EPA, Total)
     */
    static void importClubs(Connection conn) throws Exception {
        System.out.println("Import club_stats...");

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO club_stats (code_commune, code_federation, annee, clubs, epa, total) " +
            "VALUES (?, ?, 2023, ?, ?, ?)");

        BufferedReader br = new BufferedReader(new FileReader(FICHIER_CLUBS));
        Map<String, Integer> idx = indexerEnTete(br.readLine());

        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);

            String codeCommune = formaterCodeCommune(lire(c, idx, "code commune"));
            String codeFed     = lire(c, idx, "code");
            if (codeCommune.isEmpty() || codeFed.isEmpty()) continue;

            ps.setString(1, codeCommune);
            ps.setString(2, codeFed);
            ps.setInt(3, toInt(lire(c, idx, "clubs")));
            ps.setInt(4, toInt(lire(c, idx, "epa")));
            ps.setInt(5, toInt(lire(c, idx, "total")));
            ps.addBatch();
            count++;
            if (count % 1000 == 0) ps.executeBatch();
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " lignes");
    }

    /**
     * 5. Importe les statistiques de licences (tranches d'âge × sexe).
     * Fichier : lic-data-2023.csv (45 colonnes de données)
     * Utilise un tableau pour éviter de répéter 45 fois le même code.
     */
    static void importLicences(Connection conn) throws Exception {
        System.out.println("Import licence_stats...");

        // Les 45 colonnes du CSV à récupérer, dans l'ordre d'insertion SQL
        String[] colonnes = {
            "f - 1 à 4 ans", "f - 5 à 9 ans", "f - 10 à 14 ans", "f - 15 à 19 ans",
            "f - 20 à 24 ans", "f - 25 à 29 ans", "f - 30 à 34 ans", "f - 35 à 39 ans",
            "f - 40 à 44 ans", "f - 45 à 49 ans", "f - 50 à 54 ans", "f - 55 à 59 ans",
            "f - 60 à 64 ans", "f - 65 à 69 ans", "f - 70 à 74 ans", "f - 75 à 79 ans",
            "f - 80 à 99 ans", "f - nr",
            "h - 1 à 4 ans", "h - 5 à 9 ans", "h - 10 à 14 ans", "h - 15 à 19 ans",
            "h - 20 à 24 ans", "h - 25 à 29 ans", "h - 30 à 34 ans", "h - 35 à 39 ans",
            "h - 40 à 44 ans", "h - 45 à 49 ans", "h - 50 à 54 ans", "h - 55 à 59 ans",
            "h - 60 à 64 ans", "h - 65 à 69 ans", "h - 70 à 74 ans", "h - 75 à 79 ans",
            "h - 80 à 99 ans", "h - nr",
            "nr - 5 à 9 ans", "nr - 10 à 14 ans", "nr - 15 à 19 ans", "nr - 30 à 34 ans",
            "nr - 40 à 44 ans", "nr - 45 à 49 ans", "nr - 70 à 74 ans", "nr - nr",
            "total"
        };

        // Construction dynamique de la requête SQL (évite d'écrire 45 "?" à la main)
        StringBuilder placeholders = new StringBuilder("?,?,2023");
        for (int i = 0; i < colonnes.length; i++) placeholders.append(",?");

        String sql = "INSERT INTO licence_stats (code_commune, code_federation, annee, " +
            "f_1_4, f_5_9, f_10_14, f_15_19, f_20_24, f_25_29, f_30_34, f_35_39, f_40_44, " +
            "f_45_49, f_50_54, f_55_59, f_60_64, f_65_69, f_70_74, f_75_79, f_80_99, f_nr, " +
            "h_1_4, h_5_9, h_10_14, h_15_19, h_20_24, h_25_29, h_30_34, h_35_39, h_40_44, " +
            "h_45_49, h_50_54, h_55_59, h_60_64, h_65_69, h_70_74, h_75_79, h_80_99, h_nr, " +
            "nr_5_9, nr_10_14, nr_15_19, nr_30_34, nr_40_44, nr_45_49, nr_70_74, nr_nr, total) " +
            "VALUES (" + placeholders + ")";

        PreparedStatement ps = conn.prepareStatement(sql);
        BufferedReader br = new BufferedReader(new FileReader(FICHIER_LICENCES));
        Map<String, Integer> idx = indexerEnTete(br.readLine());

        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);

            String codeCommune = formaterCodeCommune(lire(c, idx, "code commune"));
            String codeFed     = lire(c, idx, "code");
            if (codeCommune.isEmpty() || codeFed.isEmpty()) continue;

            int i = 1;
            ps.setString(i++, codeCommune);
            ps.setString(i++, codeFed);
            for (String col : colonnes) {
                ps.setInt(i++, toInt(lire(c, idx, col)));
            }
            ps.addBatch();
            count++;

            // Commit par paquets pour éviter un seul énorme commit final
            if (count % 5000 == 0) {
                ps.executeBatch();
                conn.commit();
                System.out.println("  ... " + count + " lignes");
            }
        }
        ps.executeBatch();
        conn.commit();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " lignes");
    }

    // ============= UTILITAIRES =============

    /**
     * Lit l'en-tête d'un CSV et retourne une map "nom de colonne → index".
     * Permet d'accéder aux colonnes par leur nom, peu importe leur ordre.
     */
    static Map<String, Integer> indexerEnTete(String enTete) {
        Map<String, Integer> idx = new HashMap<>();
        // Détecte le séparateur : ; pour les CSV ministère, , pour communes-geo
        String sep = enTete.contains(";") ? ";" : ",";
        String[] cols = enTete.split(sep, -1);
        for (int i = 0; i < cols.length; i++) {
            String nom = cols[i].replace("\"", "").replace("\uFEFF", "").trim().toLowerCase();
            idx.put(nom, i);
        }
        return idx;
    }

    /** Récupère la valeur d'une colonne par son nom */
    static String lire(String[] ligne, Map<String, Integer> idx, String nomColonne) {
        Integer i = idx.get(nomColonne.toLowerCase());
        if (i == null || i >= ligne.length) return "";
        return ligne[i].replace("\"", "").trim();
    }

    /**
     * Complète les codes INSEE sur 5 chiffres (ex: "1004" → "01004").
     * Excel supprime souvent le 0 initial en sauvegardant en CSV.
     */
    static String formaterCodeCommune(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.length() == 4 && s.matches("\\d+")) s = "0" + s;
        return s;
    }

    /** Convertit une chaîne en entier, retourne 0 si vide ou invalide */
    static int toInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}