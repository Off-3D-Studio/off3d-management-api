package com.off3d.studio.sales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.off3d.studio.auth.security.TokenService;
import com.off3d.studio.auth.repository.UserRepository;
import com.off3d.studio.sales.dto.CustomerRequestDTO;
import com.off3d.studio.sales.dto.CustomerResponseDTO;
import com.off3d.studio.sales.service.CustomerService;
import com.off3d.studio.utils.JsonHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve retornar 201 Created ao salvar um novo cliente")
    void shouldReturnCreatedWhenSavingNewCustomer() throws Exception {
        // Cenario
        CustomerRequestDTO dto = JsonHandler.getCustomerRequest();

        // Objeto de resposta preenchido
        CustomerResponseDTO responseDTO = new CustomerResponseDTO(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                dto.phone(),
                Set.of()
        );

        // Garante que o mock do service retorne o objeto preenchido
        when(customerService.save(any(CustomerRequestDTO.class))).thenReturn(responseDTO);

        // Acao e Verificacao
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.email()));
    }
}