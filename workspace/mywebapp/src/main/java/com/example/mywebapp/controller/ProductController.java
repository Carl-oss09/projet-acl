package com.example.mywebapp.controller;
import com.example.mywebapp.client.ProductClient;
import com.example.mywebapp.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductClient productClient;

    @GetMapping("/products")
    public String getProducts(Model model) {
        List<Product> products = productClient.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("product", new Product()); // Ajoute un produit vide pour le formulaire
        return "products";  // Retourne le template products.html
    }

    // Traite l'ajout d'un produit
    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product) {
        productClient.addProduct(product);  // Ajoute le produit via l'API
        return "redirect:/products";  // Redirige vers la liste pour afficher la mise à jour
    }

    @PostMapping("/products/update")
    public String updateProduct(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("price") Double price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        productClient.updateProduct(id, product);

        return "redirect:/products";
    }

    @PostMapping("/products/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/products?error=Fichier vide";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            List<Product> products = new ArrayList<>();
            reader.readLine(); // Skip the header line

            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length >= 2) {
                    Product product = new Product();
                    product.setName(fields[0]);
                    product.setPrice(Double.parseDouble(fields[1]));
                    products.add(product);
                }
            }

            // Appel à l'API pour ajouter tous les produits
            for (Product product : products) {
                productClient.addProduct(product);
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            return "redirect:/products?error=Erreur lors de l'importation";
        }

        return "redirect:/products";
    }

   // @PostMapping("/products/delete-selected")
   // public String deleteSelectedProducts(@RequestParam List<Long> productIds) {
   //     for (Long id : productIds) {
    //        productClient.deleteProducts(id);  // Suppression de chaque produit sélectionné
     //   }
     //   return "redirect:/products";
    //}

    @GetMapping("/products/export")
    public ResponseEntity<String> exportProducts() {
        List<Product> products = productClient.getAllProducts();

        StringBuilder csvData = new StringBuilder();
        csvData.append("Name,Price\n"); // En-tête CSV
        for (Product product : products) {
            csvData.append(product.getName()).append(",")
                    .append(product.getPrice()).append("\n");
        }

        // Crée la réponse avec le CSV
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData.toString());
    }


    @PostMapping("/products/delete")
    public String deleteProduct(@ModelAttribute Product product) {
        productClient.deleteProduct(product.getId());

        return "redirect:/products";
    }
}