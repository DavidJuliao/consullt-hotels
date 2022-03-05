package com.cvc.consullthotels.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private Integer status;
    private String type;
    private String title;
    private String detail;

    public static ApiError crateBodyError(int valueStatus, String type, String title, String detail){
        return ApiError.builder()
                .status(valueStatus)
                .type(type)
                .title(title)
                .detail(detail)
                .build();
    }

}
