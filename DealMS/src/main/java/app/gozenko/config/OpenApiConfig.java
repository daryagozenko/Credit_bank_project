package app.gozenko.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "DealMS",
                description = "Микросервис по оформлению кредитной заявки"
        )
)
public class OpenApiConfig {
}
