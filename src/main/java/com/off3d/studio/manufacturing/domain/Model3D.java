package com.off3d.studio.manufacturing.domain;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.sales.domain.Customer;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_MODEL_3D")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Model3D {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fileName;

    private String filePath;

    private Double volumeCm3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "model", fetch = FetchType.LAZY)
    private Set<PrintJob> printJobs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
}
