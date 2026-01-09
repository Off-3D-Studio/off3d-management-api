package com.off3d.studio.sales.service;

import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.dto.CustomerRequestDTO;
import com.off3d.studio.sales.dto.CustomerResponseDTO;
import com.off3d.studio.sales.dto.OrderResponseDTO;
import com.off3d.studio.sales.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponseDTO save(CustomerRequestDTO dto) {
        log.info("Cadastrando novo cliente: {}", dto.name());

        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponseDTO(savedCustomer, false);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(customer -> mapToResponseDTO(customer, false))
                .toList();
    }

    public Customer findById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO findByIdDetailed(UUID id) {
        Customer customer = findById(id);
        return mapToResponseDTO(customer, true);
    }

    @Transactional
    public CustomerResponseDTO update(UUID id, CustomerRequestDTO dto) {
        log.info("Atualizando dados do cliente ID: {}", id);

        Customer customer = findById(id);
        customer.setName(dto.name());
        customer.setEmail(dto.email());
        customer.setPhone(dto.phone());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponseDTO(updatedCustomer, true);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do cliente ID: {}", id);

        if (!customerRepository.existsById(id)) {
            log.error("Erro: Cliente {} não encontrado para exclusão", id);
            throw new RuntimeException("Cliente não encontrado");
        }

        try {
            customerRepository.deleteById(id);
            log.info("Cliente {} removido com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha de segurança: Cliente {} possui pedidos vinculados e não pode ser deletado", id);
            throw e;
        }
    }

    private CustomerResponseDTO mapToResponseDTO(Customer customer, boolean detailed) {
        Set<OrderResponseDTO> orderDTOs = Set.of();

        if (detailed && customer.getOrders() != null) {
            orderDTOs = customer.getOrders().stream()
                    .map(order -> new OrderResponseDTO(
                            order.getId(),
                            order.getOrderDate(),
                            order.getTotalPrice(),
                            order.getStatus()
                    ))
                    .collect(Collectors.toSet());
        }

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                orderDTOs
        );
    }
}