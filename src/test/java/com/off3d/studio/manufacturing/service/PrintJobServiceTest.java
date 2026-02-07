package com.off3d.studio.manufacturing.service;

import com.off3d.studio.auth.service.AuthService;
import com.off3d.studio.manufacturing.domain.PrintJob;
import com.off3d.studio.manufacturing.domain.PrintJobStatus;
import com.off3d.studio.manufacturing.dto.PrintJobRequestDTO;
import com.off3d.studio.manufacturing.dto.PrintJobResponseDTO;
import com.off3d.studio.manufacturing.repository.*;
import com.off3d.studio.sales.domain.Order;
import com.off3d.studio.sales.repository.OrderRepository;
import com.off3d.studio.utils.JsonHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrintJobServiceTest {

    @Mock private PrintJobRepository printJobRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private PrinterRepository printerRepository;
    @Mock private MaterialRepository materialRepository;
    @Mock private Model3DRepository modelRepository;
    @Mock private AuthService authService;

    @InjectMocks
    private PrintJobService printJobService;

    private UUID jobId;
    private PrintJob printJob;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
        printJob = new PrintJob();
        printJob.setId(jobId);
        printJob.setStatus(PrintJobStatus.QUEUED);
        printJob.setEstimatedTime(Duration.ofHours(2));
    }

    @Test
    @DisplayName("Deve criar um PrintJob com sucesso usando JSONs")
    void shouldCreatePrintJobWithJson() {
        // 1. DADOS DE ENTRADA E CONTEXTO (Dados que vêm do front-end e sessão)
        PrintJobRequestDTO dto = JsonHandler.getPrintJobRequest();

        // Configura o usuário autenticado que vem do JSON de usuário
        when(authService.getCurrentUser()).thenReturn(JsonHandler.getUserAsEntity());

        // --- CORREÇÃO: Carrega as entidades de domínio dos arquivos JSON ---
        // IMPORTANTE: Certifique-se que os IDs nos arquivos JSON são válidos (hexadecimais)
        var printerEntity = JsonHandler.getPrinterAsEntity();
        var materialEntity = JsonHandler.getMaterialAsEntity();
        var model3DEntity = JsonHandler.getModel3DAsEntity();
        // ------------------------------------------------------------------

        // 2. CONFIGURAÇÃO DOS MOCKS (Simula o comportamento do banco de dados)
        Order order = new Order();
        order.setId(UUID.randomUUID());

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // --- CORREÇÃO: Retorna as instâncias carregadas do JSON ---
        when(printerRepository.findById(any())).thenReturn(Optional.of(printerEntity));
        when(materialRepository.findById(any())).thenReturn(Optional.of(materialEntity));
        when(modelRepository.findById(any())).thenReturn(Optional.of(model3DEntity));

        // 3. ENTIDADE DE SAÍDA (O que o serviço processa e retorna ao salvar)
        PrintJob savedJob = new PrintJob();
        savedJob.setId(UUID.randomUUID());
        savedJob.setStatus(PrintJobStatus.QUEUED);
        savedJob.setEstimatedTime(Duration.ofHours(3).plusMinutes(45));
        savedJob.setOrder(order);

        // --- CORREÇÃO: Associa as instâncias únicas para evitar NullPointerException ---
        // O mapper tentará acessar getPrinter().getModelName(), etc.
        savedJob.setPrinter(printerEntity);
        savedJob.setMaterial(materialEntity);
        savedJob.setModel(model3DEntity);
        // -------------------------------------------------------------------------------

        // Define que quando o save for chamado, retorna o objeto completo
        when(printJobRepository.save(any(PrintJob.class))).thenReturn(savedJob);

        // Acao
        PrintJobResponseDTO response = printJobService.createPrintJob(dto);

        // Verificacao
        assertNotNull(response);
        // Verifique se os dados mapeados condizem com o carregado no savedJob
        assertEquals("03:45:00", response.estimatedTime());
        assertEquals(PrintJobStatus.QUEUED.name(), response.status());
    }

    @Test
    @DisplayName("Deve lançar erro ao deletar PrintJob em andamento")
    void shouldThrowExceptionWhenDeletingPrintingJob() {
        // Cenario
        printJob.setStatus(PrintJobStatus.PRINTING);
        when(printJobRepository.findById(jobId)).thenReturn(Optional.of(printJob));

        // Acao & Verificacao
        Exception exception = assertThrows(RuntimeException.class, () -> {
            printJobService.delete(jobId);
        });

        assertEquals("Não é possível deletar um trabalho que já saiu da fila.", exception.getMessage());
        verify(printJobRepository, never()).delete(any());
    }
}