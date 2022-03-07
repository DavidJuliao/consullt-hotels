package com.cvc.consullthotels.Exception;

import feign.FeignException;
import feign.Request;

public class FeignGeneralException extends FeignException {

    public FeignGeneralException(int status, String message, Request request, Throwable cause) {
        super(status, message, request, cause);
    }
}
