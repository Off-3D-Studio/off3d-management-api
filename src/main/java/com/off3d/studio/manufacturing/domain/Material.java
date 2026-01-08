package com.off3d.studio.manufacturing.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_MATERIAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String color;

    private String brand;

    @Column(nullable = false)
    private Double weightGrams;

    @Enumerated(EnumType.STRING)
    private MaterialType type;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    private Set<PrintJob> printJobs = new HashSet<>();
}
