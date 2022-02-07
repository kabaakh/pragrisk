package com.gobr.pragrisk.domain.enumeration;

/**
 * The TechCategory enumeration.
 */
public enum TechCategory {
    FAG("Fagsystem"),
    FEL("Fellestjeneste"),
    KOM("Komponent");

    private final String value;

    TechCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
