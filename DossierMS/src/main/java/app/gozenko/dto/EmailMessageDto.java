package app.gozenko.dto;


import app.gozenko.enums.EmailTheme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDto {

    private String address;

    private EmailTheme theme;

    private Long statementId;

    private String text;
}