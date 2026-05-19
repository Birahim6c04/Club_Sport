package com.esigelec.clubsport.service;

import com.esigelec.clubsport.dao.ExportDAO;
import com.esigelec.clubsport.dao.IndicateursDAO;
import com.esigelec.clubsport.dao.LicenceStatsDAO;
import com.esigelec.clubsport.model.ClassementCommune;
import com.esigelec.clubsport.model.ClubsLicenciesDTO;
import com.esigelec.clubsport.model.ExportDashboardDTO;
import com.esigelec.clubsport.model.StatDTO;

import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ExporterPdfService {

    public void exporterDashboardPdf(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Document document = null;

        try {
            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String codeCommune = request.getParameter("codeCommune");
            String federation = request.getParameter("federation");

            if (region == null || region.isEmpty()) {
                response.sendRedirect("ElusDashboard");
                return;
            }

            ExportDAO dao = new ExportDAO();

            List<ExportDashboardDTO> lignes =
                    dao.getDashboardExport(region, departement, federation, codeCommune);

            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=dashboard_elus.pdf"
            );

            document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            document.add(new Paragraph("Dashboard des élus"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            table.addCell("Région");
            table.addCell("Département");
            table.addCell("Commune");
            table.addCell("Fédération");
            table.addCell("Total");
            table.addCell("Hommes");
            table.addCell("Femmes");

            for (ExportDashboardDTO ligne : lignes) {
                table.addCell(safe(ligne.getRegion()));
                table.addCell(safe(ligne.getDepartement()));
                table.addCell(safe(ligne.getCommune()));
                table.addCell(safe(ligne.getFederation()));
                table.addCell(String.valueOf(ligne.getTotalLicencies()));
                table.addCell(String.valueOf(ligne.getHommes()));
                table.addCell(String.valueOf(ligne.getFemmes()));
            }

            document.add(table);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'export PDF du dashboard", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    public void exporterClassementPdf(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Document document = null;

        try {
            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String federation = request.getParameter("federation");

            if (region == null || region.isEmpty()) {
                response.sendRedirect("LeClassement");
                return;
            }

            LicenceStatsDAO dao = new LicenceStatsDAO();

            List<ClassementCommune> classement =
                    dao.getClassementCommunes(region, departement, federation);

            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=classement_communes.pdf"
            );

            document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            document.add(new Paragraph("Classement des communes"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            table.addCell("Région");
            table.addCell("Département");
            table.addCell("Fédération");
            table.addCell("Rang");
            table.addCell("Commune");
            table.addCell("Code postal");
            table.addCell("Licenciés");
            table.addCell("Part (%)");

            int rang = 1;

            for (ClassementCommune commune : classement) {
                table.addCell(safe(region));
                table.addCell(departement == null || departement.isEmpty()
                        ? "Tous les départements"
                        : departement);
                table.addCell(federation == null || federation.isEmpty()
                        ? "Toutes les fédérations"
                        : federation);
                table.addCell(String.valueOf(rang));
                table.addCell(safe(commune.getNomCommune()));
                table.addCell(safe(commune.getCodePostal()));
                table.addCell(String.valueOf(commune.getTotalLicencies()));
                table.addCell(String.valueOf(commune.getPourcentage()));

                rang++;
            }

            document.add(table);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'export PDF du classement", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }

    public void exporterIndicateursPdf(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Document document = null;

        try {
            String region = request.getParameter("region");
            String departement = request.getParameter("departement");
            String codeCommune = request.getParameter("codeCommune");
            String federation = request.getParameter("federation");

            if (region == null || region.isEmpty()) {
                response.sendRedirect("indicateurs");
                return;
            }

            IndicateursDAO dao = new IndicateursDAO();

            List<StatDTO> repartitionAge =
                    dao.getRepartitionAge(region, departement, federation, codeCommune);

            List<ClubsLicenciesDTO> clubsLicenciesFederations =
                    dao.getClubsEtLicenciesParFederation(region, departement, codeCommune);

            List<StatDTO> rapportAgeClubs =
                    dao.getRapportAgeClubs(region, departement, federation, codeCommune);

            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=indicateurs_statistiques.pdf"
            );

            document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            document.add(new Paragraph("Indicateurs statistiques"));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Filtres appliqués"));
            document.add(new Paragraph("Région : " + valeurFiltre(region, "Toutes les régions")));
            document.add(new Paragraph("Département : " + valeurFiltre(departement, "Tous les départements")));
            document.add(new Paragraph("Commune : " + valeurFiltre(codeCommune, "Toutes les communes")));
            document.add(new Paragraph("Fédération : " + valeurFiltre(federation, "Toutes les fédérations")));
            document.add(new Paragraph(" "));

            ajouterTableStat(
                    document,
                    "Répartition âge",
                    repartitionAge,
                    "Tranche d'âge",
                    "Nombre de licenciés"
            );

            ajouterTableClubsLicencies(
                    document,
                    "Clubs et licenciés par fédération",
                    clubsLicenciesFederations
            );

            ajouterTableStat(
                    document,
                    "Rapport âge clubs",
                    rapportAgeClubs,
                    "Tranche d'âge",
                    "Licenciés par club"
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'export PDF des indicateurs", e);
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
    }
    private void ajouterTableStat(
            Document document,
            String titre,
            List<StatDTO> stats,
            String colonneLabel,
            String colonneValeur
    ) throws Exception {

        document.add(new Paragraph(titre));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(colonneLabel);
        table.addCell(colonneValeur);

        for (StatDTO stat : stats) {
            table.addCell(safe(stat.getLabel()));
            table.addCell(String.valueOf(stat.getValeur()));
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void ajouterTableClubsLicencies(
            Document document,
            String titre,
            List<ClubsLicenciesDTO> stats
    ) throws Exception {

        document.add(new Paragraph(titre));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        table.addCell("Fédération");
        table.addCell("Nombre de clubs");
        table.addCell("Nombre de licenciés");

        for (ClubsLicenciesDTO stat : stats) {
            table.addCell(safe(stat.getFederation()));
            table.addCell(String.valueOf(stat.getClubs()));
            table.addCell(String.valueOf(stat.getLicencies()));
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private String safe(String texte) {
        return texte == null ? "" : texte;
    }
    private String valeurFiltre(String valeur, String valeurParDefaut) {
        if (valeur == null || valeur.isEmpty()) {
            return valeurParDefaut;
        }

        return valeur;
    }
}