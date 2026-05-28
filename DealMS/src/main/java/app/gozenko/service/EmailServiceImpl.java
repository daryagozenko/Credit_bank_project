package app.gozenko.service;

import app.gozenko.dto.EmailMessageDto;
import app.gozenko.enums.EmailTheme;
import app.gozenko.service.interfaces.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${mail.receiver}")
    private String toAddress;

    @Override
    public EmailMessageDto createEmailMessage(EmailTheme theme, UUID statementId, String text) {
        return EmailMessageDto.builder()
                .address(toAddress)
                .theme(theme)
                .statementId(statementId)
                .text(text)
                .build();
    }
}
