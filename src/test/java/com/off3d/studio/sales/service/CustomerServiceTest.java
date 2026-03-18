package com.off3d.studio.sales.service;

import com.off3d.studio.auth.domain.User;
import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.dto.CustomerRequestDTO;
import com.off3d.studio.sales.dto.CustomerResponseDTO;
import com.off3d.studio.sales.repository.CustomerRepository;
import com.off3d.studio.utils.JsonHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock private CustomerRepository customerRepository;
    @InjectMocks private CustomerService customerService;

    private void mockSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User mockUser = JsonHandler.getUserAsEntity();
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    @DisplayName("Deve salvar um novo Customer com sucesso e associar ao usuário do JSON")
    void shouldSaveCustomer() {
        mockSecurityContext();
        CustomerRequestDTO dto = JsonHandler.getCustomerRequest();

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        Customer savedCustomer = new Customer();
        savedCustomer.setId(UUID.randomUUID());
        savedCustomer.setName(dto.name());
        savedCustomer.setCreatedBy(JsonHandler.getUserAsEntity());

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponseDTO response = customerService.save(dto);

        assertNotNull(response);

        verify(customerRepository).save(customerCaptor.capture());
        Customer customerToSave = customerCaptor.getValue();
        assertEquals("user@test.com", customerToSave.getCreatedBy().getEmail());
    }

    @Test
    @DisplayName("Deve atualizar um Customer existente com sucesso")
    void shouldUpdateCustomer() {
        UUID customerId = UUID.randomUUID();
        CustomerRequestDTO dto = JsonHandler.getCustomerRequest();

        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("Nome Antigo");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        CustomerResponseDTO response = customerService.update(customerId, dto);

        assertNotNull(response);
        assertEquals(dto.name(), response.name());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Deve atualizar Customer se e-mail já existir (Upsert)")
    void shouldUpsertCustomerWhenEmailExists() {
        CustomerRequestDTO dto = JsonHandler.getCustomerRequest();

        Customer existingCustomer = new Customer();
        existingCustomer.setId(UUID.randomUUID());
        existingCustomer.setEmail(dto.email());
        existingCustomer.setName("Nome Antigo");

        when(customerRepository.findByEmail(dto.email())).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(existingCustomer);

        CustomerResponseDTO response = customerService.upsert(dto);

        assertNotNull(response);
        assertEquals(dto.name(), response.name());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}