package com.example.myapi.controller;

import com.example.myapi.model.Etudiant;
import com.example.myapi.service.EtudiantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
@Tag(name = "Etudiant API", description = "API for managing students")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @Operation(summary = "Get all students", description = "Retrieve all students from the database")
    @GetMapping
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        return ResponseEntity.ok(etudiantService.getAllEtudiants());
    }

    @PostMapping
    public ResponseEntity<Etudiant> addEtudiant(@RequestBody Etudiant etudiant) {
        System.out.println("Etudiant reçu : " + etudiant.getPrenom() + " " + etudiant.getNom());
        Etudiant newEtudiant = etudiantService.addEtudiant(etudiant);
        return new ResponseEntity<>(newEtudiant, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        if (etudiant != null) {
            return ResponseEntity.ok(etudiant);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEtudiant(@PathVariable Long id, @RequestBody Etudiant updatedEtudiant) {
        Etudiant existingEtudiant = etudiantService.getEtudiantById(id);
        if (existingEtudiant != null) {
            existingEtudiant.setNom(updatedEtudiant.getNom());
            existingEtudiant.setPrenom(updatedEtudiant.getPrenom());
            existingEtudiant.setL1(updatedEtudiant.isL1());
            existingEtudiant.setL2(updatedEtudiant.isL2());
            existingEtudiant.setL3(updatedEtudiant.isL3());
            etudiantService.updateEtudiant(existingEtudiant);
            return ResponseEntity.ok("Etudiant mis à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Etudiant non trouvé");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEtudiant(@PathVariable Long id) {
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.ok("Etudiant supprimé");
    }
}