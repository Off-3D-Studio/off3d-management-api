package com.off3d.studio.auth.security;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.auth.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        // Injeta o valor do secret manualmente já que não temos o contexto do Spring
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-123");
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido para um usuário")
    void shouldGenerateToken() {
        User user = new User();
        user.setEmail("socio@off3d.com.br");

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve validar um token correto e retornar o e-mail (subject)")
    void shouldValidateCorrectToken() {
        User user = new User();
        user.setEmail("admin@off3d.com.br");
        String token = tokenService.generateToken(user);

        String email = tokenService.validateToken(token);

        assertEquals("admin@off3d.com.br", email);
    }

    @Test
    @DisplayName("Deve retornar string vazia para token inválido")
    void shouldReturnEmptyForInvalidToken() {
        String invalidToken = "token-totalmente-errado";

        String result = tokenService.validateToken(invalidToken);

        assertEquals("", result);
    }
}