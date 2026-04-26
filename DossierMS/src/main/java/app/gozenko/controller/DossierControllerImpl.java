package app.gozenko.controller;

import app.gozenko.dto.EmailMessageDto;
import app.gozenko.service.DossierServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DossierControllerImpl {

    private final DossierServiceImpl dossierService;

    @KafkaListener(topics = "finish-registration", groupId = "dossier-consumer")
    public void finishRegistration(EmailMessageDto dto) {
        log.info(dto.getText());
        dossierService.sendEmail(dto);
        log.info("Sending successfully");
    }
}
