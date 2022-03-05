package com.cvc.consullthotels.Exception;

public class HotelInformationNotFoundException extends Exception{

    private final String hotelId;

    public HotelInformationNotFoundException(Long hotelId) {
        this.hotelId = String.valueOf(hotelId);
    }
}
