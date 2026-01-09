package com.off3d.studio.sales.service;

import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.domain.Order;
import com.off3d.studio.sales.domain.OrderStatus;
import com.off3d.studio.sales.dto.OrderRequestDTO;
import com.off3d.studio.sales.dto.OrderResponseDTO;
import com.off3d.studio.sales.repository.CustomerRepository;
import com.off3d.studio.sales.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public OrderResponseDTO save(OrderRequestDTO dto) {
        log.info("Criando novo pedido para o cliente ID: {}", dto.customerId());

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + dto.customerId()));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(dto.totalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(customer);

        Order savedOrder = orderRepository.save(order);
        return mapToResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true) // Alterado para readOnly = true por ser busca
    public OrderResponseDTO findById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));

        return mapToResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO update(UUID id, OrderRequestDTO dto) {
        log.info("Atualizando pedido ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + dto.customerId()));

        order.setTotalPrice(dto.totalPrice());
        order.setCustomer(customer);

        Order updateOrder = orderRepository.save(order);
        return mapToResponseDTO(updateOrder);
    }

    @Transactional
    public OrderResponseDTO startProduction(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(OrderStatus.IN_PROGRESS);
        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO completeOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        order.setStatus(OrderStatus.COMPLETED);
        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Não é possível cancelar um pedido já concluído.");
        }

        order.setStatus(OrderStatus.CANCELED);
        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do pedido ID: {}", id);

        if (!orderRepository.existsById(id)) {
            log.error("Erro: Pedido {} não encontrado para exclusão", id);
            throw new RuntimeException("Pedido não encontrado");
        }

        try {
            orderRepository.deleteById(id);
            log.info("Pedido {} removido com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha: Pedido {} possui vínculos (como PrintJobs) e não pode ser deletado", id);
            throw e;
        }
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalPrice(),
                order.getStatus()
        );
    }
}