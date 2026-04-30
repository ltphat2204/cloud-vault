package ltphat.cloudvault.backend.mail.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltphat.cloudvault.backend.mail.domain.model.MailRequest;
import ltphat.cloudvault.backend.mail.domain.service.MailService;
import ltphat.cloudvault.backend.mail.infrastructure.config.MailRabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailConsumer {

    private final MailService mailService;

    @RabbitListener(queues = MailRabbitMQConfig.MAIL_QUEUE)
    public void consumeMailRequest(MailRequest request) {
        log.info("Received mail request from RabbitMQ for: {}", request.to());
        mailService.sendMail(request);
    }
}
