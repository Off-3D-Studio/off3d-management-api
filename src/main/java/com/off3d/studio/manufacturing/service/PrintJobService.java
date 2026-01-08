package com.off3d.studio.manufacturing.service;

import com.off3d.studio.manufacturing.dto.PrintJobResponseDTO;
import com.off3d.studio.manufacturing.repository.MaterialRepository;
import com.off3d.studio.manufacturing.repository.Model3DRepository;
import com.off3d.studio.manufacturing.repository.PrintJobRepository;
import com.off3d.studio.manufacturing.repository.PrinterRepository;
import com.off3d.studio.manufacturing.domain.PrintJob;
import com.off3d.studio.manufacturing.domain.PrintJobStatus;
import com.off3d.studio.manufacturing.dto.PrintJobRequestDTO;
import com.off3d.studio.sales.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PrintJobService {

    private final PrintJobRepository printJobRepository;
    private final OrderRepository orderRepository;
    private final PrinterRepository printerRepository;
    private final MaterialRepository materialRepository;
    private final Model3DRepository modelRepository;

    public PrintJobService(PrintJobRepository printJobRepository,
                           OrderRepository orderRepository,
                           PrinterRepository printerRepository,
                           MaterialRepository materialRepository,
                           Model3DRepository modelRepository) {
        this.printJobRepository = printJobRepository;
        this.orderRepository = orderRepository;
        this.printerRepository = printerRepository;
        this.materialRepository = materialRepository;
        this.modelRepository = modelRepository;
    }

    @Transactional
    public PrintJobResponseDTO createPrintJob(PrintJobRequestDTO dto) {
        var order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        var printer = printerRepository.findById(dto.printerId())
                .orElseThrow(() -> new RuntimeException("Impressora não encontrada"));
        var material = materialRepository.findById(dto.materialId())
                .orElseThrow(() -> new RuntimeException("Material não encontrado"));
        var model = modelRepository.findById(dto.modelId())
                .orElseThrow(() -> new RuntimeException("Modelo 3D não encontrado"));

        PrintJob printJob = new PrintJob();
        printJob.setEstimatedTime(dto.estimatedTime());
        printJob.setStatus(PrintJobStatus.QUEUED);

        printJob.setOrder(order);
        printJob.setPrinter(printer);
        printJob.setMaterial(material);
        printJob.setModel(model);

        PrintJob savedJob = printJobRepository.save(printJob);

        return new PrintJobResponseDTO(
                savedJob.getId(),
                savedJob.getEstimatedTime(),
                savedJob.getStatus().name(),
                savedJob.getStatus().getDescription(),
                order.getId(),
                printer.getModelName(),
                model.getFileName()
        );
    }

    public List<PrintJobResponseDTO> findAll() {
        return printJobRepository.findAll().stream()
                .map(job -> new PrintJobResponseDTO(
                        job.getId(),
                        job.getEstimatedTime(),
                        job.getStatus().name(),
                        job.getStatus().getDescription(),
                        job.getOrder().getId(),
                        job.getPrinter().getModelName(),
                        job.getModel().getFileName()
                ))
                .toList();
    }

    public PrintJob findById(UUID id) {
        return printJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabalho de impressão não encontrado"));
    }
}
