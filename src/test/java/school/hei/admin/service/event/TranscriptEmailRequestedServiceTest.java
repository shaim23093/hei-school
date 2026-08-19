package school.hei.admin.service.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.hei.admin.dto.response.TranscriptResponse;
import school.hei.admin.endpoint.event.model.TranscriptEmailRequested;
import school.hei.admin.file.bucket.BucketComponent;
import school.hei.admin.file.pdf.TranscriptPdfGenerator;
import school.hei.admin.mail.MailerTemplate;
import school.hei.admin.service.GradeService;

@ExtendWith(MockitoExtension.class)
class TranscriptEmailRequestedServiceTest {

  @Mock private GradeService gradeService;
  @Mock private TranscriptPdfGenerator pdfGenerator;
  @Mock private BucketComponent bucketComponent;
  @Mock private MailerTemplate mailerTemplate;
  @InjectMocks private TranscriptEmailRequestedService service;

  @Test
  void accept_generates_pdf_uploads_and_sends_email() throws Exception {
    UUID studentId = UUID.randomUUID();
    var transcript =
        TranscriptResponse.builder()
            .studentId(studentId)
            .std("STD24001")
            .name("Rakoto")
            .firstName("Jean")
            .promotionName("Promo 2024")
            .semester(1)
            .average(14.5)
            .validatedCredits(12)
            .totalCredits(18)
            .status("PROVISOIRE")
            .results(java.util.List.of())
            .build();

    File mockPdf = File.createTempFile("test-transcript", ".pdf");
    mockPdf.deleteOnExit();

    when(gradeService.getTranscriptInternal(studentId, 1)).thenReturn(transcript);
    when(pdfGenerator.generate(transcript)).thenReturn(mockPdf);
    when(bucketComponent.presign(anyString(), any()))
        .thenReturn(new java.net.URL("https://s3.example.com/presigned"));

    var event =
        TranscriptEmailRequested.builder()
            .recipientEmail("student@hei.school")
            .studentId(studentId)
            .semester(1)
            .build();

    service.accept(event);

    verify(gradeService).getTranscriptInternal(studentId, 1);
    verify(pdfGenerator).generate(transcript);
    verify(bucketComponent).upload(any(File.class), contains("transcripts/" + studentId));
    verify(bucketComponent).presign(contains("transcripts/" + studentId), any());
    verify(mailerTemplate)
        .sendEmail(
            argThat(addr -> addr.toString().equals("student@hei.school")),
            contains("Relevé de Notes"),
            eq("mail/transcript"),
            argThat(m -> m instanceof java.util.Map && ((java.util.Map<?, ?>) m).containsKey("model")));
  }

  @Test
  void accept_handles_null_semester() throws Exception {
    UUID studentId = UUID.randomUUID();
    var transcript =
        TranscriptResponse.builder()
            .studentId(studentId)
            .std("STD24002")
            .name("Rasoa")
            .firstName("Marie")
            .promotionName("Promo 2024")
            .semester(null)
            .average(null)
            .validatedCredits(0)
            .totalCredits(18)
            .status("NON_EVALUE")
            .results(java.util.List.of())
            .build();

    File mockPdf = File.createTempFile("test-transcript", ".pdf");
    mockPdf.deleteOnExit();

    when(gradeService.getTranscriptInternal(studentId, null)).thenReturn(transcript);
    when(pdfGenerator.generate(transcript)).thenReturn(mockPdf);
    when(bucketComponent.presign(anyString(), any()))
        .thenReturn(new java.net.URL("https://s3.example.com/presigned"));

    var event =
        TranscriptEmailRequested.builder()
            .recipientEmail("student@hei.school")
            .studentId(studentId)
            .semester(null)
            .build();

    service.accept(event);

    verify(gradeService).getTranscriptInternal(studentId, null);
    verify(mailerTemplate).sendEmail(any(), anyString(), eq("mail/transcript"), any(java.util.Map.class));
  }
}
