package app.gozenko.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDto {
    private String address;
    //TODO: сделать EmailTheme enum (тема письма при отправке)
    private Long statementId;
    private String text;
}