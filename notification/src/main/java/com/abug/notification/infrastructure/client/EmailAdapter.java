package com.abug.notification.infrastructure.client;

import com.abug.notification.application.port.in.SendEmailCommand;
import com.abug.notification.application.port.out.EmailPort;
import com.abug.notification.common.exception.AppException;
import com.abug.notification.common.exception.ErrorCode;
import com.abug.notification.infrastructure.client.dto.EmailRequest;
import com.abug.notification.infrastructure.client.dto.EmailResponse;
import com.abug.notification.infrastructure.client.dto.Recipient;
import com.abug.notification.infrastructure.client.dto.Sender;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAdapter implements EmailPort {
    private final EmailClient emailClient;

    @Value("${notification.email.brevo-apikey}")
    private String apiKey;

    @Value("${notification.email.sender-name}")
    private String senderName;

    @Value("${notification.email.sender-email}")
    private String senderEmail;

    @Override
    public Optional<SendResult> sendEmail(SendEmailCommand command) {


        EmailRequest emailRequest = new EmailRequest(
                new Sender(senderName, senderEmail),
                List.of(new Recipient(command.name(), command.email())),
                command.subject(),
                command.htmlContent()
        );

        try{
            EmailResponse response = emailClient.sendEmail(apiKey, emailRequest);
            return Optional.of(new SendResult(response.messageId()));
        } catch (FeignException exception){
            log.info(exception.toString());
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }

    }
}
