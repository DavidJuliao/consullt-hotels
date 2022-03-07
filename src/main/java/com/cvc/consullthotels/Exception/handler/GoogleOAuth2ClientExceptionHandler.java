package com.cvc.consullthotels.Exception.handler;

import com.cvc.consullthotels.Exception.GoogleTokenInvalidException;
import com.cvc.consullthotels.config.feign.FeignHttpExceptionHandler;
import feign.Response;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuth2ClientExceptionHandler implements FeignHttpExceptionHandler {

    @Override
    public Exception handle(Response response) {
        return new GoogleTokenInvalidException();
    }

}