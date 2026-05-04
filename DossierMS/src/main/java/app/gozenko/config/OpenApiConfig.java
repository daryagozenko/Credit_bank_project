package app.gozenko.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "DossierMS",
                description = "Микросервис по отправке почтовых сообщений"
        )
)
public class OpenApiConfig {
}
