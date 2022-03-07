package com.cvc.consullthotels.enums;

import lombok.Getter;

@Getter
public enum ErrorType {
    REDIS_CONNECTION_ERROR("Redis Connection error","redis-connection-error","redis.connection.error"),
    GOOGLE_TOKEN_INVALID("Invalid Token","invalid-token","invalid.token"),
    UNAUTHORIZED("Unauthorized action","unauthorized-action","authentication.unauthorized"),
    CONSULT_HOTEL_INFORMATION("error in consult of hotel information","error-consult-hotel-information","error.consult.hotel.information"),
    HOTEL_INFORMATION_NOT_FOUNT("No hotel information found","hotel-information-not-found","hotel.information.not.found"),
    INVALID_FORMAT_GENERIC("Invalid format parameters","invalid-format-parameters","invalid.format.parameter.generic"),
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
