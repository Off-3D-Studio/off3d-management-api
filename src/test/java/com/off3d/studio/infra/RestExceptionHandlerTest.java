package com.off3d.studio.infra;

import com.off3d.studio.auth.repository.UserRepository;
import com.off3d.studio.auth.security.TokenService;
import com.off3d.studio.sales.controller.OrderController;
import com.off3d.studio.sales.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve retornar status 400 quando uma RuntimeException for lançada")
    void shouldReturnBadRequestOnRuntimeException() throws Exception {
        UUID id = UUID.randomUUID();

        when(orderService.findById(id)).thenThrow(new RuntimeException("Pedido não encontrado"));

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Pedido não encontrado"));
    }
}