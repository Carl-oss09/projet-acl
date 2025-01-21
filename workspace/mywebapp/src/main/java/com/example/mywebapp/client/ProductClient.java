package com.example.mywebapp.client;

import com.example.mywebapp.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "productClient", url = "http://localhost:8080")
public interface ProductClient {
    @GetMapping("/api/products")
    List<Product> getAllProducts();

    @PostMapping("/api/products")
    void addProduct(@RequestBody Product product);

    @DeleteMapping("/api/products/{id}")
    void deleteProduct(@PathVariable("id") Long id);

    @PutMapping("/api/products/{id}")
    void updateProduct(@PathVariable("id") Long id, @RequestBody Product product);


}
