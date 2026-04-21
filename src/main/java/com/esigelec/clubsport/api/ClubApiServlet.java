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
@WebServlet(urlPatterns = {"/api/clubs", "/api/clubs/regions"})
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
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
 
        // Headers communs : JSON + CORS (pour appel depuis un frontend)
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
 
        String path = req.getServletPath() + (req.getPathInfo() != null ? req.getPathInfo() : "");
 
        try (PrintWriter out = resp.getWriter()) {
 
            // --- Endpoint : /api/clubs/regions ---
            if (path.endsWith("/regions")) {
                List<String> regions = clubDAO.listerRegions();
                out.write(gson.toJson(regions));
                return;
            }
 
            // --- Endpoint : /api/clubs ---
            String federation = req.getParameter("federation");
            String region     = req.getParameter("region");
 
            // Au moins un critère obligatoire
            if ((federation == null || federation.isBlank())
                && (region == null || region.isBlank())) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> err = new HashMap<>();
                err.put("error", "Au moins un critère requis : 'federation' ou 'region'");
                out.write(gson.toJson(err));
                return;
            }
 
            List<Club> clubs = clubDAO.rechercher(federation, region);
            out.write(gson.toJson(clubs));
 
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            resp.getWriter().write(gson.toJson(err));
        }
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
