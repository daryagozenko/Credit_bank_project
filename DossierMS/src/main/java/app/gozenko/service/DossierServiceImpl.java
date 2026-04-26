package app.gozenko.service;

import app.gozenko.dto.EmailMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierServiceImpl {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String toAddress;

    public void sendEmail(EmailMessageDto dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddress);
        message.setSubject(dto.getTheme().toString());
        message.setText(dto.getText());

        mailSender.send(message);
        log.info("Sending mail message");
    }
}
