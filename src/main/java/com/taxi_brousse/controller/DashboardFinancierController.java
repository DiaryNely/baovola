package com.taxi_brousse.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.taxi_brousse.dto.RentabiliteTrajetDTO;
import com.taxi_brousse.dto.RevenuMensuelDTO;
import com.taxi_brousse.dto.StatistiquesFinancieresDTO;
import com.taxi_brousse.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardFinancierController {

    private final DashboardService dashboardService;

    @GetMapping("/financier")
    public String afficherDashboard(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            Model model) {

        // Par défaut: 6 derniers mois
        if (dateDebut == null) {
            dateDebut = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }

        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin.atTime(23, 59, 59);

        StatistiquesFinancieresDTO stats = dashboardService.getStatistiquesFinancieres(dateDebutTime, dateFinTime);

        model.addAttribute("stats", stats);
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);

        return "taxi_brousse/dashboard-financier";
    }

    @GetMapping("/financier/export/excel")
    public ResponseEntity<InputStreamResource> exporterExcel(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin) throws IOException {

        if (dateDebut == null) {
            dateDebut = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }

        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin.atTime(23, 59, 59);

        StatistiquesFinancieresDTO stats = dashboardService.getStatistiquesFinancieres(dateDebutTime, dateFinTime);

        ByteArrayInputStream in = generateExcel(stats, dateDebut, dateFin);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=rapport-financier-" + dateDebut + "-" + dateFin + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/financier/export/pdf")
    public ResponseEntity<InputStreamResource> exporterPDF(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin) throws IOException {

        if (dateDebut == null) {
            dateDebut = LocalDate.now().minusMonths(6).withDayOfMonth(1);
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }

        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin.atTime(23, 59, 59);

        StatistiquesFinancieresDTO stats = dashboardService.getStatistiquesFinancieres(dateDebutTime, dateFinTime);

        ByteArrayInputStream in = generatePDF(stats, dateDebut, dateFin);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=rapport-financier-" + dateDebut + "-" + dateFin + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }

    private ByteArrayInputStream generateExcel(StatistiquesFinancieresDTO stats, LocalDate dateDebut, LocalDate dateFin) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Rapport Financier");

            // Style pour titres
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 14);
            headerStyle.setFont(font);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style pour valeurs monétaires
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            int rowIdx = 0;

            // En-tête
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("RAPPORT FINANCIER TAXI-BROUSSE");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            Row periodRow = sheet.createRow(rowIdx++);
            periodRow.createCell(0).setCellValue("Période: " + dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                    " - " + dateFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            rowIdx++; // Ligne vide

            // Statistiques globales
            Row globalHeader = sheet.createRow(rowIdx++);
            globalHeader.createCell(0).setCellValue("STATISTIQUES GLOBALES");
            globalHeader.getCell(0).setCellStyle(headerStyle);

            addRow(sheet, rowIdx++, "Revenus Total", stats.getRevenusTotal(), moneyStyle);
            addRow(sheet, rowIdx++, "Revenus Réservations", stats.getRevenusReservations(), moneyStyle);
            addRow(sheet, rowIdx++, "Revenus Publicités", stats.getRevenusPublicites(), moneyStyle);
            addRow(sheet, rowIdx++, "Nombre de Départs", BigDecimal.valueOf(stats.getNombreDeparts()), null);
            addRow(sheet, rowIdx++, "Nombre de Réservations", BigDecimal.valueOf(stats.getNombreReservations()), null);
            addRow(sheet, rowIdx++, "Taux Remplissage Moyen (%)", BigDecimal.valueOf(stats.getTauxRemplissageMoyen()), null);

            rowIdx++; // Ligne vide

            // Répartition par statut
            Row statutHeader = sheet.createRow(rowIdx++);
            statutHeader.createCell(0).setCellValue("RÉPARTITION PAR STATUT");
            statutHeader.getCell(0).setCellStyle(headerStyle);

            addRow(sheet, rowIdx++, "Départs Programmés", BigDecimal.valueOf(stats.getDeprogrammes()), null);
            addRow(sheet, rowIdx++, "Départs En Cours", BigDecimal.valueOf(stats.getDepartsEnCours()), null);
            addRow(sheet, rowIdx++, "Départs Terminés", BigDecimal.valueOf(stats.getDepartsTermines()), null);
            addRow(sheet, rowIdx++, "Départs Annulés", BigDecimal.valueOf(stats.getDepartsAnnules()), null);

            rowIdx++; // Ligne vide

            // Top 5 trajets
            Row top5Header = sheet.createRow(rowIdx++);
            top5Header.createCell(0).setCellValue("TOP 5 TRAJETS LES PLUS RENTABLES");
            top5Header.getCell(0).setCellStyle(headerStyle);

            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.createCell(0).setCellValue("Itinéraire");
            headerRow.createCell(1).setCellValue("Départs");
            headerRow.createCell(2).setCellValue("Réservations");
            headerRow.createCell(3).setCellValue("Revenus Total");
            headerRow.createCell(4).setCellValue("Rev. Réservations");
            headerRow.createCell(5).setCellValue("Rev. Publicités");

            for (RentabiliteTrajetDTO trajet : stats.getTop5Trajets()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(trajet.getItineraire());
                row.createCell(1).setCellValue(trajet.getNombreDeparts());
                row.createCell(2).setCellValue(trajet.getNombreReservations());
                Cell c3 = row.createCell(3);
                c3.setCellValue(trajet.getRevenusTotal().doubleValue());
                c3.setCellStyle(moneyStyle);
                Cell c4 = row.createCell(4);
                c4.setCellValue(trajet.getRevenusReservations().doubleValue());
                c4.setCellStyle(moneyStyle);
                Cell c5 = row.createCell(5);
                c5.setCellValue(trajet.getRevenusPublicites().doubleValue());
                c5.setCellStyle(moneyStyle);
            }

            // Auto-size colonnes
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void addRow(Sheet sheet, int rowIdx, String label, BigDecimal value, CellStyle style) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value.doubleValue());
        if (style != null) {
            valueCell.setCellStyle(style);
        }
    }

    private ByteArrayInputStream generatePDF(StatistiquesFinancieresDTO stats, LocalDate dateDebut, LocalDate dateFin) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Titre
        Paragraph title = new Paragraph("RAPPORT FINANCIER TAXI-BROUSSE")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        Paragraph period = new Paragraph("Période: " + dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                " - " + dateFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(period);

        // Statistiques globales
        document.add(new Paragraph("STATISTIQUES GLOBALES").setBold().setFontSize(14));
        Table globalTable = new Table(2);
        globalTable.addCell("Revenus Total");
        globalTable.addCell(formatMoney(stats.getRevenusTotal()));
        globalTable.addCell("Revenus Réservations");
        globalTable.addCell(formatMoney(stats.getRevenusReservations()));
        globalTable.addCell("Revenus Publicités");
        globalTable.addCell(formatMoney(stats.getRevenusPublicites()));
        globalTable.addCell("Nombre de Départs");
        globalTable.addCell(String.valueOf(stats.getNombreDeparts()));
        globalTable.addCell("Nombre de Réservations");
        globalTable.addCell(String.valueOf(stats.getNombreReservations()));
        globalTable.addCell("Taux Remplissage Moyen");
        globalTable.addCell(String.format("%.2f%%", stats.getTauxRemplissageMoyen()));
        document.add(globalTable);

        // Répartition par statut
        document.add(new Paragraph("\nRÉPARTITION PAR STATUT").setBold().setFontSize(14));
        Table statutTable = new Table(2);
        statutTable.addCell("Départs Programmés");
        statutTable.addCell(String.valueOf(stats.getDeprogrammes()));
        statutTable.addCell("Départs En Cours");
        statutTable.addCell(String.valueOf(stats.getDepartsEnCours()));
        statutTable.addCell("Départs Terminés");
        statutTable.addCell(String.valueOf(stats.getDepartsTermines()));
        statutTable.addCell("Départs Annulés");
        statutTable.addCell(String.valueOf(stats.getDepartsAnnules()));
        document.add(statutTable);

        // Top 5 trajets
        document.add(new Paragraph("\nTOP 5 TRAJETS LES PLUS RENTABLES").setBold().setFontSize(14));
        Table top5Table = new Table(4);
        top5Table.addCell("Itinéraire");
        top5Table.addCell("Départs");
        top5Table.addCell("Réservations");
        top5Table.addCell("Revenus Total");

        for (RentabiliteTrajetDTO trajet : stats.getTop5Trajets()) {
            top5Table.addCell(trajet.getItineraire());
            top5Table.addCell(String.valueOf(trajet.getNombreDeparts()));
            top5Table.addCell(String.valueOf(trajet.getNombreReservations()));
            top5Table.addCell(formatMoney(trajet.getRevenusTotal()));
        }
        document.add(top5Table);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String formatMoney(BigDecimal value) {
        return String.format("%,.2f Ar", value);
    }
}
