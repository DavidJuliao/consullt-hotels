package com.cvc.consullthotels.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    INVALID_FORMAT("Invalid format parameter","invalid-format-parameter","invalid.format.parameter"),
    CHECK_IN_DATE_INVALID("CheckIn invalid","checkIn-not-valid","checkIn.invalid"),
    CHECK_OUT_DATE_INVALID("CheckOut invalid","checkOut-not-valid","checkOut.invalid"),
    NUMBER_CLIENTS_INVALID("Number of Clients Invalid","number-clients-invalid","number.clients.invalid"),
    MISSING_PARAMETER("Missing Parameter","missing-parameter","parameter.not.informed");

    private final String title;
    private final String uri;
    private final String messageSource;

    ErrorType(String title, String uri, String messageSource) {
        this.title = title;
        this.uri = "https://cvc.com.br/" + uri;
        this.messageSource = messageSource;
    }

}
