package com.off3d.studio.sales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.off3d.studio.auth.repository.UserRepository;
import com.off3d.studio.auth.security.TokenService;
import com.off3d.studio.sales.domain.OrderStatus;
import com.off3d.studio.sales.dto.OrderRequestDTO;
import com.off3d.studio.sales.dto.OrderResponseDTO;
import com.off3d.studio.sales.service.OrderService;
import com.off3d.studio.utils.JsonHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve criar um pedido com sucesso utilizando JSONHandler")
    void shouldCreateOrder() throws Exception {
        OrderRequestDTO request = JsonHandler.getOrderRequest();

        OrderResponseDTO response = new OrderResponseDTO(
                UUID.randomUUID(),
                LocalDateTime.now(),
                request.totalPrice(),
                OrderStatus.PENDING
        );

        when(orderService.save(any(OrderRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.totalPrice").value(request.totalPrice().doubleValue()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Deve iniciar produção do pedido usando status do JSON")
    void shouldStartProduction() throws Exception {
        UUID orderId = UUID.randomUUID();

        String statusFromJson = JsonHandler.getOrderStatusFromJson();

        OrderResponseDTO response = new OrderResponseDTO(
                orderId,
                LocalDateTime.now(),
                new BigDecimal("150.00"),
                OrderStatus.valueOf(statusFromJson)
        );

        when(orderService.startProduction(orderId)).thenReturn(response);

        mockMvc.perform(patch("/orders/{id}/start", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value(statusFromJson));
    }
}