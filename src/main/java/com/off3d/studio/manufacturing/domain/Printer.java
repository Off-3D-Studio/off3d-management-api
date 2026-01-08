package com.off3d.studio.manufacturing.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_PRINTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Printer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String modelName;

    @Enumerated(EnumType.STRING)
    private PrinterTechnology technology;

    @Enumerated(EnumType.STRING)
    private PrinterStatus status;

    @OneToMany(mappedBy = "printer", fetch = FetchType.LAZY)
    private Set<PrintJob> printJobs = new HashSet<>();
}
