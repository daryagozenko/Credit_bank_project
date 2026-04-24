package app.gozenko.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "StatementMS",
                description = "Микросервис прескоринга кредитной заявки"
        )
)
public class OpenApiConfig {
}
