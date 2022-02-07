package com.gobr.pragrisk.domain.enumeration;

/**
 * The MitigationStatus enumeration.
 */
public enum MitigationStatus {
    MISS("Not performed"),
    ADHOC("Performed informally"),
    REPT("Planned &amp; tracked"),
    DEF("Well defined"),
    MEAS("Quantitatively controlled"),
    CONT("Continuously improved");

    private final String value;

    MitigationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
