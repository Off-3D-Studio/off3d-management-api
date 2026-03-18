package com.off3d.studio.manufacturing.service;

import com.off3d.studio.auth.service.AuthService;
import com.off3d.studio.infra.exceptions.BusinessException;
import com.off3d.studio.infra.exceptions.ResourceNotFoundException;
import com.off3d.studio.manufacturing.domain.Printer;
import com.off3d.studio.manufacturing.domain.PrinterStatus;
import com.off3d.studio.manufacturing.dto.PrinterRequestDTO;
import com.off3d.studio.manufacturing.dto.PrinterResponseDTO;
import com.off3d.studio.manufacturing.repository.PrinterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.off3d.studio.infra.config.ErrorMessages.PRINTER_NOT_FOUND;

@Slf4j
@Service
public class PrinterService {

    private final PrinterRepository printerRepository;
    private final AuthService authService;

    public PrinterService(PrinterRepository printerRepository, AuthService authService) {
        this.printerRepository = printerRepository;
        this.authService = authService;
    }

    @Transactional
    public PrinterResponseDTO save(PrinterRequestDTO dto) {
        log.info("Cadastrando nova impressora: {}", dto.modelName());

        Printer printer = new Printer();
        printer.setModelName(dto.modelName());
        printer.setTechnology(dto.technology());
        printer.setStatus(dto.status() != null ? dto.status() : PrinterStatus.AVAILABLE);

        printer.setCreatedBy(authService.getCurrentUser());

        return mapToResponseDTO(printerRepository.save(printer));
    }

    @Transactional(readOnly = true)
    public List<PrinterResponseDTO> findAll() {
        log.info("Buscando todas as impressoras cadastradas");
        return printerRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Printer findById(UUID id) {
        return printerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRINTER_NOT_FOUND + id));
    }

    @Transactional(readOnly = true)
    public PrinterResponseDTO findByIdDetailed(UUID id) {
        return mapToResponseDTO(findById(id));
    }

    @Transactional
    public PrinterResponseDTO update(UUID id, PrinterRequestDTO dto) {
        log.info("Atualizando Impressora ID: {} para o status: {}", id, dto.status());

        Printer printer = findById(id);

        printer.setModelName(dto.modelName());
        printer.setTechnology(dto.technology());

        if (dto.status() != null) {
            printer.setStatus(dto.status());
        }

        return mapToResponseDTO(printerRepository.save(printer));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão da impressora ID: {}", id);

        Printer printer = findById(id);

        try {
            printerRepository.delete(printer);
            log.info("Impressora {} removida com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha: Impressora {} possui trabalhos vinculados", id);
            throw new BusinessException("Não é possível deletar uma impressora com trabalhos de impressão vinculados.");
        }
    }

    private PrinterResponseDTO mapToResponseDTO(Printer print) {
        return new PrinterResponseDTO(
                print.getId(),
                print.getModelName(),
                print.getTechnology(),
                print.getStatus()
        );
    }
}