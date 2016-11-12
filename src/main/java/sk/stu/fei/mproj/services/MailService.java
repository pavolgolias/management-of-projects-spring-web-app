package sk.stu.fei.mproj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.stu.fei.mproj.configuration.ApplicationProperties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Transactional
public class MailService {
    private final ApplicationProperties applicationProperties;
    private final JavaMailSender mailSender;

    @Autowired
    public MailService(ApplicationProperties applicationProperties, JavaMailSender mailSender) {
        this.applicationProperties = applicationProperties;
        this.mailSender = mailSender;
    }

    public void sendPlainTextEmail(String addressee, String subject, String plainText) throws MessagingException {
        if ( applicationProperties.getEnableMailSending() ) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setTo(addressee);
            helper.setSubject(subject);
            helper.setText(plainText);
            mailSender.send(message);
        }
    }
}
