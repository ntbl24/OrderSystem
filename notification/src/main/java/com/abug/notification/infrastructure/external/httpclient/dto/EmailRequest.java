package com.abug.notification.infrastructure.external.httpclient.dto;

import java.util.List;


public record EmailRequest(
        Sender sender,
        List<Recipient> to,
        String subject,
        String htmlContent
) {

}
