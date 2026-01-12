package com.off3d.studio.auth.dto;

import com.off3d.studio.auth.domain.UserRole;

public record UserRequestDTO(
        String name,
        String email,
        String password,
        UserRole role
) {}