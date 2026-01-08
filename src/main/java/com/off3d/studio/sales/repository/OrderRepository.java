package com.off3d.studio.sales.repository;

import com.off3d.studio.sales.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    // Métodos adicionais podem ser criados aqui, como:
    // List<Order> findByCustomerId(UUID customerId);
}