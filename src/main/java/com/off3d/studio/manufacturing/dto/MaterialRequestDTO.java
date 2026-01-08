package com.off3d.studio.manufacturing.dto;

import com.off3d.studio.manufacturing.domain.MaterialType;

public record MaterialRequestDTO(
        String name,
        String color,
        String brand,
        Double weightGrams,
        MaterialType type,
        String description
) {}
