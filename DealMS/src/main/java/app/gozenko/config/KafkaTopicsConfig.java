package app.gozenko.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic finishRegistration(){
        return TopicBuilder.name("finish-registration")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic createDocuments(){
        return TopicBuilder.name("create-documents")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sendDocuments(){
        return TopicBuilder.name("send-documents")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic sendSes(){
        return TopicBuilder.name("send-ses")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic creditIssued(){
        return TopicBuilder.name("credit-issued")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic statementDenied(){
        return TopicBuilder.name("statement-denied")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
