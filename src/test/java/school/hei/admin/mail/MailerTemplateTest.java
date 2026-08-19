package school.hei.admin.mail;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import jakarta.mail.internet.InternetAddress;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@ExtendWith(MockitoExtension.class)
class MailerTemplateTest {

  @Mock private TemplateEngine templateEngine;
  @Mock private Mailer mailer;
  @InjectMocks private MailerTemplate mailerTemplate;

  @Test
  void sendEmail_renders_template_and_sends() throws Exception {
    when(templateEngine.process(eq("mail/transcript"), any(Context.class)))
        .thenReturn("<html><body>Test email</body></html>");

    var to = new InternetAddress("student@hei.school");
    var context = new Object();

    mailerTemplate.sendEmail(to, "Test Subject", "mail/transcript", context);

    ArgumentCaptor<Email> captor = ArgumentCaptor.forClass(Email.class);
    verify(mailer).accept(captor.capture());

    Email sent = captor.getValue();
    assertEquals(to, sent.to());
    assertEquals("Test Subject", sent.subject());
    assertEquals("<html><body>Test email</body></html>", sent.htmlBody());
    assertTrue(sent.attachments().isEmpty());
  }

  @Test
  void sendEmailWithAttachments_sends_with_files() throws Exception {
    when(templateEngine.process(eq("mail/transcript"), any(Context.class)))
        .thenReturn("<html><body>Test</body></html>");

    var to = new InternetAddress("student@hei.school");
    var file = java.io.File.createTempFile("test", ".pdf");
    file.deleteOnExit();

    mailerTemplate.sendEmailWithAttachments(
        to, "Subject", "mail/transcript", new Object(), List.of(file));

    ArgumentCaptor<Email> captor = ArgumentCaptor.forClass(Email.class);
    verify(mailer).accept(captor.capture());

    assertEquals(1, captor.getValue().attachments().size());
    file.delete();
  }
}
