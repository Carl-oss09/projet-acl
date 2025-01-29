package com.example.mywebapp.client;

import com.example.mywebapp.model.Cours;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "coursClient", url = "http://my-api:8080")
public interface CoursClient {

    @GetMapping("/api/cours")
    List<Cours> getAllCours();

    @GetMapping("/api/cours/formateur/{id}")
    List<Cours> getCoursByFormateurId(@PathVariable("id") Long id);

    // Recherche de cours avec paramètres facultatifs
    @GetMapping(value = "/api/cours/search")
    List<Cours> rechercherCours(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "matiere", required = false) String matiere
    );

    @PostMapping("/api/cours")
    void addCours(@RequestBody Cours cours);

    @DeleteMapping("/api/cours/{id}")
    void deleteCours(@PathVariable("id") Long id);

    @PutMapping("/api/cours/{id}")
    void updateCours(@PathVariable("id") Long id, @RequestBody Cours cours);

    @GetMapping("/api/cours/{id}")
    Cours getCoursById(@PathVariable("id") Long id);

    @GetMapping("api/reservations/eleve/{idEleve}/cours")
    List<Cours> getCoursByEleveId(@PathVariable ("idEleve") Long idEleve);
}
