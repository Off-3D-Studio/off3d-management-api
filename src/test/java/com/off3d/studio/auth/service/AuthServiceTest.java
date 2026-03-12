package com.off3d.studio.auth.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.auth.domain.UserRole;
import com.off3d.studio.auth.dto.UserRequestDTO;
import com.off3d.studio.auth.dto.UserResponseDTO;
import com.off3d.studio.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve registrar um usuário com senha criptografada")
    void shouldRegisterUserSuccessfully() {
        // Cenario
        UserRequestDTO dto = new UserRequestDTO("Amanda", "amanda@off3d.com", "senha123", UserRole.ADMIN);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.password())).thenReturn("senha_hash_bcrypt");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Acao
        UserResponseDTO response = authService.register(dto);

        // Verificacao
        assertNotNull(response);
        assertEquals(dto.email(), response.email());
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o e-mail já existir")
    void shouldThrowExceptionWhenEmailExists() {
        UserRequestDTO dto = new UserRequestDTO("Amanda", "amanda@off3d.com", "senha123", UserRole.ADMIN);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(dto));

        assertEquals("E-mail já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve recuperar o usuário autenticado atual com sucesso")
    void shouldGetCurrentUserSuccessfully() {
        // Cenário: Criamos um mock do contexto de segurança
        User mockUser = new User();
        mockUser.setEmail("amanda@off3d.com.br");
        mockUser.setName("Amanda");

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(mockUser);
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);

        // Ação
        User result = authService.getCurrentUser();

        // Verificação
        assertNotNull(result);
        assertEquals("amanda@off3d.com.br", result.getEmail());

        // Limpeza (Importante para não afetar outros testes)
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar recuperar usuário sem autenticação no contexto")
    void shouldThrowExceptionWhenNoAuthenticationInContext() {
        // Cenário: Contexto vazio
        SecurityContextHolder.clearContext();

        // Ação & Verificação
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.getCurrentUser();
        });

        assertEquals("Nenhum usuário autenticado encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar recuperar usuário não autenticado")
    void shouldThrowExceptionWhenUserIsNotAuthenticated() {
        // Cenário: Existe auth no contexto, mas isAuthenticated é false
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(auth.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Ação & Verificação
        assertThrows(RuntimeException.class, () -> authService.getCurrentUser());

        SecurityContextHolder.clearContext();
    }
}