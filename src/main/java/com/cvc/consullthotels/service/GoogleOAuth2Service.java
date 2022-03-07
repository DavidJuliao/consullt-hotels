package com.cvc.consullthotels.service;

import com.cvc.consullthotels.Exception.FeignGeneralException;
import com.cvc.consullthotels.Exception.GoogleTokenInvalidException;
import com.cvc.consullthotels.domain.dto.GoogleTokenResponseDto;
import com.cvc.consullthotels.domain.dto.JwtResponseDto;
import com.cvc.consullthotels.domain.dto.UserDataDto;
import com.cvc.consullthotels.enums.TokenType;
import com.cvc.consullthotels.security.TokenProvider;
import com.cvc.consullthotels.security.UserPrincipal;
import com.cvc.consullthotels.service.client.GoogleOAuth2Client;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class GoogleOAuth2Service {

    @Autowired
    private GoogleOAuth2Client googleOAuth2Client;

    @Autowired
    private TokenProvider tokenProvider;

    public JwtResponseDto validateToken( String googleTokenId) throws GoogleTokenInvalidException {
        GoogleTokenResponseDto googleTokenResponse = null;

        try {
             googleTokenResponse = googleOAuth2Client.validateToken(googleTokenId);
        }catch (FeignException fex){
            throw new FeignGeneralException(fex.status(), fex.getMessage(), fex.request(), fex.getCause());
        }
        
        String email = googleTokenResponse.getEmail();
        String name = googleTokenResponse.getName();

        final UserDetails userDetails = UserPrincipal.create(email);

        UserDataDto userData = UserDataDto.builder()
                .name(name)
                .email(email)
                .build();

        String tokenJwt = tokenProvider.generateToken(userDetails);

        return JwtResponseDto.builder()
                .type(TokenType.BEARER.getDescription())
                .jwtToken(tokenJwt)
                .userData(userData)
                .build();
    }

}
