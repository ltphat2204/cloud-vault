package ltphat.cloudvault.backend.mail.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltphat.cloudvault.backend.mail.domain.model.MailRequest;
import ltphat.cloudvault.backend.mail.domain.repository.MailSender;
import ltphat.cloudvault.backend.mail.domain.service.MailService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailSender mailSender;

    @Override
    public void sendMail(MailRequest request) {
        log.info("Processing mail request to: {}", request.to());
        try {
            mailSender.send(request);
            log.info("Mail sent successfully to: {}", request.to());
        } catch (Exception e) {
            log.error("Failed to send mail to: {}", request.to(), e);
            // In a production system, we might want to retry or send to a DLQ
            throw e;
        }
    }
}
