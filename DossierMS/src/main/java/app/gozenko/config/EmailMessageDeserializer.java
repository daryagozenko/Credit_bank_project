package app.gozenko.config;

import app.gozenko.dto.EmailMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

@RequiredArgsConstructor
public class EmailMessageDeserializer implements Deserializer<EmailMessageDto> {

    private final ObjectMapper objectMapper;

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
