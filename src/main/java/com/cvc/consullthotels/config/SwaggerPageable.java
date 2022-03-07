package com.cvc.consullthotels.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwaggerPageable {
    @ApiModelProperty(value = "Number of records per page", example = "20")
    private Integer size;
    @ApiModelProperty(value = "Results page you want to retrieve (0..N)", example = "0")
    private Integer page;
}
