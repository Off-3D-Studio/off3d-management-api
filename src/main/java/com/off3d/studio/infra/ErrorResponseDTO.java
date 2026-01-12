package com.off3d.studio.infra;

public record ErrorResponseDTO(int status, String message, long timestamp) {}