package com.off3d.studio.auth.dto;

import com.off3d.studio.auth.domain.UserRole;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role
) {}