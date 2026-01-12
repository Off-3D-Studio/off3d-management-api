package com.off3d.studio.auth.domain;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Administrador"),
    PARTNER("Sócio"),
    OPERATOR("Operador");

    private final String role;

    UserRole(String description) {
        this.role = description;
    }

}
