package app.gozenko.service.interfaces;

import app.gozenko.dto.EmailMessageDto;
import app.gozenko.enums.EmailTheme;

import java.util.UUID;

public interface EmailService {
    EmailMessageDto createEmailMessage(EmailTheme theme, UUID statementId, String text);
}
