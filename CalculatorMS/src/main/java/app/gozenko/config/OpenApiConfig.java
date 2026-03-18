package app.gozenko.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "CalculatorMS",
                description = "Микросервис по формированию кредитных предложений"
        )
)
public class OpenApiConfig {
}
