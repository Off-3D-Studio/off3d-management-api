package com.off3d.studio.manufacturing.service;

import com.off3d.studio.auth.service.AuthService;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final AuthService authService;

    public PrintJobService(PrintJobRepository printJobRepository,
                           OrderRepository orderRepository,
                           PrinterRepository printerRepository,
                           MaterialRepository materialRepository,
                           Model3DRepository modelRepository,
                           AuthService authService) {
        this.printJobRepository = printJobRepository;
        this.orderRepository = orderRepository;
        this.printerRepository = printerRepository;
        this.materialRepository = materialRepository;
        this.modelRepository = modelRepository;
        this.authService = authService;
    }

    @Transactional
    public PrintJobResponseDTO createPrintJob(PrintJobRequestDTO dto) {
        log.info("Iniciando criação de novo PrintJob para o Pedido: {}", dto.orderId());

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

        printJob.setCreatedBy(authService.getCurrentUser());

        PrintJob savedJob = printJobRepository.save(printJob);
        log.info("PrintJob criado com sucesso. ID: {}", savedJob.getId());

        return mapToResponseDTO(savedJob);
    }

    @Transactional
    public PrintJobResponseDTO update(UUID id, PrintJobRequestDTO dto) {
        log.info("Atualizando PrintJob ID: {}", id);

        PrintJob job = findEntityById(id);

        if (dto.estimatedTime() != null) job.setEstimatedTime(dto.estimatedTime());
        if (dto.status() != null) job.setStatus(dto.status());

        java.util.Optional.ofNullable(dto.printerId())
                .ifPresent(pId -> job.setPrinter(printerRepository.findById(pId)
                        .orElseThrow(() -> new RuntimeException("Impressora não encontrada"))));

        java.util.Optional.ofNullable(dto.materialId())
                .ifPresent(mId -> job.setMaterial(materialRepository.findById(mId)
                        .orElseThrow(() -> new RuntimeException("Material não encontrado"))));

        return mapToResponseDTO(printJobRepository.save(job));
    }

    @Transactional(readOnly = true)
    public List<PrintJobResponseDTO> findAll() {
        return printJobRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrintJobResponseDTO findByIdDetailed(UUID id) {
        return mapToResponseDTO(findEntityById(id));
    }

    public PrintJob findEntityById(UUID id) {
        return printJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabalho de impressão não encontrado"));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do PrintJob ID: {}", id);

        PrintJob job = findEntityById(id);

        if (job.getStatus() != PrintJobStatus.QUEUED && job.getStatus() != PrintJobStatus.CANCELED) {
            log.warn("Tentativa de exclusão negada: PrintJob {} em status {}", id, job.getStatus());
            throw new RuntimeException("Não é possível deletar um trabalho que já saiu da fila.");
        }

        try {
            printJobRepository.delete(job);
            log.info("PrintJob {} removido com sucesso", id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Erro de integridade ao deletar PrintJob {}", id);
            throw e;
        }
    }

    private PrintJobResponseDTO mapToResponseDTO(PrintJob job) {
        long hours = job.getEstimatedTime().toHours();
        int minutes = job.getEstimatedTime().toMinutesPart();
        String formattedTime = String.format("%02d:%02d:00", hours, minutes);

        return new PrintJobResponseDTO(
                job.getId(),
                formattedTime,
                job.getStatus().name(),
                job.getStatus().getDescription(),
                job.getOrder().getId(),
                job.getPrinter().getModelName(),
                job.getModel().getFileName()
        );
    }
}
