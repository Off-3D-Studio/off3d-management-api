package com.off3d.studio.manufacturing.domain;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.sales.domain.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "TB_PRINT_JOB")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PrintJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PrintJobStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model3D model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "printer_id")
    private Printer printer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    private Duration estimatedTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}