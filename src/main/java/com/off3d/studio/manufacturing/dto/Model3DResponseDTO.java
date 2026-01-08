package com.off3d.studio.manufacturing.dto;

import java.util.UUID;

public record Model3DResponseDTO(
        UUID id,
        String fileName,
        String filePath,
        Double volumeCm3,
        UUID customerId
) {}