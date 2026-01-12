package com.off3d.studio.auth.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.auth.dto.UserRequestDTO;
import com.off3d.studio.auth.dto.UserResponseDTO;
import com.off3d.studio.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
            throw new RuntimeException("E-mail já cadastrado");
        }

        User newUser = new User();
        newUser.setName(dto.name());
        newUser.setEmail(dto.email());
        newUser.setRole(dto.role());

        newUser.setPassword(passwordEncoder.encode(dto.password()));

        return mapToResponseDTO(userRepository.save(newUser));
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado encontrado");
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
