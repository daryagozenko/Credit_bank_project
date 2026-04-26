package app.gozenko.service;

import app.gozenko.dto.EmailMessageDto;
import app.gozenko.enums.EmailTheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EmailServiceImpl {

    @Value("${gozenko.mail}")
    private String toAddress;

    public EmailMessageDto createEmailMessage(EmailTheme theme, UUID statementId, String text) {
        return EmailMessageDto.builder()
                .address(toAddress)
                .theme(theme)
                .statementId(statementId.getLeastSignificantBits())
                .text(text)
                .build();
    }
}
