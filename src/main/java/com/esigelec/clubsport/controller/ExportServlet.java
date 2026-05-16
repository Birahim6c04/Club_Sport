package com.esigelec.clubsport.controller;

import com.esigelec.clubsport.dao.ExportDAO;
import com.esigelec.clubsport.dao.IndicateursDAO;
import com.esigelec.clubsport.dao.LicenceStatsDAO;
import com.esigelec.clubsport.model.ClassementCommune;
import com.esigelec.clubsport.model.ExportDashboardDTO;
import com.esigelec.clubsport.model.StatDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");

        if ("dashboard".equals(type) || "ElusDashboard".equals(type)) {
            exporterDashboard(request, response);
            return;
        }

        if ("classement".equals(type)) {
            exporterClassement(request, response);
            return;
        }

        if ("indicateurs".equals(type)) {
            exporterIndicateurs(request, response);
            return;
        }

        response.sendRedirect("ElusDashboard");
    }

    private void exporterDashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Dashboard");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Région");
            header.createCell(1).setCellValue("Département");
            header.createCell(2).setCellValue("Commune");
            header.createCell(3).setCellValue("Fédération");
            header.createCell(4).setCellValue("Total licenciés");
            header.createCell(5).setCellValue("Hommes");
            header.createCell(6).setCellValue("Femmes");

            int rowIndex = 1;

            for (ExportDashboardDTO ligne : lignes) {
                Row row = sheet.createRow(rowIndex);

                row.createCell(0).setCellValue(ligne.getRegion());
                row.createCell(1).setCellValue(ligne.getDepartement());
                row.createCell(2).setCellValue(ligne.getCommune());
                row.createCell(3).setCellValue(ligne.getFederation());
                row.createCell(4).setCellValue(ligne.getTotalLicencies());
                row.createCell(5).setCellValue(ligne.getHommes());
                row.createCell(6).setCellValue(ligne.getFemmes());

                rowIndex++;
            }

            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=dashboard_elus.xlsx"
            );

            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'export Excel du dashboard", e);
        }
    }

    private void exporterClassement(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Classement");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Région");
            header.createCell(1).setCellValue("Département");
            header.createCell(2).setCellValue("Fédération");
            header.createCell(3).setCellValue("Rang");
            header.createCell(4).setCellValue("Commune");
            header.createCell(5).setCellValue("Code postal");
            header.createCell(6).setCellValue("Licenciés");
            header.createCell(7).setCellValue("Part (%)");

            int rowIndex = 1;
            int rang = 1;

            for (ClassementCommune commune : classement) {
                Row row = sheet.createRow(rowIndex);

                row.createCell(0).setCellValue(region);
                row.createCell(1).setCellValue(
                        departement == null || departement.isEmpty()
                                ? "Tous les départements"
                                : departement
                );
                row.createCell(2).setCellValue(
                        federation == null || federation.isEmpty()
                                ? "Toutes les fédérations"
                                : federation
                );

                row.createCell(3).setCellValue(rang);
                row.createCell(4).setCellValue(commune.getNomCommune());
                row.createCell(5).setCellValue(commune.getCodePostal());
                row.createCell(6).setCellValue(commune.getTotalLicencies());
                row.createCell(7).setCellValue(commune.getPourcentage());

                rowIndex++;
                rang++;
            }

            for (int i = 0; i <= 7; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=classement_communes.xlsx"
            );

            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'export Excel du classement", e);
        }
    }

    private void exporterIndicateurs(HttpServletRequest request, HttpServletResponse response) throws IOException {

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

            List<StatDTO> clubsFederations =
                    dao.getClubsParFederation(region, departement, codeCommune);

            List<StatDTO> rapportAgeClubs =
                    dao.getRapportAgeClubs(region, departement, federation, codeCommune);

            Workbook workbook = new XSSFWorkbook();

            remplirFeuilleStat(
                    workbook,
                    "Répartition âge",
                    region,
                    departement,
                    codeCommune,
                    federation,
                    repartitionAge,
                    "Tranche d'âge",
                    "Nombre de licenciés"
            );

            remplirFeuilleStat(
                    workbook,
                    "Clubs fédérations",
                    region,
                    departement,
                    codeCommune,
                    federation,
                    clubsFederations,
                    "Fédération",
                    "Nombre de clubs"
            );

            remplirFeuilleStat(
                    workbook,
                    "Rapport âge clubs",
                    region,
                    departement,
                    codeCommune,
                    federation,
                    rapportAgeClubs,
                    "Tranche d'âge",
                    "Licenciés par club"
            );

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );

            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=indicateurs_statistiques.xlsx"
            );

            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Erreur lors de l'export Excel des indicateurs", e);
        }
    }

    private void remplirFeuilleStat(
            Workbook workbook,
            String nomFeuille,
            String region,
            String departement,
            String codeCommune,
            String federation,
            List<StatDTO> stats,
            String colonneLabel,
            String colonneValeur
    ) {
        Sheet sheet = workbook.createSheet(nomFeuille);

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Région");
        header.createCell(1).setCellValue("Département");
        header.createCell(2).setCellValue("Commune");
        header.createCell(3).setCellValue("Fédération");
        header.createCell(4).setCellValue(colonneLabel);
        header.createCell(5).setCellValue(colonneValeur);

        int rowIndex = 1;

        for (StatDTO stat : stats) {
            Row row = sheet.createRow(rowIndex);

            row.createCell(0).setCellValue(
                    region == null || region.isEmpty()
                            ? "Toutes les régions"
                            : region
            );

            row.createCell(1).setCellValue(
                    departement == null || departement.isEmpty()
                            ? "Tous les départements"
                            : departement
            );

            row.createCell(2).setCellValue(
                    codeCommune == null || codeCommune.isEmpty()
                            ? "Toutes les communes"
                            : codeCommune
            );

            row.createCell(3).setCellValue(
                    federation == null || federation.isEmpty()
                            ? "Toutes les fédérations"
                            : federation
            );

            row.createCell(4).setCellValue(stat.getLabel());
            row.createCell(5).setCellValue(stat.getValeur());

            rowIndex++;
        }

        for (int i = 0; i <= 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}