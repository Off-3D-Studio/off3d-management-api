package com.off3d.studio.infra.exceptions;

public class TokenGenerationException extends RuntimeException {
    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}