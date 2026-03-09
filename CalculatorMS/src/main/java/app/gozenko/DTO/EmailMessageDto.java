package app.gozenko.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDto {
    private String address;
    //TODO: сделать EmailTheme enum
    private Long statementId;
    private String text;
}