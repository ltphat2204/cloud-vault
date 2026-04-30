package ltphat.cloudvault.backend.mail.domain.model;

import lombok.Builder;

@Builder
public record MailRequest(
    String to,
    String subject,
    String body,
    boolean isHtml
) {
}
