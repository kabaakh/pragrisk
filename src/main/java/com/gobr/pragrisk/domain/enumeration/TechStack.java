package com.gobr.pragrisk.domain.enumeration;

/**
 * The TechStack enumeration.
 */
public enum TechStack {
    JAVA("Java"),
    NET(".NET"),
    PHP;

    private final String value;

    TechStack(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
