package com.off3d.studio.sales.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.infra.exceptions.BusinessException;
import com.off3d.studio.infra.exceptions.ResourceNotFoundException;
import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.domain.Order;
import com.off3d.studio.sales.domain.OrderStatus;
import com.off3d.studio.sales.dto.OrderRequestDTO;
import com.off3d.studio.sales.dto.OrderResponseDTO;
import com.off3d.studio.sales.repository.CustomerRepository;
import com.off3d.studio.sales.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.off3d.studio.infra.config.ErrorMessages.CUSTOMER_NOT_FOUND;
import static com.off3d.studio.infra.config.ErrorMessages.ORDER_NOT_FOUND_ID;

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
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Sócio [{}] - ID: {} criando pedido para cliente: {}",
                currentUser.getName(), currentUser.getId(), dto.customerId());

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND + dto.customerId()));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(dto.totalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(customer);
        order.setCreatedBy(currentUser);

        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll() {
        log.info("Buscando todos os pedidos");
        return orderRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(UUID id) {
        return orderRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_ID + id));
    }

    @Transactional
    public OrderResponseDTO update(UUID id, OrderRequestDTO dto) {
        log.info("Atualizando pedido ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_ID + id));

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER_NOT_FOUND + dto.customerId()));

        order.setTotalPrice(dto.totalPrice());
        order.setCustomer(customer);

        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO startProduction(UUID id) {
        log.info("Iniciando produção do pedido ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_ID + id));

        order.setStatus(OrderStatus.IN_PROGRESS);
        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO completeOrder(UUID id) {
        log.info("Finalizando pedido ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_ID + id));

        order.setStatus(OrderStatus.COMPLETED);
        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_ID + id));

        if (OrderStatus.COMPLETED.equals(order.getStatus())) {
            throw new BusinessException("Não é possível cancelar um pedido já concluído.");
        }

        order.setStatus(OrderStatus.CANCELED);
        return mapToResponseDTO(orderRepository.save(order));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Iniciando exclusão do pedido ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND_ID + id));

        try {
            orderRepository.delete(order);
            log.info("Pedido {} removido com sucesso", id);
        } catch (DataIntegrityViolationException e) {
            log.warn("Falha: Pedido {} possui vínculos ativos", id);
            throw new BusinessException("O pedido possui registros vinculados e não pode ser deletado.");
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