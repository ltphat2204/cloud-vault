package ltphat.cloudvault.backend.mail.infrastructure.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.mail.domain.model.MailRequest;
import ltphat.cloudvault.backend.mail.domain.repository.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JavaMailSenderAdapter implements MailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(MailRequest request) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(request.to());
            helper.setSubject(request.subject());
            helper.setText(request.body(), request.isHtml());
            
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error creating mime message", e);
        }
    }
}
