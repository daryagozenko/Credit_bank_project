package app.gozenko.dto;


import app.gozenko.enums.EmailTheme;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDto {

    @Schema(description = "Адрес", example = "mail@gmail.com")
    private String address;

    @Schema(description = "Тема письма", example = "CREDIT_ISSUED")
    private EmailTheme theme;

    @Schema(description = "ID заявления", example = "123e4567-e89b-12d3-a456-426614174000")
    private Long statementId;

    @Schema(description = "Текст письма", example = "Кредит одобрен")
    private String text;
}