package com.off3d.studio.auth.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.auth.dto.UserRequestDTO;
import com.off3d.studio.auth.dto.UserResponseDTO;
import com.off3d.studio.auth.repository.UserRepository;
import com.off3d.studio.infra.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO register(UserRequestDTO dto) {
        if(userRepository.findByEmail(dto.email()).isPresent()) {
            log.warn("Tentativa de cadastro com e-mail já existente: {}", dto.email());
            throw new BusinessException("E-mail já cadastrado no sistema.");
        }

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        newUser.setRole(dto.role());
        newUser.setPassword(passwordEncoder.encode(dto.password()));

        log.info("Novo usuário cadastrado com sucesso: {}", dto.email());
        return mapToResponseDTO(userRepository.save(newUser));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Nenhum usuário autenticado encontrado no contexto de segurança.");
        }

        return (User) authentication.getPrincipal();
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}