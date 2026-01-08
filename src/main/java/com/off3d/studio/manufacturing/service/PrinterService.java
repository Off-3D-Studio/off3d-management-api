package com.off3d.studio.manufacturing.service;

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
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrinterService {

    private final PrinterRepository printerRepository;

    public PrinterService(PrinterRepository printerRepository) {
        this.printerRepository = printerRepository;
    }

    @Transactional
    public PrinterResponseDTO save(PrinterRequestDTO dto) {
        log.info("Cadastrando nova impressora: {}", dto.modelName());

        Printer printer = new Printer();
        printer.setModelName(dto.modelName());
        printer.setTechnology(dto.technology());
        printer.setStatus(PrinterStatus.AVAILABLE);

        Printer savedPrinter = printerRepository.save(printer);
        return mapToResponseDTO(savedPrinter);
    }

    @Transactional(readOnly = true)
    public List<PrinterResponseDTO> findAll() {
        return printerRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Printer findById(UUID id) {
        return printerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impressora não encontrada"));
    }

    @Transactional
    public PrinterResponseDTO update(UUID id, PrinterRequestDTO dto) {
        log.info("Atualizando impressora ID: {}", id);

        Printer printer = findById(id);
        printer.setModelName(dto.modelName());
        printer.setTechnology(dto.technology());

        Printer updatedPrinter = printerRepository.save(printer);
        return mapToResponseDTO(updatedPrinter);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão da impressora ID: {}", id);

        if (!printerRepository.existsById(id)) {
            log.error("Erro: Impressora {} não encontrada para exclusão", id);
            throw new RuntimeException("Impressora não encontrada");
        }

        try {
            printerRepository.deleteById(id);
            log.info("Impressora {} removida com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha: Impressora {} possui trabalhos vinculados e não pode ser deletada", id);
            throw e;
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