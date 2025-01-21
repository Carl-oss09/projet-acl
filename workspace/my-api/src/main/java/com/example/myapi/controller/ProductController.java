package com.example.myapi.controller;

import com.example.myapi.model.Product;
import com.example.myapi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Date;
import java.util.List;



@RestController
@RequestMapping("/api/products")
@Tag(name = "Product API", description = "API for managing products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Retrieve all products from the database")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<Product> addProducts(@RequestBody Product p) {
        Product newP = productService.addProducts(p);
        return new ResponseEntity<>(newP, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Product existingProduct = productService.getProductById(id);
        if (existingProduct != null) {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setPrice(updatedProduct.getPrice());
            productService.updateProduct(existingProduct);  // Appelle le service pour sauvegarder le produit modifié
            return ResponseEntity.ok("Produit mis à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produit non trouvé");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProducts(id);
        return ResponseEntity.ok("Produit supprimé");
    }

}


