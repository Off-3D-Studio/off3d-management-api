package com.off3d.studio.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // 1. Trata erros de negócio (ex: e-mail já cadastrado, usuário não encontrado)
    @ExceptionHandler(RuntimeException.class)
    private ResponseEntity<ErrorResponseDTO> runtimeHandler(RuntimeException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // 2. Trata erros de banco de dados (ex: deletar cliente com pedido vinculado)
    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<ErrorResponseDTO> handleIntegrityViolation(DataIntegrityViolationException exception) {
        log.error("Conflito de integridade: {}", exception.getMostSpecificCause().getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, "Erro de integridade: Registro vinculado a outros processos.");
    }

    // 3. Trata Login incorreto
    @ExceptionHandler(BadCredentialsException.class)
    private ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException exception) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    // 4. ESSENCIAL: Trata quando um usuário tenta acessar o que não deve (ex: Operador em Sales)
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    private ResponseEntity<ErrorResponseDTO> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso.");
    }

    // 5. Trata erros de validação (ex: campos @NotBlank vazios no DTO)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    private ResponseEntity<ErrorResponseDTO> handleValidation(jakarta.validation.ConstraintViolationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Dados inválidos: verifique os campos enviados.");
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                status.value(),
                message,
                Instant.now().toEpochMilli()
        );
        return ResponseEntity.status(status).body(error);
    }
}