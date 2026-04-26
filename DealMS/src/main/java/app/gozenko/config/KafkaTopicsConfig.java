package app.gozenko.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

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
    public NewTopic finishRegistration(){
        return TopicBuilder.name(FINISH_REGISTRATION)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic createDocuments(){
        return TopicBuilder.name(CREATE_DOCUMENT)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sendDocuments(){
        return TopicBuilder.name(SEND_DOCUMENT)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sendSes(){
        return TopicBuilder.name(SEND_SES)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic creditIssued(){
        return TopicBuilder.name(CREDIT_ISSUED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic statementDenied(){
        return TopicBuilder.name(STATEMENT_DENIED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
