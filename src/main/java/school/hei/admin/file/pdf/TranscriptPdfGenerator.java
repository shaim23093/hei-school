package school.hei.admin.file.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.hei.admin.PojaGenerated;
import school.hei.admin.dto.response.CourseGradeResult;
import school.hei.admin.dto.response.TranscriptResponse;

@PojaGenerated
@Component
public class TranscriptPdfGenerator {

  @SneakyThrows
  public File generate(TranscriptResponse transcript) {
    File tempFile = File.createTempFile("transcript-", ".pdf");
    Document document = new Document(PageSize.A4, 50, 50, 50, 50);
    PdfWriter.getInstance(document, new FileOutputStream(tempFile));
    document.open();

    Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

    Paragraph title = new Paragraph("Relevé de Notes", titleFont);
    title.setAlignment(Element.ALIGN_CENTER);
    document.add(title);

    Paragraph promotion = new Paragraph(transcript.promotionName(), subtitleFont);
    promotion.setAlignment(Element.ALIGN_CENTER);
    document.add(promotion);
    document.add(new Paragraph(" "));

    Table infoTable = new Table(2);
    infoTable.setWidth(100);
    addInfoRow(infoTable, "Nom", transcript.firstName() + " " + transcript.name(), bodyFont);
    addInfoRow(infoTable, "Matricule", transcript.std(), bodyFont);
    addInfoRow(
        infoTable,
        "Semestre",
        transcript.semester() != null ? String.valueOf(transcript.semester()) : "Année complète",
        bodyFont);
    addInfoRow(
        infoTable,
        "Moyenne",
        transcript.average() != null ? String.format("%.2f / 20", transcript.average()) : "N/A",
        bodyFont);
    addInfoRow(
        infoTable,
        "Crédits validés",
        transcript.validatedCredits() + " / " + transcript.totalCredits(),
        bodyFont);
    addInfoRow(infoTable, "Statut", transcript.status(), bodyFont);
    document.add(infoTable);
    document.add(new Paragraph(" "));

    if (transcript.results() != null && !transcript.results().isEmpty()) {
      Paragraph detailTitle = new Paragraph("Détail par matière", headerFont);
      document.add(detailTitle);

      Table gradeTable = new Table(6);
      gradeTable.setWidth(100);
      gradeTable.setWidths(new int[] {15, 30, 10, 10, 15, 10});

      addHeaderRow(
          gradeTable,
          new String[] {"Code", "Matière", "Crédits", "Sem.", "Moyenne", "Validé"},
          headerFont);

      for (CourseGradeResult r : transcript.results()) {
        gradeTable.addCell(createCell(r.code(), bodyFont));
        gradeTable.addCell(createCell(r.name(), bodyFont));
        gradeTable.addCell(createCell(String.valueOf(r.credits()), bodyFont));
        gradeTable.addCell(createCell(String.valueOf(r.semester()), bodyFont));
        gradeTable.addCell(
            createCell(r.average() != null ? String.format("%.2f", r.average()) : "N/A", bodyFont));
        gradeTable.addCell(createCell(r.validated() ? "Oui" : "Non", bodyFont));
      }
      document.add(gradeTable);
    }

    HeaderFooter footer =
        new HeaderFooter(new Phrase("Généré automatiquement par HEI Admin"), true);
    footer.setAlignment(Element.ALIGN_CENTER);
    document.setFooter(footer);

    document.close();
    return tempFile;
  }

  private void addInfoRow(Table table, String label, String value, Font font)
      throws DocumentException {
    table.addCell(createCell(label, font));
    table.addCell(createCell(value, font));
  }

  private void addHeaderRow(Table table, String[] headers, Font font) throws DocumentException {
    Font whiteFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, java.awt.Color.WHITE);
    for (String h : headers) {
      com.lowagie.text.Cell cell = new com.lowagie.text.Cell(new Phrase(h, whiteFont));
      cell.setBackgroundColor(new java.awt.Color(44, 62, 80));
      table.addCell(cell);
    }
  }

  private com.lowagie.text.Cell createCell(String content, Font font) {
    return new com.lowagie.text.Cell(new Phrase(content != null ? content : "", font));
  }
}
