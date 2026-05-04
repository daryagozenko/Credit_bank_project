package app.gozenko.controller.interfaces;

import app.gozenko.dto.EmailMessageDto;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface DossierKafkaConsumer {
    void sendingSuccessEmail(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             EmailMessageDto dto);

    void sendingDeniedEmail(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            EmailMessageDto dto);
}
