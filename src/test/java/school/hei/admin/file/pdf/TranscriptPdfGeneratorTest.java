package school.hei.admin.file.pdf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.admin.dto.response.CourseGradeResult;
import school.hei.admin.dto.response.TranscriptResponse;

class TranscriptPdfGeneratorTest {

  private final TranscriptPdfGenerator generator = new TranscriptPdfGenerator();

  @Test
  void generate_creates_valid_pdf_file() {
    var transcript =
        TranscriptResponse.builder()
            .studentId(java.util.UUID.randomUUID())
            .std("STD24001")
            .name("Rakoto")
            .firstName("Jean")
            .promotionName("Promo 2024")
            .semester(1)
            .average(14.5)
            .validatedCredits(12)
            .totalCredits(18)
            .status("PROVISOIRE")
            .results(
                List.of(
                    CourseGradeResult.builder()
                        .courseId(java.util.UUID.randomUUID())
                        .code("MATH101")
                        .name("Mathématiques")
                        .credits(6)
                        .semester(1)
                        .academicYear(1)
                        .average(15.0)
                        .complete(true)
                        .validated(true)
                        .build()))
            .build();

    File pdf = generator.generate(transcript);

    assertNotNull(pdf);
    assertTrue(pdf.exists());
    assertTrue(pdf.length() > 0);
    assertTrue(pdf.getName().endsWith(".pdf"));
    pdf.delete();
  }

  @Test
  void generate_handles_null_average_and_results() {
    var transcript =
        TranscriptResponse.builder()
            .studentId(java.util.UUID.randomUUID())
            .std("STD24002")
            .name("Rasoa")
            .firstName("Marie")
            .promotionName("Promo 2024")
            .semester(null)
            .average(null)
            .validatedCredits(0)
            .totalCredits(18)
            .status("NON_EVALUE")
            .results(List.of())
            .build();

    File pdf = generator.generate(transcript);

    assertNotNull(pdf);
    assertTrue(pdf.exists());
    assertTrue(pdf.length() > 0);
    pdf.delete();
  }
}
