package com.esigelec.clubsport.api;
 
import com.esigelec.clubsport.dao.ClubDAO;
import com.esigelec.clubsport.model.Club;
import com.google.gson.Gson;
 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * API REST : recherche des clubs par fédération et/ou région.
 *
 * Endpoints :
 *   GET /api/clubs?federation=111                       -> tous les clubs FFF
 *   GET /api/clubs?region=Île-de-France                  -> tous les clubs d'Île-de-France
 *   GET /api/clubs?federation=111&region=Bretagne        -> les deux
 *   GET /api/clubs/regions                               -> liste des régions
 */
@WebServlet(urlPatterns = {"/api/clubs", "/api/clubs/regions", "/api/clubs/communes"})
public class ClubApiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClubApiServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private final ClubDAO clubDAO = new ClubDAO();
    private final Gson gson = new Gson();
    
 // Rayons autorisés
    private static final List<Integer> RAYONS_AUTORISES = Arrays.asList(10, 20, 50, 100);
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
 
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
 
        String path = req.getServletPath();
 
        try (PrintWriter out = resp.getWriter()) {
 
            // --- /api/clubs/regions ---
            if (path.endsWith("/regions")) {
                out.write(gson.toJson(clubDAO.listerRegions()));
                return;
            }
 
            // --- /api/clubs/communes?q=... ---
            if (path.endsWith("/communes")) {
                String q = req.getParameter("q");
                if (q == null || q.length() < 2) {
                    erreur(resp, out, 400, "Paramètre 'q' requis (au moins 2 caractères)");
                    return;
                }
                out.write(gson.toJson(clubDAO.rechercherCommune(q)));
                return;
            }
 
            // --- /api/clubs ---
            String federation = req.getParameter("federation");
            String region     = req.getParameter("region");
            String commune    = req.getParameter("commune");
            String rayonStr   = req.getParameter("rayon");
 
            // Cas 1 : recherche par RAYON
            if (commune != null && !commune.isBlank() && rayonStr != null) {
                int rayon;
                try { rayon = Integer.parseInt(rayonStr); }
                catch (NumberFormatException e) {
                    erreur(resp, out, 400, "Paramètre 'rayon' invalide");
                    return;
                }
                if (!RAYONS_AUTORISES.contains(rayon)) {
                    erreur(resp, out, 400, "Rayon doit être parmi " + RAYONS_AUTORISES + " km");
                    return;
                }
                List<Club> clubs = clubDAO.rechercherParRayon(federation, commune, rayon);
                out.write(gson.toJson(clubs));
                return;
            }
 
            // Cas 2 : recherche par FÉDÉRATION / RÉGION
            if ((federation == null || federation.isBlank())
                && (region == null || region.isBlank())) {
                erreur(resp, out, 400,
                    "Critère requis : 'federation', 'region', ou ('commune' + 'rayon')");
                return;
            }
            out.write(gson.toJson(clubDAO.rechercher(federation, region)));
 
        } catch (IllegalArgumentException e) {
            erreur(resp, resp.getWriter(), 400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            erreur(resp, resp.getWriter(), 500, e.getMessage());
        }
    }
 
    private void erreur(HttpServletResponse resp, PrintWriter out, int status, String msg) {
        resp.setStatus(status);
        Map<String, String> err = new HashMap<>();
        err.put("error", msg);
        out.write(gson.toJson(err));
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
