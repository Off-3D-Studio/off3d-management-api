package com.off3d.studio.auth.domain;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN(Constants.ADMIN_VALUE),
    PARTNER(Constants.PARTNER_VALUE),
    OPERATOR(Constants.OPERATOR_VALUE);

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    // Classe interna para centralizar as Strings literais
    public static class Constants {
        public static final String ADMIN_VALUE = "ADMIN";
        public static final String PARTNER_VALUE = "PARTNER";
        public static final String OPERATOR_VALUE = "OPERATOR";

        // Se quiser usar descrições amigáveis no Front futuramente:
        public static final String ADMIN_DESC = "Administrador";
        public static final String PARTNER_DESC = "Sócio";
        public static final String OPERATOR_DESC = "Operador";
    }
}