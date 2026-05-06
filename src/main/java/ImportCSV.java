import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImportCSV {

    static final String DB_URL  = "jdbc:mysql://localhost:3306/clubs_sportifs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASS = " ";

    static final String FICHIER_FEDERATIONS = "data/liste-federations.csv";
    static final String FICHIER_CLUBS       = "data/clubs-data-2019.csv";
    static final String FICHIER_LICENCES    = "data/lic-data-2019.csv";
    static final String FICHIER_GEO         = "data/communes-geo.csv";

    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn.setAutoCommit(false);
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

    // ============================================================
    // 1. IMPORT DES FEDERATIONS
    // Format : "Libellé fédération;Code fédération"
    // Encodage : Latin-1, séparateur ;
    // ============================================================
    static void importFederations(Connection conn) throws Exception {
        System.out.println("Import fédérations...");

        BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(FICHIER_FEDERATIONS), "ISO-8859-1"));

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO federation (code_federation, nom_federation) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE nom_federation = VALUES(nom_federation)");

        br.readLine(); // saute l'en-tête
        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            String[] colonnes = ligne.split(";");
            if (colonnes.length < 2) continue;

            String nom  = colonnes[0].trim();
            String code = colonnes[1].trim();
            if (code.isEmpty()) continue;

            ps.setString(1, code);
            ps.setString(2, nom);
            ps.addBatch();
            count++;
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " fédérations");
    }

    // ============================================================
    // 2. IMPORT DES COMMUNES
    // Lit clubs-data-2019.csv puis lic-data-2019.csv pour ne rater aucune commune
    // ============================================================
    static void importCommunes(Connection conn) throws Exception {
        System.out.println("Import communes...");

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO commune (code_commune, nom_commune, departement, region) VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE nom_commune = VALUES(nom_commune)");

        Set<String> dejaVu = new HashSet<>();

        // --- Source 1 : clubs-data-2019.csv (séparateur ;, valeurs entre guillemets) ---
        BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(FICHIER_CLUBS), StandardCharsets.UTF_8));
        br.readLine();
        String ligne;
        while ((ligne = br.readLine()) != null) {
            List<String> c = parserCsv(ligne, ';');
            if (c.size() < 6) continue;

            String code   = formaterCode(c.get(0));
            String nom    = c.get(1);
            String dept   = c.get(4);
            String region = c.get(5);

            if (code.isEmpty() || code.length() > 5 || !dejaVu.add(code)) continue;

            ps.setString(1, code);
            ps.setString(2, nom);
            ps.setString(3, dept);
            ps.setString(4, region);
            ps.addBatch();
        }
        br.close();

        // --- Source 2 : lic-data-2019.csv (séparateur ,) ---
        br = new BufferedReader(
            new InputStreamReader(new FileInputStream(FICHIER_LICENCES), StandardCharsets.UTF_8));
        br.readLine();
        while ((ligne = br.readLine()) != null) {
            List<String> c = parserCsv(ligne, ',');
            if (c.size() < 4) continue;

            String code   = formaterCode(c.get(0));
            String nom    = c.get(1);
            String dept   = c.get(2);
            String region = c.get(3);

            if (code.isEmpty() || code.length() > 5 || !dejaVu.add(code)) continue;

            ps.setString(1, code);
            ps.setString(2, nom);
            ps.setString(3, dept);
            ps.setString(4, region);
            ps.addBatch();
        }
        br.close();

        ps.executeBatch();
        ps.close();
        System.out.println("  -> " + dejaVu.size() + " communes");
    }

    // ============================================================
    // 3. IMPORT DES COORDONNEES GPS
    // Format : code_commune_INSEE,nom_commune_postal,code_postal,libelle,ligne_5,latitude,longitude,...
    // Indices :        0          ,        1        ,     2     ,   3   ,   4   ,    5   ,    6    ,...
    // ============================================================
    static void importCoordonnees(Connection conn) throws Exception {
        System.out.println("Import coordonnées GPS...");

        BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(FICHIER_GEO), StandardCharsets.UTF_8));

        PreparedStatement ps = conn.prepareStatement(
            "UPDATE commune SET latitude = ?, longitude = ?, code_postal = ? WHERE code_commune = ?");

        br.readLine(); // saute l'en-tête
        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            List<String> c = parserCsv(ligne, ',');
            if (c.size() < 7) continue;

            String code = formaterCode(c.get(0));
            if (code.isEmpty() || code.length() > 5) continue;

            String cp     = formaterCodePostal(c.get(2));
            String latStr = c.get(5);
            String lonStr = c.get(6);
            if (latStr.isEmpty() || lonStr.isEmpty()) continue;

            try {
                ps.setDouble(1, Double.parseDouble(latStr));
                ps.setDouble(2, Double.parseDouble(lonStr));
                ps.setString(3, cp);
                ps.setString(4, code);
                ps.addBatch();
                count++;
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " communes mises à jour");
    }

    // ============================================================
    // 4. IMPORT DES STATS CLUBS
    // Format : "code commune";"commune";"code qpv";"nom qpv";"département";"région";"statut géo";"code";"fédération";"clubs";"epa";"total"
    // Indices :       0       ;    1   ;     2    ;    3    ;       4     ;    5   ;      6     ;   7  ;      8     ;    9  ;  10 ;   11
    // ============================================================
    static void importClubs(Connection conn) throws Exception {
        System.out.println("Import club_stats...");

        Set<String> federationsValides = chargerCodesFederations(conn);

        BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(FICHIER_CLUBS), StandardCharsets.UTF_8));

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO club_stats (code_commune, code_federation, annee, clubs, epa, total) " +
            "VALUES (?, ?, 2019, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE clubs = VALUES(clubs), epa = VALUES(epa), total = VALUES(total)");

        br.readLine(); // saute l'en-tête
        String ligne;
        int count = 0, ignores = 0;
        while ((ligne = br.readLine()) != null) {
            List<String> c = parserCsv(ligne, ';');
            if (c.size() < 12) { ignores++; continue; }

            String code = formaterCode(c.get(0));
            String fed  = c.get(7);

            if (code.isEmpty() || code.length() > 5) { ignores++; continue; }
            if (fed.isEmpty()  || fed.length()  > 5) { ignores++; continue; }
            if (!federationsValides.contains(fed))   { ignores++; continue; }

            ps.setString(1, code);
            ps.setString(2, fed);
            ps.setInt(3, parseInt(c.get(9)));   // Clubs
            ps.setInt(4, parseInt(c.get(10)));  // EPA
            ps.setInt(5, parseInt(c.get(11)));  // Total
            ps.addBatch();
            count++;
            if (count % 1000 == 0) ps.executeBatch();
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " lignes (" + ignores + " ignorées)");
    }

    // ============================================================
    // 5. IMPORT DES STATS LICENCES
    // Format : code_commune,commune,departement,region,code_federation,nom_federation,
    //          18 tranches femmes (l_f_*), 18 tranches hommes (l_h_*),
    //          8 tranches NR, total_licences
    // Soit 51 colonnes au total
    // ============================================================
    static void importLicences(Connection conn) throws Exception {
        System.out.println("Import licence_stats...");

        Set<String> federationsValides = chargerCodesFederations(conn);

        BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(FICHIER_LICENCES), StandardCharsets.UTF_8));

        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO licence_stats (code_commune, code_federation, annee, " +
            "f_1_4, f_5_9, f_10_14, f_15_19, f_20_24, f_25_29, f_30_34, f_35_39, f_40_44, " +
            "f_45_49, f_50_54, f_55_59, f_60_64, f_65_69, f_70_74, f_75_79, f_80_99, f_nr, " +
            "h_1_4, h_5_9, h_10_14, h_15_19, h_20_24, h_25_29, h_30_34, h_35_39, h_40_44, " +
            "h_45_49, h_50_54, h_55_59, h_60_64, h_65_69, h_70_74, h_75_79, h_80_99, h_nr, " +
            "nr_5_9, nr_10_14, nr_15_19, nr_30_34, nr_40_44, nr_45_49, nr_70_74, nr_nr, total) " +
            "VALUES (?, ?, 2019, " +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +     // 18 femmes
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +     // 18 hommes
            "?,?,?,?,?,?,?,?,?) " +                       // 8 NR + total
            "ON DUPLICATE KEY UPDATE total = VALUES(total)");

        br.readLine();
        String ligne;
        int count = 0, ignores = 0;
        while ((ligne = br.readLine()) != null) {
            List<String> c = parserCsv(ligne, ',');
            if (c.size() < 51) { ignores++; continue; }

            String code = formaterCode(c.get(0));
            String fed  = c.get(4);

            if (code.isEmpty() || code.length() > 5) { ignores++; continue; }
            if (fed.isEmpty()  || fed.length()  > 5) { ignores++; continue; }
            if (!federationsValides.contains(fed))   { ignores++; continue; }

            // Indices CSV :
            // 0..5   = code_commune, commune, dept, region, code_federation, nom_federation
            // 6..23  = 18 colonnes femmes (l_f_1_4 -> l_f_nr)
            // 24..41 = 18 colonnes hommes (l_h_1_4 -> l_h_nr)
            // 42..49 = 8 colonnes NR
            // 50     = total_licences

            ps.setString(1, code);
            ps.setString(2, fed);
            for (int i = 0; i < 18; i++) ps.setInt(3 + i,  parseInt(c.get(6  + i))); // 18 femmes
            for (int i = 0; i < 18; i++) ps.setInt(21 + i, parseInt(c.get(24 + i))); // 18 hommes
            for (int i = 0; i < 8;  i++) ps.setInt(39 + i, parseInt(c.get(42 + i))); // 8 NR
            ps.setInt(47, parseInt(c.get(50))); // total

            ps.addBatch();
            count++;
            if (count % 5000 == 0) {
                ps.executeBatch();
                conn.commit();
            }
        }
        ps.executeBatch();
        conn.commit();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " lignes (" + ignores + " ignorées)");
    }

    // ============================================================
    // METHODES UTILITAIRES
    // ============================================================

    /**
     * Parse une ligne CSV en gérant les valeurs entre guillemets.
     * Exemple : 01001,"FF de Judo, Jujitsu, Kendo et DA",10
     *           => ["01001", "FF de Judo, Jujitsu, Kendo et DA", "10"]
     */
    static List<String> parserCsv(String ligne, char separateur) {
        List<String> resultat = new ArrayList<>();
        StringBuilder valeur = new StringBuilder();
        boolean dansGuillemets = false;

        for (int i = 0; i < ligne.length(); i++) {
            char ch = ligne.charAt(i);
            if (ch == '"') {
                dansGuillemets = !dansGuillemets;
            } else if (ch == separateur && !dansGuillemets) {
                resultat.add(valeur.toString().trim());
                valeur.setLength(0);
            } else {
                valeur.append(ch);
            }
        }
        resultat.add(valeur.toString().trim());
        return resultat;
    }

    /** Complète le code commune sur 5 chiffres (ex : "1001" -> "01001") */
    static String formaterCode(String s) {
        if (s == null) return "";
        s = s.trim();
        while (s.length() < 5 && s.matches("\\d+")) s = "0" + s;
        return s;
    }

    /** Complète le code postal sur 5 chiffres (ex : "1400" -> "01400") */
    static String formaterCodePostal(String s) {
        if (s == null) return "";
        s = s.trim();
        while (s.length() < 5 && s.matches("\\d+")) s = "0" + s;
        return s;
    }

    /** Convertit une chaîne en entier, retourne 0 si vide ou invalide */
    static int parseInt(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    /** Charge les codes fédération valides depuis la BDD */
    static Set<String> chargerCodesFederations(Connection conn) throws Exception {
        Set<String> set = new HashSet<>();
        PreparedStatement ps = conn.prepareStatement("SELECT code_federation FROM federation");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) set.add(rs.getString(1));
        rs.close();
        ps.close();
        return set;
    }
}