package com.off3d.studio.sales.repository;

import com.off3d.studio.sales.domain.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Deve salvar e buscar um Customer pelo ID")
    void shouldSaveAndFindCustomerById() {
        Customer customer = new Customer();
        customer.setName("Maria Silva");
        customer.setEmail("maria@email.com");
        customer.setPhone("11999999999");

        // Salva usando o EntityManager
        entityManager.persist(customer);
        entityManager.flush();

        Optional<Customer> foundCustomer = customerRepository.findById(customer.getId());

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getEmail(), foundCustomer.get().getEmail());
    }

    @Test
    @DisplayName("Deve deletar um Customer existente")
    void shouldDeleteCustomer() {
        Customer customer = new Customer();
        customer.setName("João Souza");
        customer.setEmail("joao@email.com");
        customer.setPhone("11888888888");

        entityManager.persist(customer);
        entityManager.flush();

        customerRepository.deleteById(customer.getId());

        Optional<Customer> foundCustomer = customerRepository.findById(customer.getId());
        assertFalse(foundCustomer.isPresent());
    }
}