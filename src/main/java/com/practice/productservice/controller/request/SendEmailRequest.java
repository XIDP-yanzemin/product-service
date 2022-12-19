package com.practice.productservice.controller.request;

import com.practice.productservice.constant.Constant;
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

    public static SendEmailRequest buildSellProductNotificationFrom(ListUserResponse postOwner, String contactorEmail) {
        return builder()
                .emailReceiver(postOwner.getEmail())
                .subject(Constant.SELL_SUBJECT)
                .emailBody(Constant.SELL_EMAIL_BODY)
                .text(contactorEmail)
                .build();
    }

    public static SendEmailRequest buildBuyProductNotificationFrom(ListUserResponse postOwner, String contactorEmail) {
        return builder()
                .emailReceiver(postOwner.getEmail())
                .subject(Constant.BUY_SUBJECT)
                .emailBody(Constant.BUY_EMAIL_BODY)
                .text(contactorEmail)
                .build();
    }
}
