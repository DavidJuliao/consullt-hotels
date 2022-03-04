package com.cvc.consullthotels.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceDetailDto implements Serializable {

    private BigDecimal pricePerDayAdult;
    private BigDecimal pricePerDayChild;
}
