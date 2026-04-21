import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ImportCSV {

    // ======== CONFIG ========
    static final String DB_URL  = "jdbc:mysql://localhost:3306/clubs_sportifs?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String DB_USER = "root";
    static final String DB_PASS = "rootpassword";

    static final String FICHIER_FEDERATIONS = "data/liste-federations.csv";
    static final String FICHIER_CLUBS       = "data/clubs-data-2019.csv";
    static final String FICHIER_LICENCES    = "data/lic-data-2019.csv";
    // ========================

    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        conn.setAutoCommit(false);
        System.out.println("Connexion OK");

        importFederations(conn);
        importCommunes(conn);
        importClubs(conn);
        importLicences(conn);

        conn.commit();
        conn.close();
        System.out.println("Import terminé !");
    }

    // -------- 1. Fédérations --------
    static void importFederations(Connection conn) throws Exception {
        System.out.println("Import fédérations...");
        PreparedStatement ps = conn.prepareStatement(
            "INSERT IGNORE INTO federation (code_federation, nom_federation) VALUES (?, ?)");

        BufferedReader br = new BufferedReader(new FileReader(FICHIER_FEDERATIONS));
        String enTete = br.readLine();
        String[] colonnes = enTete.split(";", -1);

        int idxNom = -1, idxCode = -1;
        for (int i = 0; i < colonnes.length; i++) {
            String nom = nettoyer(colonnes[i]).toLowerCase();
            if (nom.contains("libell") || nom.equals("nom") || nom.contains("nom fédération")) idxNom = i;
            else if (nom.contains("code")) idxCode = i;
        }

        if (idxNom < 0 || idxCode < 0) {
            System.out.println("En-tête : " + enTete);
            throw new RuntimeException("Colonnes nom/code introuvables dans " + FICHIER_FEDERATIONS);
        }

        String ligne;
        int count = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);
            if (c.length <= Math.max(idxNom, idxCode)) continue;

            String nomFed  = nettoyer(c[idxNom]);
            String codeFed = nettoyer(c[idxCode]);
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

    // -------- 2. Communes (déduites de clubs-data-2023.csv) --------
    static void importCommunes(Connection conn) throws Exception {
        System.out.println("Import communes...");
        PreparedStatement ps = conn.prepareStatement(
            "INSERT IGNORE INTO commune (code_commune, nom_commune, departement, region, code_qpv, nom_qpv, statut_geo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)");

        Set<String> vues = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(FICHIER_CLUBS));

        String enTete = br.readLine();
        Map<String, Integer> idx = indexerColonnes(enTete);

        String ligne;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);

            String code = nettoyerCodeCommune(getCol(c, idx, "code commune"));
            if (code.isEmpty() || code.length() > 5 || !vues.add(code)) continue;

            ps.setString(1, code);
            ps.setString(2, getCol(c, idx, "commune"));
            ps.setString(3, getCol(c, idx, "département"));
            ps.setString(4, getCol(c, idx, "région"));
            ps.setString(5, nullSiVide(getCol(c, idx, "code qpv")));
            ps.setString(6, nullSiVide(getCol(c, idx, "nom qpv")));
            ps.setString(7, nullSiVide(getCol(c, idx, "statut géo")));
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + vues.size() + " communes");
    }

    // -------- 3. Stats clubs --------
    // Colonnes : Code Commune ; Commune ; Code QPV ; Nom QPV ; Département ; Région ; Statut géo ; Code ; Fédération ; Clubs ; EPA ; Total
    static void importClubs(Connection conn) throws Exception {
        System.out.println("Import club_stats...");
        PreparedStatement ps = conn.prepareStatement(
            "INSERT IGNORE INTO club_stats (code_commune, code_federation, annee, clubs, epa, total) " +
            "VALUES (?, ?, 2023, ?, ?, ?)");

        BufferedReader br = new BufferedReader(new FileReader(FICHIER_CLUBS));
        String enTete = br.readLine();
        Map<String, Integer> idx = indexerColonnes(enTete);

        String ligne;
        int count = 0, ignores = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);

            String codeCommune = nettoyerCodeCommune(getCol(c, idx, "code commune"));
            String codeFed     = nettoyer(getCol(c, idx, "code"));

            if (codeCommune.isEmpty() || codeFed.isEmpty()) { ignores++; continue; }
            if (codeCommune.length() > 5 || codeFed.length() > 5) { ignores++; continue; }

            ps.setString(1, codeCommune);
            ps.setString(2, codeFed);
            ps.setInt(3, toInt(getCol(c, idx, "clubs")));
            ps.setInt(4, toInt(getCol(c, idx, "epa")));
            ps.setInt(5, toInt(getCol(c, idx, "total")));
            ps.addBatch();
            count++;
            if (count % 1000 == 0) ps.executeBatch();
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " lignes (" + ignores + " ignorées)");
    }

    // -------- 4. Stats licences --------
    static void importLicences(Connection conn) throws Exception {
        System.out.println("Import licence_stats...");

        String[] tranches = {
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

        StringBuilder placeholders = new StringBuilder("?,?,2023");
        for (int i = 0; i < tranches.length; i++) placeholders.append(",?");

        String sql = "INSERT IGNORE INTO licence_stats (code_commune, code_federation, annee, " +
            "f_1_4, f_5_9, f_10_14, f_15_19, f_20_24, f_25_29, f_30_34, f_35_39, f_40_44, " +
            "f_45_49, f_50_54, f_55_59, f_60_64, f_65_69, f_70_74, f_75_79, f_80_99, f_nr, " +
            "h_1_4, h_5_9, h_10_14, h_15_19, h_20_24, h_25_29, h_30_34, h_35_39, h_40_44, " +
            "h_45_49, h_50_54, h_55_59, h_60_64, h_65_69, h_70_74, h_75_79, h_80_99, h_nr, " +
            "nr_5_9, nr_10_14, nr_15_19, nr_30_34, nr_40_44, nr_45_49, nr_70_74, nr_nr, total) " +
            "VALUES (" + placeholders + ")";

        PreparedStatement ps = conn.prepareStatement(sql);
        BufferedReader br = new BufferedReader(new FileReader(FICHIER_LICENCES));
        String enTete = br.readLine();
        Map<String, Integer> idx = indexerColonnes(enTete);

        String ligne;
        int count = 0, ignores = 0;
        while ((ligne = br.readLine()) != null) {
            String[] c = ligne.split(";", -1);

            String codeCommune = nettoyerCodeCommune(getCol(c, idx, "code commune"));
            String codeFed     = nettoyer(getCol(c, idx, "code"));

            if (codeCommune.isEmpty() || codeFed.isEmpty()) { ignores++; continue; }
            if (codeCommune.length() > 5 || codeFed.length() > 5) { ignores++; continue; }

            int i = 1;
            ps.setString(i++, codeCommune);
            ps.setString(i++, codeFed);
            for (String tranche : tranches) {
                ps.setInt(i++, toInt(getCol(c, idx, tranche)));
            }
            ps.addBatch();
            count++;
            if (count % 500 == 0) ps.executeBatch();
        }
        ps.executeBatch();
        ps.close();
        br.close();
        System.out.println("  -> " + count + " lignes (" + ignores + " ignorées)");
    }

    // ============= UTILITAIRES =============
    static Map<String, Integer> indexerColonnes(String enTete) {
        Map<String, Integer> idx = new HashMap<>();
        String[] cols = enTete.split(";", -1);
        for (int i = 0; i < cols.length; i++) {
            idx.put(nettoyer(cols[i]).toLowerCase(), i);
        }
        return idx;
    }

    static String getCol(String[] c, Map<String, Integer> idx, String nom) {
        Integer i = idx.get(nom.toLowerCase());
        if (i == null || i >= c.length) return "";
        return nettoyer(c[i]);
    }

    static String nettoyer(String s) {
        if (s == null) return "";
        s = s.replace("\uFEFF", "").trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.trim();
    }

    static String nettoyerCodeCommune(String s) {
        s = nettoyer(s);
        if (s.isEmpty()) return "";
        // Les codes INSEE font 5 chiffres (ex: Paris = 75056, Ambérieu = 01004)
        // Excel peut avoir tronqué le 0 de tête
        if (s.length() == 4 && s.matches("\\d+")) s = "0" + s;
        return s;
    }

    static String nullSiVide(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    static int toInt(String s) {
        if (s == null) return 0;
        s = s.trim().replace(" ", "");
        if (s.isEmpty()) return 0;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return 0; }
    }
}