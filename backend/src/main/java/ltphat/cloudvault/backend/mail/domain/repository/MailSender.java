package ltphat.cloudvault.backend.mail.domain.repository;

import ltphat.cloudvault.backend.mail.domain.model.MailRequest;

public interface MailSender {
    void send(MailRequest request);
}
