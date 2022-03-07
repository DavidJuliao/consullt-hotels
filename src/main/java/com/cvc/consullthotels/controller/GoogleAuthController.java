package com.cvc.consullthotels.controller;

import com.cvc.consullthotels.Exception.GoogleTokenInvalidException;
import com.cvc.consullthotels.domain.dto.JwtResponseDto;
import com.cvc.consullthotels.service.GoogleOAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.constraints.NotBlank;

@RestController
@RestControllerAdvice()
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @Autowired
    private GoogleOAuth2Service googleOAuth2Service;

    @PostMapping
    public JwtResponseDto validateToken(@RequestParam @NotBlank String googleTokenId ) throws GoogleTokenInvalidException {

        return googleOAuth2Service.validateToken( googleTokenId);
    }

}
