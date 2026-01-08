package com.off3d.studio.manufacturing.dto;

import com.off3d.studio.manufacturing.domain.PrinterTechnology;


public record PrinterRequestDTO(
    String modelName,
    PrinterTechnology technology
){}
