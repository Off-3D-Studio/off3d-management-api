package com.off3d.studio.manufacturing.service;


import com.off3d.studio.manufacturing.domain.Printer;
import com.off3d.studio.manufacturing.domain.PrinterStatus;
import com.off3d.studio.manufacturing.dto.PrinterRequestDTO;
import com.off3d.studio.manufacturing.dto.PrinterResponseDTO;
import com.off3d.studio.manufacturing.repository.PrinterRepository;
import lombok.extern.slf4j.Slf4j;
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
        printer.setStatus(PrinterStatus.AVALIABLE);

        Printer savedPrinter = printerRepository.save(printer);
        return mapToResponseDTO(savedPrinter);
    }

    @Transactional(readOnly = true)
    public List<PrinterResponseDTO> findAll() {
        return printerRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }


    public Printer findById(UUID id) {
        return printerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impressora não encontrada"));
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
