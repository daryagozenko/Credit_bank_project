package app.gozenko.config;

import app.gozenko.dto.EmailMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class EmailMessageSerializer implements Serializer<EmailMessageDto> {

    private final ObjectMapper objectMapper;

    public EmailMessageSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(String topic, EmailMessageDto data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сериализации сообщения из топика: " + topic);
        }
    }
}
