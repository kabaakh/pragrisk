package com.gobr.pragrisk.domain.enumeration;

/**
 * The Environment enumeration.
 */
public enum Environment {
    KOM("Kommune"),
    KS,
    UV("Uvedkommende"),
    LEV("Leverandør");

    private final String value;

    Environment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
