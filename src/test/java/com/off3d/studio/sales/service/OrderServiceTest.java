package com.off3d.studio.sales.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.domain.Order;
import com.off3d.studio.sales.domain.OrderStatus;
import com.off3d.studio.sales.dto.OrderRequestDTO;
import com.off3d.studio.sales.dto.OrderResponseDTO;
import com.off3d.studio.sales.repository.CustomerRepository;
import com.off3d.studio.sales.repository.OrderRepository;
import com.off3d.studio.utils.JsonHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User mockUser = JsonHandler.getUserAsEntity();
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    @DisplayName("Deve salvar um novo pedido com sucesso")
    void shouldSaveOrder() {
        mockSecurityContext();
        OrderRequestDTO dto = JsonHandler.getOrderRequest();
        Customer customer = new Customer();
        customer.setId(dto.customerId());

        when(customerRepository.findById(dto.customerId())).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderResponseDTO response = orderService.save(dto);

        assertNotNull(response);
        assertEquals(dto.totalPrice(), response.totalPrice());
        assertEquals(OrderStatus.PENDING, response.status());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve iniciar a produção do pedido (status: IN_PROGRESS)")
    void shouldStartProduction() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        OrderResponseDTO response = orderService.startProduction(orderId);

        assertEquals(OrderStatus.IN_PROGRESS, response.status());
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Não deve cancelar o pedido se ele já estiver concluído")
    void shouldNotCancelCompletedOrder() {
        UUID orderId = UUID.randomUUID();

        Order order = JsonHandler.getOrderAsEntity();
        order.setId(orderId);
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(orderId);
        });

        assertEquals("Não é possível cancelar um pedido já concluído.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException ao deletar pedido com vínculos")
    void shouldThrowExceptionWhenDeletingOrderWithDependencies() {
        UUID orderId = JsonHandler.getOrderIdToDelete();

        when(orderRepository.existsById(orderId)).thenReturn(true);
        // Simula a falha de integridade
        doThrow(new DataIntegrityViolationException("Erro de integridade"))
                .when(orderRepository).deleteById(orderId);

        assertThrows(DataIntegrityViolationException.class, () -> {
            orderService.delete(orderId);
        });

        verify(orderRepository, times(1)).deleteById(orderId);
    }
}