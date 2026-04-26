package app.gozenko.config;

import app.gozenko.dto.EmailMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class EmailMessageDeserializer implements Deserializer<EmailMessageDto> {

    private final ObjectMapper objectMapper;

    public EmailMessageDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public EmailMessageDto deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, EmailMessageDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при десериализации сообщения из топика: " + topic);
        }
    }
}
