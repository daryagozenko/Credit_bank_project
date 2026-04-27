package app.gozenko.service;

import app.gozenko.dto.EmailMessageDto;
import app.gozenko.service.interfaces.DossierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierServiceImpl implements DossierService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailMessageDto dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getAddress());
        message.setSubject(dto.getTheme().toString());
        message.setText(dto.getText());

        mailSender.send(message);
        log.info("Sending mail message");
    }
}
