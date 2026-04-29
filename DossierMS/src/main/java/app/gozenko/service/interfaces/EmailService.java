package app.gozenko.service.interfaces;

import app.gozenko.dto.EmailMessageDto;

public interface EmailService {
    void sendEmail(EmailMessageDto dto);
}
