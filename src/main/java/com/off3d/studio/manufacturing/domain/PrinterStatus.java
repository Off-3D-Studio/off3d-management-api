package com.off3d.studio.manufacturing.domain;

import lombok.Getter;

@Getter
public enum PrinterStatus {
    AVALIABLE("Disponivel"),
    PRINTING("Imprimindo"),
    MAINTENANCE("Em manutenção");

    private final String status;

    PrinterStatus(String description) {
        this.status = description;
    }
}
