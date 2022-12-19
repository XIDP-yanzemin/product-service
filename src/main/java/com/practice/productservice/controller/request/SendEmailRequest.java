package com.practice.productservice.controller.request;

import com.practice.productservice.controller.response.ListUserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailRequest {

    @NotEmpty(message = "email receiver must not be empty.")
    private String emailReceiver;

    @NotEmpty(message = "subject must not be empty.")
    private String subject;

    @NotEmpty(message = "body must not be empty.")
    private String emailBody;

    @NotEmpty(message = "email text must not be null.")
    private String text;

    public static SendEmailRequest buildNotificationRequestFrom(ListUserResponse postOwner, String contactorEmail, String subject, String emailBody) {
        return builder()
                .emailReceiver(postOwner.getEmail())
                .subject(subject)
                .emailBody(emailBody)
                .text(contactorEmail)
                .build();
    }
}
