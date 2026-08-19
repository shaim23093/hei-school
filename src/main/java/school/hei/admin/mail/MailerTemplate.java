package school.hei.admin.mail;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import school.hei.admin.PojaGenerated;

@PojaGenerated
@Component
@AllArgsConstructor
public class MailerTemplate {
  private final TemplateEngine templateEngine;
  private final Mailer mailer;

  public void sendEmail(
      jakarta.mail.internet.InternetAddress to,
      String subject,
      String templateName,
      Object context) {
    var ctx = new Context(Locale.FRENCH, Map.of("model", context));
    String htmlBody = templateEngine.process(templateName, ctx);
    mailer.accept(new Email(to, List.of(), List.of(), subject, htmlBody, List.of()));
  }

  public void sendEmailWithAttachments(
      jakarta.mail.internet.InternetAddress to,
      String subject,
      String templateName,
      Object context,
      List<java.io.File> attachments) {
    var ctx = new Context(Locale.FRENCH, Map.of("model", context));
    String htmlBody = templateEngine.process(templateName, ctx);
    mailer.accept(new Email(to, List.of(), List.of(), subject, htmlBody, attachments));
  }
}
