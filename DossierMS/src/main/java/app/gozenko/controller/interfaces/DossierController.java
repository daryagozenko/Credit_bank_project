package app.gozenko.controller.interfaces;

import app.gozenko.dto.EmailMessageDto;

public interface DossierController {
    void sendingEmailWithSuccess(EmailMessageDto dto);
    void sendingEmailWithDenied(EmailMessageDto dto);
}
