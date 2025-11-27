package com.bnpp.kata.bookdiscount.app.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
class BookPriceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCalculatePriceEndpointSuccess() throws Exception {
        Map<String, Object> request = new HashMap<>();
        Map<String, Integer> items = new HashMap<>();
        items.put("Clean Code", 1);
        items.put("The Clean Coder", 1);
        request.put("items", items);

        mockMvc.perform(post("/api/price/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice", is(95.0)));
    }

    @Test
    void testCalculatePriceEndpointValidationError() throws Exception {
        Map<String, Object> request = new HashMap<>();
        // missing items field
        mockMvc.perform(post("/api/price/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCalculatePriceEndpointNegativeQuantity() throws Exception {
        Map<String, Object> request = new HashMap<>();
        Map<String, Integer> items = new HashMap<>();
        items.put("Clean Code", -1);
        request.put("items", items);
        mockMvc.perform(post("/api/price/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
