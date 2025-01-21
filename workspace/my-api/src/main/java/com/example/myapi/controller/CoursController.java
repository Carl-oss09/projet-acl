package com.example.myapi.controller;

import com.example.myapi.model.Cours;
import com.example.myapi.service.CoursService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
@Tag(name = "Cours API", description = "API for managing courses")
public class CoursController {

    private final CoursService coursService;

    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    /**
     * Recherche des cours par date et/ou matière.
     */
    @Operation(summary = "Search courses", description = "Retrieve courses by date and/or subject")
    @GetMapping("/search")
    public ResponseEntity<List<Cours>> rechercherCours(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String matiere
    ) {
        List<Cours> coursList;

        // Vérifiez si les paramètres sont vides ou null
        boolean isDateEmpty = (date == null || date.trim().isEmpty());
        boolean isMatiereEmpty = (matiere == null || matiere.trim().isEmpty() || matiere.equalsIgnoreCase("Toutes"));

        if (isDateEmpty && isMatiereEmpty) {
            // Cas 1 : Aucun filtre, retourner tous les cours
            coursList = coursService.getAllCours();
        } else if (isDateEmpty) {
            // Cas 2 : Pas de date, filtrer uniquement par matière
            coursList = coursService.rechercherCoursParMatiere(matiere);
        } else if (isMatiereEmpty) {
            // Cas 3 : Pas de matière ou matière = "Toutes", filtrer uniquement par date
            coursList = coursService.rechercherCoursParDate(date);
        } else {
            // Cas 4 : Date et matière spécifiées, appliquer les deux filtres
            coursList = coursService.rechercherCoursParDateEtMatiere(date, matiere);
        }

        return ResponseEntity.ok(coursList);
    }

    /**
     * Récupère tous les cours.
     */
    @Operation(summary = "Get all courses", description = "Retrieve all courses from the database")
    @GetMapping
    public ResponseEntity<List<Cours>> getAllCours() {
        List<Cours> coursList = coursService.getAllCours();
        return ResponseEntity.ok(coursList);
    }

    /**
     * Ajoute un nouveau cours.
     */
    @Operation(summary = "Add a new course", description = "Add a new course to the database")
    @PostMapping
    public ResponseEntity<Cours> addCours(@RequestBody Cours cours) {
        Cours newCours = coursService.addCours(cours);
        return new ResponseEntity<>(newCours, HttpStatus.CREATED);
    }

    /**
     * Récupère un cours par ID.
     */
    @Operation(summary = "Get course by ID", description = "Retrieve a course by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<Cours> getCoursById(@PathVariable Long id) {
        Cours cours = coursService.getCoursById(id);
        if (cours != null) {
            return ResponseEntity.ok(cours);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Met à jour un cours par ID.
     */
    @Operation(summary = "Update a course", description = "Update an existing course by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCours(@PathVariable Long id, @RequestBody Cours updatedCours) {
        Cours existingCours = coursService.getCoursById(id);
        if (existingCours != null) {
            existingCours.setMatiere(updatedCours.getMatiere());
            existingCours.setTitre(updatedCours.getTitre());
            existingCours.setDescription(updatedCours.getDescription());
            existingCours.setDate(updatedCours.getDate());
            existingCours.setAprem_matin(updatedCours.isAprem_matin());
            existingCours.setFormateurId(updatedCours.getFormateurId());
            existingCours.setNb_eleves_max(updatedCours.getNb_eleves_max());
            coursService.updateCours(existingCours);
            return ResponseEntity.ok("Cours mis à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cours non trouvé");
        }
    }

    /**
     * Supprime un cours par ID.
     */
    @Operation(summary = "Delete a course", description = "Delete an existing course by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.ok("Cours supprimé");
    }

    /**
     * Recherche des cours par formateurId.
     */
    @Operation(summary = "Search courses by formateurId", description = "Retrieve courses by formateurId")
    @GetMapping("/formateur/{id}")
    public ResponseEntity<List<Cours>> getCoursByFormateurId(@PathVariable Long id) {
        List<Cours> coursList = coursService.rechercherCoursParFormateurId(id);
        return ResponseEntity.ok(coursList);
    }
}
