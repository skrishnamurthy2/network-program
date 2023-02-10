package com.newrelic.app.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * The application can run in server or client mode and this enum is used to represent that.
 */
public enum ServiceMode {
    SERVER, CLIENT;

    public static ServiceMode getByMode(String mode) {
        return Arrays.stream(ServiceMode.values())
                .filter(s -> StringUtils.equalsIgnoreCase(mode, s.name()))
                .findFirst()
                .orElse(null);
    }
}
