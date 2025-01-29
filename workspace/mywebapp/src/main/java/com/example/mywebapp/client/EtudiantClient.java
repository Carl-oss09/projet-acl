package com.example.mywebapp.client;

import com.example.mywebapp.model.Etudiant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "etudiantClient", url = "http://my-api:8080")
public interface EtudiantClient {

    @GetMapping("/api/etudiants")
    List<Etudiant> getAllEtudiants();

    @PostMapping("/api/etudiants")
    void addEtudiant(@RequestBody Etudiant etudiant);

    @DeleteMapping("/api/etudiants/{id}")
    void deleteEtudiant(@PathVariable("id") Long id);

    @PostMapping("/api/etudiants")
    Long createEtudiantAndGetId(@RequestBody Etudiant etudiant);


    @PutMapping("/api/etudiants/{id}")
    void updateEtudiant(@PathVariable("id") Long id, @RequestBody Etudiant etudiant);

    @GetMapping("/api/etudiants/{id}")
    Etudiant getEtudiantById(@PathVariable("id") Long id);
}