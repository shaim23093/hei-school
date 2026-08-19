package school.hei.admin.service.event;

import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import school.hei.admin.PojaGenerated;
import school.hei.admin.dto.response.TranscriptResponse;
import school.hei.admin.endpoint.event.model.TranscriptEmailRequested;
import school.hei.admin.file.bucket.BucketComponent;
import school.hei.admin.file.pdf.TranscriptPdfGenerator;
import school.hei.admin.mail.MailerTemplate;
import school.hei.admin.service.GradeService;

@PojaGenerated
@Service
@AllArgsConstructor
public class TranscriptEmailRequestedService implements Consumer<TranscriptEmailRequested> {
  private final GradeService gradeService;
  private final TranscriptPdfGenerator pdfGenerator;
  private final BucketComponent bucketComponent;
  private final MailerTemplate mailerTemplate;

  @SneakyThrows
  @Override
  public void accept(TranscriptEmailRequested event) {
    UUID studentId = event.getStudentId();
    Integer semester = event.getSemester();

    TranscriptResponse transcript =
        gradeService.getTranscriptInternal(studentId, semester);
    File pdfFile = pdfGenerator.generate(transcript);

    String bucketKey = "transcripts/" + studentId + "/" + pdfFile.getName();
    bucketComponent.upload(pdfFile, bucketKey);

    var presignedUrl = bucketComponent.presign(bucketKey, java.time.Duration.ofHours(1));

    String emailSubject = "Relevé de Notes - " + transcript.promotionName();
    mailerTemplate.sendEmail(
        new InternetAddress(event.getRecipientEmail()),
        emailSubject,
        "mail/transcript",
        java.util.Map.of("model", transcript, "pdfUrl", presignedUrl.toString()));

    pdfFile.delete();
  }
}
