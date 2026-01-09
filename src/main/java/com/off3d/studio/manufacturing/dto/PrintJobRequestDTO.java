package com.off3d.studio.manufacturing.dto;

import com.off3d.studio.manufacturing.domain.PrintJobStatus;

import java.time.Duration;
import java.util.UUID;

public record PrintJobRequestDTO(
        UUID orderId,
        UUID modelId,
        UUID printerId,
        UUID materialId,
        Duration estimatedTime,
        PrintJobStatus status
) {}
