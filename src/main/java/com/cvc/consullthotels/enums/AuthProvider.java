package com.cvc.consullthotels.enums;

import java.util.Locale;

public enum AuthProvider {
    GOOGLE;

    public static AuthProvider getProvider(String name) {
        return AuthProvider.valueOf(name.toUpperCase(Locale.ROOT));
    }
}