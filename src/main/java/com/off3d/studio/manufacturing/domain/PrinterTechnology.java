package com.off3d.studio.manufacturing.domain;

import lombok.Getter;

@Getter
public enum PrinterTechnology {
    FDM, // Deposição de Material Fundido (Filamento)
    SLA, // Estereolitografia (Resina)
    SLS  // Sinterização Seletiva a Laser (Pó)
}
