package app.gozenko.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.List;

@Configuration
public class KafkaTopicsConfig {

    @Value("${kafka.topic.finish-registration}")
    private String FINISH_REGISTRATION;
    @Value("${kafka.topic.create-documents}")
    private String CREATE_DOCUMENT;
    @Value("${kafka.topic.send-documents}")
    private String SEND_DOCUMENT;
    @Value("${kafka.topic.send-ses}")
    private String SEND_SES;
    @Value("${kafka.topic.credit-issued}")
    private String CREDIT_ISSUED;
    @Value("${kafka.topic.statement-denied}")
    private String STATEMENT_DENIED;

    @Bean
    public List<NewTopic> topics() {
        return List.of(
                createTopic(FINISH_REGISTRATION),
                createTopic(CREATE_DOCUMENT),
                createTopic(SEND_DOCUMENT),
                createTopic(SEND_SES),
                createTopic(CREDIT_ISSUED),
                createTopic(STATEMENT_DENIED)
        );
    }

    public NewTopic createTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
