package com.off3d.studio.auth.dto;

import com.off3d.studio.auth.domain.UserRole;

public record LoginResponseDTO(
        String token,
        String name,
        String email,
        UserRole role
) {}