package com.example.mywebapp.client;

import com.example.mywebapp.model.Formateur;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "formateurClient", url = "http://localhost:8080")
public interface FormateurClient {

    @GetMapping("/api/formateurs")
    List<Formateur> getAllFormateurs();

    @PostMapping("/api/formateurs")
    void addFormateur(@RequestBody Formateur formateur);

    @DeleteMapping("/api/formateurs/{id}")
    void deleteFormateur(@PathVariable("id") Long id);

    @PutMapping("/api/formateurs/{id}")
    void updateFormateur(@PathVariable("id") Long id, @RequestBody Formateur formateur);

    @GetMapping("/api/formateurs/{id}")
    Formateur getFormateurById(@PathVariable("id") Long id);
}