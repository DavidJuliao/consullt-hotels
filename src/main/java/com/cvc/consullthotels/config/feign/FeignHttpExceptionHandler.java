package com.cvc.consullthotels.config.feign;

import feign.Response;

public interface FeignHttpExceptionHandler {
    Exception handle(Response response);
}