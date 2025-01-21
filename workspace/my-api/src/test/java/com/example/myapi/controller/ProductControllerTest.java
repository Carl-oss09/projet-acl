package com.example.myapi.controller;

import com.example.myapi.model.Product;
import com.example.myapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Données fictives
        List<Product> mockProducts = Arrays.asList(
                new Product("1L", 12, new Date(2024,12,03)),
                new Product("2L", 34, new Date(2023,06,02))
        );

        // Simulation du comportement du service
        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Appel de la méthode du contrôleur et vérification
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Vérifie que le statut HTTP est 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Vérifie que le type de contenu est JSON
                .andExpect(jsonPath("$[0].name").value("1L")) // Vérifie que le premier produit a le nom "Product 1"
                .andExpect(jsonPath("$[1].name").value("2L")); // Vérifie que le deuxième produit a le nom "Product 2"
    }
}