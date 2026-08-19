package school.hei.admin.file.excel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import school.hei.admin.dto.response.GraduatedStudentResponse;
import school.hei.admin.entity.enums.Path;

class GraduationExcelExporterTest {

  private final GraduationExcelExporter exporter = new GraduationExcelExporter();

  @Test
  void export_creates_valid_excel_bytes() {
    var graduates =
        List.of(
            GraduatedStudentResponse.builder()
                .studentId(UUID.randomUUID())
                .std("STD24001")
                .name("Rakoto")
                .firstName("Jean")
                .path(Path.EL)
                .average(15.5)
                .rank(1)
                .build(),
            GraduatedStudentResponse.builder()
                .studentId(UUID.randomUUID())
                .std("STD24002")
                .name("Rasoa")
                .firstName("Marie")
                .path(Path.TN)
                .average(14.0)
                .rank(2)
                .build());

    byte[] excel = exporter.export("Promo 2024", graduates);

    assertNotNull(excel);
    assertTrue(excel.length > 0);
    assertEquals(0x50, excel[0] & 0xFF); // PK header byte 1
  }

  @Test
  void export_handles_empty_graduates() {
    byte[] excel = exporter.export("Promo 2024", List.of());

    assertNotNull(excel);
    assertTrue(excel.length > 0);
  }

  @Test
  void export_handles_null_path() {
    var graduates =
        List.of(
            GraduatedStudentResponse.builder()
                .studentId(UUID.randomUUID())
                .std("STD24003")
                .name("Test")
                .firstName("User")
                .path(null)
                .average(12.0)
                .rank(1)
                .build());

    byte[] excel = exporter.export("Promo 2024", graduates);

    assertNotNull(excel);
    assertTrue(excel.length > 0);
  }
}
