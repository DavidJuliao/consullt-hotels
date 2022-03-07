package com.cvc.consullthotels.Exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class GoogleTokenInvalidResponse {

    private String error = "invalid_token";
    private String error_description = "Invalid Value";

}
