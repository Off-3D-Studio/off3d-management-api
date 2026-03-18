package com.off3d.studio.manufacturing.repository;

import com.off3d.studio.manufacturing.domain.PrintJob;
import com.off3d.studio.manufacturing.domain.PrintJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrintJobRepository extends JpaRepository<PrintJob, UUID> {

    List<PrintJob> findByStatus(PrintJobStatus status);

    List<PrintJob> findByOrderId(UUID orderId);

    List<PrintJob> findByPrinterId(UUID printerId);
}
