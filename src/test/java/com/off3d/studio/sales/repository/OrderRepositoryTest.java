package com.off3d.studio.sales.repository;

import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.domain.Order;
import com.off3d.studio.utils.JsonHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Deve buscar todos os Orders de um Customer específico usando JSONs")
    void shouldFindAllOrdersByCustomerFromJson() {
        Customer customerTransient = JsonHandler.getCustomerAsEntity();

        // 2. FORÇAR ID NULO PARA GERAR UM NOVO
        customerTransient.setId(null);

        final Customer managedCustomer = entityManager.persist(customerTransient);
        entityManager.flush();

        List<Order> ordersFromJson = JsonHandler.getOrdersAsList();

        ordersFromJson.forEach(order -> {
            order.setCustomer(managedCustomer);
            order.setOrderDate(LocalDateTime.now());
            entityManager.persist(order);
        });

        entityManager.flush();

        // Acao: Buscar usando o repositório
        List<Order> foundOrders = orderRepository.findByCustomerId(managedCustomer.getId());

        // Verificacao
        assertEquals(2, foundOrders.size());
        assertEquals(managedCustomer.getName(), foundOrders.get(0).getCustomer().getName());
    }
}