
	package util ;

	import java.io.BufferedReader;
	import java.io.InputStreamReader;
	import java.net.HttpURLConnection;
	import java.net.URL;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.PreparedStatement;
	import java.sql.ResultSet;
	import java.sql.Statement;
	import java.util.ArrayList;
	import java.util.List;

	import com.google.gson.JsonArray;
	import com.google.gson.JsonObject;
	import com.google.gson.JsonParser;

	
	public class ImportGeolocalisation {

	    private static final String DB_URL      = "jdbc:mysql://localhost:3306/clubs_sportifs"
	                                              + "?useSSL=false&serverTimezone=Europe/Paris"
	                                              + "&allowPublicKeyRetrieval=true";
	    private static final String DB_USER     = "root";
	    private static final String DB_PASSWORD = ""; 

	    private static final String TABLE_COMMUNE  = "commune";
	    private static final String COL_CODE       = "code_commune";
	    private static final String COL_LATITUDE   = "latitude";
	    private static final String COL_LONGITUDE  = "longitude";

	    private static final String API_BASE       = "https://geo.api.gouv.fr/communes/";

	    /** Pause en millisecondes entre chaque appel API (évite le rate-limit) */
	    private static final int    PAUSE_MS       = 60;

	    /** Nombre d'enregistrements traités avant chaque commit */
	    private static final int    BATCH_SIZE     = 100;
	    // =========================================================

	    public static void main(String[] args) {

	        System.out.println("=== Import géolocalisation des communes ===\n");

	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            System.err.println("Driver MySQL introuvable : " + e.getMessage());
	            return;
	        }

	        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

	            conn.setAutoCommit(false);

	            ajouterColonnesSiAbsentes(conn);
	            conn.commit();

	            // Récupérer les codes commune sans coordonnées
	            List<String> codes = recupererCodesManquants(conn);
	            int total = codes.size();
	            System.out.println("[2] " + total + " communes à géolocaliser.\n");

	            if (total == 0) {
	                System.out.println("    Toutes les communes ont déjà des coordonnées.");
	                return;
	            }

	            //  Boucle principale
	            int ok = 0, ko = 0;

	            PreparedStatement pstmt = conn.prepareStatement(
	                "UPDATE " + TABLE_COMMUNE +
	                " SET " + COL_LATITUDE + " = ?, " + COL_LONGITUDE + " = ?" +
	                " WHERE " + COL_CODE + " = ?"
	            );

	            for (int i = 0; i < codes.size(); i++) {
	                String code = codes.get(i);
	                double[] coords = obtenirCoords(code);

	                if (coords != null) {
	                    pstmt.setDouble(1, coords[0]); // latitude
	                    pstmt.setDouble(2, coords[1]); // longitude
	                    pstmt.setString(3, code);
	                    pstmt.executeUpdate();
	                    ok++;
	                } else {
	                    System.out.println("    [SKIP] Code introuvable : " + code);
	                    ko++;
	                }

	                // Commit par batch
	                if ((i + 1) % BATCH_SIZE == 0) {
	                    conn.commit();
	                    System.out.printf("  Progression : %d/%d  ✓%d  ✗%d%n",
	                                      i + 1, total, ok, ko);
	                }

	                // Petite pause pour ne pas saturer l'API
	                Thread.sleep(PAUSE_MS);
	            }

	            // Commit final
	            conn.commit();
	            pstmt.close();

	            System.out.println("\n=== Terminé ===");
	            System.out.println("  Communes géolocalisées : " + ok);
	            System.out.println("  Codes non trouvés      : " + ko);
	            System.out.println("  Total traité           : " + total);

	        } catch (Exception e) {
	            System.err.println("Erreur : " + e.getMessage());
	            e.printStackTrace();
	        }
	    }

	    // ----------------------------------------------------------
	    // Ajoute latitude et longitude à la table si absentes
	    // ----------------------------------------------------------
	    private static void ajouterColonnesSiAbsentes(Connection conn) throws Exception {

	        System.out.println("[1] Vérification des colonnes lat/lng...");

	        // Récupérer les colonnes existantes
	        List<String> colonnes = new ArrayList<>();
	        try (ResultSet rs = conn.getMetaData()
	                .getColumns(null, null, TABLE_COMMUNE, null)) {
	            while (rs.next()) {
	                colonnes.add(rs.getString("COLUMN_NAME").toLowerCase());
	            }
	        }

	        try (Statement stmt = conn.createStatement()) {
	            if (!colonnes.contains(COL_LATITUDE)) {
	                stmt.execute("ALTER TABLE " + TABLE_COMMUNE
	                             + " ADD COLUMN " + COL_LATITUDE + " DECIMAL(10,7)");
	                System.out.println("    Colonne '" + COL_LATITUDE + "' ajoutée.");
	            }
	            if (!colonnes.contains(COL_LONGITUDE)) {
	                stmt.execute("ALTER TABLE " + TABLE_COMMUNE
	                             + " ADD COLUMN " + COL_LONGITUDE + " DECIMAL(10,7)");
	                System.out.println("    Colonne '" + COL_LONGITUDE + "' ajoutée.");
	            }
	        }

	        if (colonnes.contains(COL_LATITUDE) && colonnes.contains(COL_LONGITUDE)) {
	            System.out.println("    Colonnes déjà présentes.");
	        }
	    }

	    // ----------------------------------------------------------
	    // Récupère les codes commune dont lat ou lng sont NULL
	    // ----------------------------------------------------------
	    private static List<String> recupererCodesManquants(Connection conn) throws Exception {

	        System.out.println("[2] Récupération des communes sans coordonnées...");

	        List<String> codes = new ArrayList<>();
	        String sql = "SELECT DISTINCT " + COL_CODE + " FROM " + TABLE_COMMUNE
	                   + " WHERE " + COL_LATITUDE  + " IS NULL"
	                   + "    OR " + COL_LONGITUDE + " IS NULL";

	        try (Statement stmt = conn.createStatement();
	             ResultSet rs   = stmt.executeQuery(sql)) {
	            while (rs.next()) {
	                codes.add(rs.getString(COL_CODE));
	            }
	        }
	        return codes;
	    }

	    // ----------------------------------------------------------
	    // Appelle l'API Geo pour un code commune INSEE
	    // Retourne [latitude, longitude] ou null si introuvable
	    // ----------------------------------------------------------
	    private static double[] obtenirCoords(String codeCommune) {

	        // URL : https://geo.api.gouv.fr/communes/76540?fields=centre
	        String urlStr = API_BASE + codeCommune + "?fields=centre&format=json";

	        try {
	            URL url = new URL(urlStr);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("GET");
	            conn.setConnectTimeout(5000);
	            conn.setReadTimeout(5000);
	            conn.setRequestProperty("Accept", "application/json");

	            int status = conn.getResponseCode();
	            if (status != 200) {
	                return null;
	            }

	            // Lire la réponse
	            BufferedReader reader = new BufferedReader(
	                new InputStreamReader(conn.getInputStream(), "UTF-8"));
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            reader.close();
	            conn.disconnect();

	            // Parser le JSON avec Gson
	            // Réponse attendue :
	            // { "centre": { "type": "Point", "coordinates": [1.0808, 49.4431] } }
	            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();

	            if (!json.has("centre") || json.get("centre").isJsonNull()) {
	                return null;
	            }

	            JsonObject centre = json.getAsJsonObject("centre");
	            JsonArray coordinates = centre.getAsJsonArray("coordinates");

	            // GeoJSON : coordinates[0] = longitude, coordinates[1] = latitude
	            double longitude = coordinates.get(0).getAsDouble();
	            double latitude  = coordinates.get(1).getAsDouble();

	            return new double[]{latitude, longitude};

	        } catch (Exception e) {
	            // Silencieux — le code sera marqué comme SKIP
	            return null;
	        }
	    }
	}

