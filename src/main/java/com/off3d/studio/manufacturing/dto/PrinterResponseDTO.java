package com.off3d.studio.manufacturing.dto;

import com.off3d.studio.manufacturing.domain.PrinterStatus;
import com.off3d.studio.manufacturing.domain.PrinterTechnology;

import java.util.UUID;

public record PrinterResponseDTO(
        UUID id,
        String modelName,
        PrinterTechnology technology,
        PrinterStatus status
) {}
