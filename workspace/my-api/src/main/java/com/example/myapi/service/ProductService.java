package com.example.myapi.service;

import com.example.myapi.model.Product;
import com.example.myapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    //public Product getById(Long id){}

    public Product addProducts(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public void updateProduct(Product product) {
        productRepository.save(product);  // `save` peut également mettre à jour l'entité si l'ID existe déjà
    }

    public void deleteProducts(Long id) {
        productRepository.deleteById(id);
    }


}
