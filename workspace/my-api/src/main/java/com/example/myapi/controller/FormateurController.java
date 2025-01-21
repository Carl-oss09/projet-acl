package com.example.myapi.controller;

import com.example.myapi.model.Formateur;
import com.example.myapi.service.FormateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@Tag(name = "Formateur API", description = "API for managing instructors")
public class FormateurController {

    private final FormateurService formateurService;

    public FormateurController(FormateurService formateurService) {
        this.formateurService = formateurService;
    }

    @Operation(summary = "Get all instructors", description = "Retrieve all instructors from the database")
    @GetMapping
    public ResponseEntity<List<Formateur>> getAllFormateurs() {
        return ResponseEntity.ok(formateurService.getAllFormateurs());
    }

    @PostMapping
    public ResponseEntity<Formateur> addFormateur(@RequestBody Formateur formateur) {
        System.out.println("Formateur reçu : " + formateur.getPrenom() + " " + formateur.getNom());
        Formateur newFormateur = formateurService.addFormateur(formateur);
        return new ResponseEntity<>(newFormateur, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Formateur> getFormateurById(@PathVariable Long id) {
        Formateur formateur = formateurService.getFormateurById(id);
        if (formateur != null) {
            return ResponseEntity.ok(formateur);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateFormateur(@PathVariable Long id, @RequestBody Formateur updatedFormateur) {
        Formateur existingFormateur = formateurService.getFormateurById(id);
        if (existingFormateur != null) {
            existingFormateur.setNom(updatedFormateur.getNom());
            existingFormateur.setPrenom(updatedFormateur.getPrenom());
            existingFormateur.setL1(updatedFormateur.isL1());
            existingFormateur.setL2(updatedFormateur.isL2());
            existingFormateur.setL3(updatedFormateur.isL3());
            formateurService.updateFormateur(existingFormateur);
            return ResponseEntity.ok("Formateur mis à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Formateur non trouvé");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFormateur(@PathVariable Long id) {
        formateurService.deleteFormateur(id);
        return ResponseEntity.ok("Formateur supprimé");
    }
}