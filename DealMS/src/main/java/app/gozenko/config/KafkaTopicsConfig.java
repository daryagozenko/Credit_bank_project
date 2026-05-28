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
    private String finish_registration;
    @Value("${kafka.topic.create-documents}")
    private String create_document;
    @Value("${kafka.topic.send-documents}")
    private String send_document;
    @Value("${kafka.topic.send-ses}")
    private String send_ses;
    @Value("${kafka.topic.credit-issued}")
    private String credit_issued;
    @Value("${kafka.topic.statement-denied}")
    private String statement_denied;

    @Bean
    public List<NewTopic> topics() {
        return List.of(
                createTopic(finish_registration),
                createTopic(create_document),
                createTopic(send_document),
                createTopic(send_ses),
                createTopic(credit_issued),
                createTopic(statement_denied)
        );
    }

    public NewTopic createTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
