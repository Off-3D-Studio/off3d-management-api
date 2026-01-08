package com.off3d.studio.sales.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pendente"),
    IN_PROGRESS("Em Progresso"),
    COMPLETED("Concluído"),
    CANCELED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
