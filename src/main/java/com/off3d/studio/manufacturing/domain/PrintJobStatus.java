package com.off3d.studio.manufacturing.domain;

import lombok.Getter;

@Getter
public enum PrintJobStatus {

    QUEUED("Na Fila"),
    PREPARING("Preparando Material"),
    PRINTING("Em Impressão"),
    PAUSED("Pausado"),
    COMPLETED("Concluído"),
    FAILED("Falha na Impressão"),
    CANCELED("Cancelado");

    private final String description;

    PrintJobStatus(String description) {
        this.description = description;
    }
}
