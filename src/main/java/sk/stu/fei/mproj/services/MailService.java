package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import sk.stu.fei.mproj.configuration.ApplicationProperties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
@Transactional
public class MailService {
    private final ApplicationProperties applicationProperties;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public MailService(ApplicationProperties applicationProperties, JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.applicationProperties = applicationProperties;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendPlainTextEmail(String addressee, String subject, String plainText) throws MessagingException {
        if ( applicationProperties.getEnableMailSending() ) {
            sendEmail(addressee, subject, plainText, false);
        }
    }

    public void sendHtmlEmail(String addresse, String subject, String templateFile, Map<String, String> model) throws MessagingException {
        if ( applicationProperties.getEnableMailSending() ) {
            Context context = new Context();
            context.setVariables(model);
            String body = templateEngine.process(templateFile, context);
            sendEmail(addresse, subject, body, true);
        }
    }

    private void sendEmail(String addressee, String subject, String body, boolean html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(addressee);
        helper.setSubject(subject);
        helper.setText(body, html);
        mailSender.send(message);
    }
}
