package com.off3d.studio.sales.repository;

import com.off3d.studio.sales.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    // O JpaRepository já nos dá o findById(UUID id) por padrão
}