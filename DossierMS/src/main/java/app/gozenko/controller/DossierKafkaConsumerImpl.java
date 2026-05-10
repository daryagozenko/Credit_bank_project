package app.gozenko.controller;

import app.gozenko.controller.interfaces.DossierKafkaConsumer;
import app.gozenko.dto.EmailMessageDto;
import app.gozenko.service.interfaces.DealCallingService;
import app.gozenko.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DossierKafkaConsumerImpl implements DossierKafkaConsumer {

    private final EmailService dossierService;
    private final DealCallingService dealCallingService;

    @KafkaListener(topics = {"finish-registration", "create-documents",
            "send-ses", "credit-issued"},
            groupId = "dossier-consumer")
    @Override
    public void sendingSuccessEmail(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    EmailMessageDto dto) {
        log.info("Topic in sendingSuccessEmail - {}", topic);
        log.info("Message in sendingSuccessEmail - {}", dto.getText());
        dossierService.sendEmail(dto);
        log.info("Sending in sendingSuccessEmail successfully");
    }

    @KafkaListener(topics = "statement-denied", groupId = "dossier-consumer")
    @Override
    public void sendingDeniedEmail(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   EmailMessageDto dto) {
        log.info("Topic in sendingDeniedEmail - {}", topic);
        log.info("Message in sendingDeniedEmail - {}", dto.getText());
        dossierService.sendEmail(dto);
        log.info("Sending in sendingDeniedEmail successfully");
    }

    @KafkaListener(topics = "send-documents", groupId = "dossier-consumer")
    @Override
    public void sendingSuccessEmailWithUpdateDocument(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                                      EmailMessageDto dto) {
        log.info("Topic in sendingSuccessEmailWithUpdateDocument - {}", topic);
        log.info("Message in sendingSuccessEmailWithUpdateDocument - {}", dto.getText());

        dealCallingService.putDocumentStatus(dto.getStatementId());
        log.debug("Put document status");

        dossierService.sendEmail(dto);
        log.info("Sending in sendingSuccessEmailWithUpdateDocument successfully");
    }
}
