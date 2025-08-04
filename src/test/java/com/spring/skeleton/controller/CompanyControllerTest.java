package com.spring.skeleton.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.skeleton.model.Company;
import com.spring.skeleton.service.CompanyService;
import com.spring.skeleton.service.CustomAuthenticationManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CompanyService companyService;

    @MockitoBean
    private CustomAuthenticationManager authManager;

    private final String name = "Test Company";
    private final String phone = "1234567890";
    private final String email = "test@exakmple.com";
    private final String password = "password123";

    @Test
    void create() throws Exception {
        CompanyController.Body body = new CompanyController.Body();
        body.setName(name);
        body.setPhone(phone);
        body.setEmail(email);
        body.setPassword(password);

        Company mockCompany = new Company(1L, name, email, phone);

        when(companyService.create(name, phone, email, password))
                .thenReturn(mockCompany);

        mockMvc.perform(post("/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email));
    }
}
