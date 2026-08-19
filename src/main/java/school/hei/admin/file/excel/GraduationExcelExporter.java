package school.hei.admin.file.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import school.hei.admin.PojaGenerated;
import school.hei.admin.dto.response.GraduatedStudentResponse;

@PojaGenerated
@Component
public class GraduationExcelExporter {

  @SneakyThrows
  public byte[] export(String promotionName, java.util.List<GraduatedStudentResponse> graduates) {
    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Diplômés");

      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
      headerFont.setColor(IndexedColors.WHITE.getIndex());
      headerFont.setBold(true);
      headerStyle.setFont(headerFont);

      Row headerRow = sheet.createRow(0);
      String[] headers = {"Rang", "Matricule", "Nom", "Prénom", "Parcours", "Moyenne"};
      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(headerStyle);
      }

      int rowNum = 1;
      for (GraduatedStudentResponse g : graduates) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(g.rank());
        row.createCell(1).setCellValue(g.std());
        row.createCell(2).setCellValue(g.name());
        row.createCell(3).setCellValue(g.firstName());
        row.createCell(4).setCellValue(g.path() != null ? g.path().toString() : "");
        row.createCell(5).setCellValue(String.format("%.2f", g.average()));
      }

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      workbook.write(bos);
      return bos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to generate Excel", e);
    }
  }
}
