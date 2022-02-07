package com.gobr.pragrisk.domain.enumeration;

/**
 * The MitigationType enumeration.
 */
public enum MitigationType {
    PREV("Preventive"),
    DETECT("Detective"),
    CORR("Corrective"),
    DETER("Deterrent");

    private final String value;

    MitigationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
