package com.cvc.consullthotels.Exception;

public class HotelInformationNotFoundException extends Exception{

    private final String message;

    public HotelInformationNotFoundException(String message) {
        this.message = String.valueOf(message);
    }
}
