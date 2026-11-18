package com.abug.notification.application.usecase;

import com.abug.notification.application.dto.SendEmailResult;
import com.abug.notification.application.port.in.EmailUseCase;
import com.abug.notification.application.port.in.SendEmailCommand;
import com.abug.notification.application.port.out.EmailPort;
import com.abug.notification.common.exception.AppException;
import com.abug.notification.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements EmailUseCase {
    private final EmailPort emailPort;

    @Override
    public SendEmailResult sendEmail(SendEmailCommand command) {

        EmailPort.SendResult result = emailPort.sendEmail(command)
                .orElseThrow(() -> new AppException(ErrorCode.CANNOT_SEND_EMAIL));

        return new SendEmailResult(result.messageId());
    }
}
