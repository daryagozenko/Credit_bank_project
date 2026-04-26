package app.gozenko.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class DossierControllerImpl {

    @KafkaListener(topics = "finish-registration", groupId = "dossier-consumer")
    public void finishRegistration(String text){
        log.info(text);
    }
}
