package com.cvc.consullthotels.calculator;

import com.cvc.consullthotels.enums.CalculateCommission;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static com.cvc.consullthotels.Utils.DateUtils.getDaysByDates;

public class CalculatorCommission {

    public BigDecimal calculate(BigDecimal adultValue, BigDecimal childValue,
                                       LocalDate initialDate, LocalDate finalDate, int quantityAdults, int quantityChild){

        BigDecimal days = BigDecimal.valueOf(getDaysByDates(initialDate, finalDate));
        BigDecimal valueAdults = days.multiply(adultValue).multiply(BigDecimal.valueOf(quantityAdults));
        BigDecimal valueChild = days.multiply(childValue).multiply(BigDecimal.valueOf(quantityChild));
        BigDecimal totalValues = valueAdults.add(valueChild);
        BigDecimal valueCommission = totalValues.divide(CalculateCommission.COMMISSION.getValue(), RoundingMode.HALF_EVEN);
        return valueCommission.add(totalValues);
    }
}
