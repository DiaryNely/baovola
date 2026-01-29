package com.taxi_brousse.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.taxi_brousse.dto.ChiffreAffairesStatsDTO;
import com.taxi_brousse.service.ChiffreAffairesStatsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardFinancierController {

    private final ChiffreAffairesStatsService chiffreAffairesStatsService;

    @GetMapping("/financier")
    public String afficherDashboard(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            Model model) {

        // Par défaut: mois actuel
        if (dateDebut == null) {
            dateDebut = LocalDate.now().withDayOfMonth(1);
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }

        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin.atTime(23, 59, 59);

        ChiffreAffairesStatsDTO stats = chiffreAffairesStatsService.getStatistiquesPeriode(dateDebutTime, dateFinTime);

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
            dateDebut = LocalDate.now().withDayOfMonth(1);
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }

        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin.atTime(23, 59, 59);

        ChiffreAffairesStatsDTO stats = chiffreAffairesStatsService.getStatistiquesPeriode(dateDebutTime, dateFinTime);

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
            dateDebut = LocalDate.now().withDayOfMonth(1);
        }
        if (dateFin == null) {
            dateFin = LocalDate.now();
        }

        LocalDateTime dateDebutTime = dateDebut.atStartOfDay();
        LocalDateTime dateFinTime = dateFin.atTime(23, 59, 59);

        ChiffreAffairesStatsDTO stats = chiffreAffairesStatsService.getStatistiquesPeriode(dateDebutTime, dateFinTime);

        ByteArrayInputStream in = generatePDF(stats, dateDebut, dateFin);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=rapport-financier-" + dateDebut + "-" + dateFin + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }

    private ByteArrayInputStream generateExcel(ChiffreAffairesStatsDTO stats, LocalDate dateDebut, LocalDate dateFin) throws IOException {
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
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

            Row periodRow = sheet.createRow(rowIdx++);
            periodRow.createCell(0).setCellValue("Période: " + dateDebut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                    " - " + dateFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            rowIdx++; // Ligne vide

            // Chiffre d'Affaires Global
            Row globalHeader = sheet.createRow(rowIdx++);
            globalHeader.createCell(0).setCellValue("CHIFFRE D'AFFAIRES GLOBAL");
            globalHeader.getCell(0).setCellStyle(headerStyle);

            // En-tête tableau
            Row tableHeader = sheet.createRow(rowIdx++);
            tableHeader.createCell(0).setCellValue("Catégorie");
            tableHeader.createCell(1).setCellValue("CA Théorique");
            tableHeader.createCell(2).setCellValue("CA Réel");
            tableHeader.createCell(3).setCellValue("Écart");

            addRowWithEcart(sheet, rowIdx++, "Réservations", stats.getCaReservationsTheorique(), stats.getCaReservationsReel(), moneyStyle);
            addRowWithEcart(sheet, rowIdx++, "Diffusions Publicité", stats.getCaDiffusionsTheorique(), stats.getCaDiffusionsReel(), moneyStyle);
            addRowWithEcart(sheet, rowIdx++, "Ventes Produits", stats.getCaVentesProduitsTheorique(), stats.getCaVentesProduitsReel(), moneyStyle);
            addRowWithEcart(sheet, rowIdx++, "TOTAL", stats.getTotalTheorique(), stats.getTotalReel(), moneyStyle);

            rowIdx++; // Ligne vide

            // Nombre de départs
            Row departsHeader = sheet.createRow(rowIdx++);
            departsHeader.createCell(0).setCellValue("DÉTAILS");
            departsHeader.getCell(0).setCellStyle(headerStyle);

            addRow(sheet, rowIdx++, "Nombre de Départs", BigDecimal.valueOf(stats.getDeparts() != null ? stats.getDeparts().size() : 0), null);

            // Auto-size colonnes
            for (int i = 0; i < 4; i++) {
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
        valueCell.setCellValue(value != null ? value.doubleValue() : 0);
        if (style != null) {
            valueCell.setCellStyle(style);
        }
    }

    private void addRowWithEcart(Sheet sheet, int rowIdx, String label, BigDecimal theorique, BigDecimal reel, CellStyle moneyStyle) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        
        Cell theoriqueCell = row.createCell(1);
        theoriqueCell.setCellValue(theorique != null ? theorique.doubleValue() : 0);
        theoriqueCell.setCellStyle(moneyStyle);
        
        Cell reelCell = row.createCell(2);
        reelCell.setCellValue(reel != null ? reel.doubleValue() : 0);
        reelCell.setCellStyle(moneyStyle);
        
        Cell ecartCell = row.createCell(3);
        BigDecimal ecart = (reel != null ? reel : BigDecimal.ZERO).subtract(theorique != null ? theorique : BigDecimal.ZERO);
        ecartCell.setCellValue(ecart.doubleValue());
        ecartCell.setCellStyle(moneyStyle);
    }

    private ByteArrayInputStream generatePDF(ChiffreAffairesStatsDTO stats, LocalDate dateDebut, LocalDate dateFin) throws IOException {
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

        // Chiffre d'Affaires Global
        document.add(new Paragraph("CHIFFRE D'AFFAIRES GLOBAL").setBold().setFontSize(14));
        Table globalTable = new Table(4);
        globalTable.addCell("Catégorie");
        globalTable.addCell("CA Théorique");
        globalTable.addCell("CA Réel");
        globalTable.addCell("Écart");

        // Réservations
        globalTable.addCell("Réservations");
        globalTable.addCell(formatMoney(stats.getCaReservationsTheorique()));
        globalTable.addCell(formatMoney(stats.getCaReservationsReel()));
        globalTable.addCell(formatMoney(calcEcart(stats.getCaReservationsTheorique(), stats.getCaReservationsReel())));

        // Diffusions Publicité
        globalTable.addCell("Diffusions Publicité");
        globalTable.addCell(formatMoney(stats.getCaDiffusionsTheorique()));
        globalTable.addCell(formatMoney(stats.getCaDiffusionsReel()));
        globalTable.addCell(formatMoney(calcEcart(stats.getCaDiffusionsTheorique(), stats.getCaDiffusionsReel())));

        // Ventes Produits
        globalTable.addCell("Ventes Produits");
        globalTable.addCell(formatMoney(stats.getCaVentesProduitsTheorique()));
        globalTable.addCell(formatMoney(stats.getCaVentesProduitsReel()));
        globalTable.addCell(formatMoney(calcEcart(stats.getCaVentesProduitsTheorique(), stats.getCaVentesProduitsReel())));

        // Total
        globalTable.addCell("TOTAL");
        globalTable.addCell(formatMoney(stats.getTotalTheorique()));
        globalTable.addCell(formatMoney(stats.getTotalReel()));
        globalTable.addCell(formatMoney(calcEcart(stats.getTotalTheorique(), stats.getTotalReel())));

        document.add(globalTable);

        // Détails
        document.add(new Paragraph("\nDÉTAILS").setBold().setFontSize(14));
        Table detailsTable = new Table(2);
        detailsTable.addCell("Nombre de Départs");
        detailsTable.addCell(String.valueOf(stats.getDeparts() != null ? stats.getDeparts().size() : 0));
        document.add(detailsTable);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private BigDecimal calcEcart(BigDecimal theorique, BigDecimal reel) {
        BigDecimal t = theorique != null ? theorique : BigDecimal.ZERO;
        BigDecimal r = reel != null ? reel : BigDecimal.ZERO;
        return r.subtract(t);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0,00 Ar";
        return String.format("%,.2f Ar", value);
    }
}
