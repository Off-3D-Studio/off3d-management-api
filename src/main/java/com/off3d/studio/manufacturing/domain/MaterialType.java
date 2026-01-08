package com.off3d.studio.manufacturing.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MaterialType {
    PLA;

    @JsonCreator
    public static MaterialType fromValue(String value) {
        return MaterialType.valueOf(value.toUpperCase());
    }
}
