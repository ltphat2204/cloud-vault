package ltphat.cloudvault.backend.mail.domain.service;

import ltphat.cloudvault.backend.mail.domain.model.MailRequest;

public interface MailService {
    void sendMail(MailRequest request);
}
