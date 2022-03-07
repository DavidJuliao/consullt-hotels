package com.cvc.consullthotels.enums;

public enum TokenType {
    BEARER("Bearer");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
