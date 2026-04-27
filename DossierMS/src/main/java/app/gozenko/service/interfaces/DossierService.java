package app.gozenko.service.interfaces;

import app.gozenko.dto.EmailMessageDto;

public interface DossierService {
    void sendEmail(EmailMessageDto dto);
}
