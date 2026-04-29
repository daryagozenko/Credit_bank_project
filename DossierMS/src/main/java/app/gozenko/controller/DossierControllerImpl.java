package app.gozenko.controller;

import app.gozenko.controller.interfaces.DossierController;
import app.gozenko.dto.EmailMessageDto;
import app.gozenko.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DossierControllerImpl implements DossierController {

    private final EmailService dossierService;

    @KafkaListener(topics = {"finish-registration", "create-documents",
            "send-documents", "send-ses", "credit-issued"},
            groupId = "dossier-consumer")
    @Override
    public void sendingEmailWithSuccess(EmailMessageDto dto) {
        log.info("Message in finishRegistrationAndDocuments - {}", dto.getText());
        dossierService.sendEmail(dto);
        log.info("Sending in finishRegistrationAndDocuments successfully");
    }

    @KafkaListener(topics = "statement-denied", groupId = "dossier-consumer")
    @Override
    public void sendingEmailWithDenied(EmailMessageDto dto) {
        log.info("Message in createDocuments - {}", dto.getText());
        dossierService.sendEmail(dto);
        log.info("Sending in createDocuments successfully");
    }
}
