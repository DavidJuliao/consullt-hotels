package com.cvc.consullthotels.domain.dto;

import com.cvc.consullthotels.enums.CalculateCommission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto implements Serializable {

    private Long id;
    private CategoryDto category;
    private BigDecimal totalPrice;
    private PriceDetailDto priceDetail;

    public void calculateTotalPrice(LocalDate initialDate, LocalDate finalDate, int quantityAdults, int quantityChild){
        this.totalPrice = CalculateCommission.COMMISSION.getCalculator()
                .calculate(priceDetail.getPricePerDayAdult(), priceDetail.getPricePerDayChild(),
                            initialDate, finalDate, quantityAdults, quantityChild);
    }
}
