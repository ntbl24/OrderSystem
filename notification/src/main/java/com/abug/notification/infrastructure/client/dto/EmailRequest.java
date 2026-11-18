package com.abug.notification.infrastructure.client.dto;

import java.util.List;


public record EmailRequest(
        Sender sender,
        List<Recipient> to,
        String subject,
        String htmlContent
) {

}
