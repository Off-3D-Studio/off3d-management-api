package com.off3d.studio.manufacturing.repository;

import com.off3d.studio.manufacturing.domain.PrintJob;
import com.off3d.studio.manufacturing.domain.PrintJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrintJobRepository extends JpaRepository<PrintJob, UUID> {

    // Busca todos os trabalhos que estão com um status específico (ex: todos na fila)
    List<PrintJob> findByStatus(PrintJobStatus status);

    // Busca todos os trabalhos vinculados a um pedido específico
    List<PrintJob> findByOrderId(UUID orderId);

    // Busca todos os trabalhos feitos por uma impressora específica
    List<PrintJob> findByPrinterId(UUID printerId);
}
