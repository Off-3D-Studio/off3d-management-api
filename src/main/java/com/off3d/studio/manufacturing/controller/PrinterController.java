package com.off3d.studio.manufacturing.controller;

import com.off3d.studio.manufacturing.domain.Printer;
import com.off3d.studio.manufacturing.dto.PrinterRequestDTO;
import com.off3d.studio.manufacturing.dto.PrinterResponseDTO;
import com.off3d.studio.manufacturing.service.PrinterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/printers")
public class PrinterController {

    private final PrinterService printerService;

    public PrinterController(PrinterService printerService) {
        this.printerService = printerService;
    }

    @PostMapping
    public ResponseEntity<PrinterResponseDTO> create(@RequestBody PrinterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(printerService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrinterResponseDTO>> getAll() {
        return ResponseEntity.ok(printerService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrinterResponseDTO> update(@PathVariable UUID id, @RequestBody PrinterRequestDTO dto) {
        return ResponseEntity.ok(printerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        printerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}