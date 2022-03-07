package com.cvc.consullthotels.service.client;

import com.cvc.consullthotels.Exception.GoogleTokenInvalidException;
import com.cvc.consullthotels.domain.dto.GoogleTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(url = "${google.oauth2.api.url}", value = "googleOAuth2")
public interface GoogleOAuth2Client {

    @GetMapping("/tokeninfo")
    GoogleTokenResponseDto validateToken(@RequestParam("id_token") String tokenId) throws GoogleTokenInvalidException;

}
