package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomerDTO;
import com.example.ecommerce.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Devrait retourner tous les clients")
    public void shouldGetAllCustomers() throws Exception {
        CustomerDTO dto = CustomerDTO.builder().id(1L).firstName("John").lastName("Doe").build();
        Mockito.when(customerService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @DisplayName("Devrait créer un nouveau client")
    public void shouldCreateCustomer() throws Exception {
        CustomerDTO input = CustomerDTO.builder().firstName("Jane").build();
        CustomerDTO output = CustomerDTO.builder().id(2L).firstName("Jane").build();
        
        Mockito.when(customerService.create(any(CustomerDTO.class))).thenReturn(output);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }
}
