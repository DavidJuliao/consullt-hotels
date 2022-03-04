package com.cvc.consullthotels.enums;

import com.cvc.consullthotels.calculator.CalculatorCommission;

import java.math.BigDecimal;

public enum CalculateCommission {
    COMMISSION(new BigDecimal("0.7"), new CalculatorCommission());

    private final BigDecimal value;
    private final CalculatorCommission calculator;

    CalculateCommission(BigDecimal value, CalculatorCommission calculator) {
        this.value = value;
        this.calculator = calculator;
    }

    public BigDecimal getValue() {
        return value;
    }

    public CalculatorCommission getCalculator() {
        return calculator;
    }
}
