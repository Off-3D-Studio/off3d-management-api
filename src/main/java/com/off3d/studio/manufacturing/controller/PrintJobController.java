package com.off3d.studio.manufacturing.controller;

import com.off3d.studio.manufacturing.domain.PrintJob;
import com.off3d.studio.manufacturing.dto.PrintJobRequestDTO;
import com.off3d.studio.manufacturing.dto.PrintJobResponseDTO;
import com.off3d.studio.manufacturing.service.PrintJobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/print-jobs")
public class PrintJobController {

    private final PrintJobService printJobService;

    public PrintJobController(PrintJobService printJobService) {
        this.printJobService = printJobService;
    }

    @PostMapping
    public ResponseEntity<PrintJobResponseDTO> createPrintJob(@RequestBody PrintJobRequestDTO dto) {
        PrintJobResponseDTO response = printJobService.createPrintJob(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PrintJobResponseDTO>> getAll() {
        return ResponseEntity.ok(printJobService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrintJobResponseDTO> getById(@PathVariable UUID id) {
        PrintJob job = printJobService.findById(id);

        PrintJobResponseDTO response = new PrintJobResponseDTO(
                job.getId(),
                job.getEstimatedTime(),
                job.getStatus().name(),
                job.getStatus().getDescription(),
                job.getOrder().getId(),
                job.getPrinter().getModelName(),
                job.getModel().getFileName()
        );
        return ResponseEntity.ok(response);
    }
}