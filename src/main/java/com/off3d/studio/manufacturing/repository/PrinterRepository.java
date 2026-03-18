package com.off3d.studio.manufacturing.repository;

import com.off3d.studio.manufacturing.domain.Printer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, UUID> {
}