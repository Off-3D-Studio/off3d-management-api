package com.off3d.studio.auth.controller;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.auth.dto.AuthenticationDTO;
import com.off3d.studio.auth.dto.LoginResponseDTO;
import com.off3d.studio.auth.security.TokenService;
import com.off3d.studio.auth.service.AuthService;
import com.off3d.studio.auth.dto.UserRequestDTO;
import com.off3d.studio.auth.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        var user = (User) auth.getPrincipal();

        return ResponseEntity.ok(new LoginResponseDTO(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole()
        ));
    }
}