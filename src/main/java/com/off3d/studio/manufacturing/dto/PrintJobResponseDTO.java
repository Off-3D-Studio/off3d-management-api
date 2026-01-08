package com.off3d.studio.manufacturing.dto;

import java.time.Duration;
import java.util.UUID;

public record PrintJobResponseDTO(
        UUID id,
        Duration estimatedTime,
        String status,
        String statusDescription,
        UUID orderId,
        String printerName,
        String modelFileName
) {}