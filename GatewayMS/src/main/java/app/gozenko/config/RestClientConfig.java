package app.gozenko.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient dealClientBean(@Value("${app.gozenko.deal-url}") String baseUrl){
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient statementClientBean(@Value("${app.gozenko.statement-url}") String baseUrl){
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
