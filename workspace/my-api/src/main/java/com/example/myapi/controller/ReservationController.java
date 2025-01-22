package com.example.myapi.controller;

import com.example.myapi.model.Cours;
import com.example.myapi.model.Reservation;
import com.example.myapi.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation API", description = "API for managing reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Get all reservations", description = "Retrieve all reservations from the database")
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @Operation(summary = "Get courses for a specific student", description = "Retrieve all courses an eleve is enrolled in")
    @GetMapping("/eleve/{idEleve}/cours")
    public ResponseEntity<List<Cours>> getCoursByEleveId(@PathVariable Long idEleve) {
        List<Cours> coursList = reservationService.getCoursByEleveId(idEleve);

        if (coursList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(coursList);
    }


    @PostMapping
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        Reservation newReservation = reservationService.addReservation(reservation);
        return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable Long id, @RequestBody Reservation updatedReservation) {
        Reservation existingReservation = reservationService.getReservationById(id);
        if (existingReservation != null) {
            existingReservation.setIdCours(updatedReservation.getIdCours());
            existingReservation.setIdEleve(updatedReservation.getIdEleve());
            reservationService.updateReservation(existingReservation);
            return ResponseEntity.ok("Reservation mise à jour");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation non trouvée");
        }
    }

    @Operation(summary = "Get reservations by student ID", description = "Retrieve all reservations for a specific student")
    @GetMapping("eleve/{idEleve}")
    public ResponseEntity<List<Reservation>> getReservationsByEleveId(@PathVariable Long idEleve) {
        List<Reservation> reservations = reservationService.getReservationsByEleveId(idEleve);
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "Get reservations by cours ID", description = "Retrieve all reservations for a specific student")
    @GetMapping("cours/{idCours}")
    public ResponseEntity<List<Reservation>> getReservationsByCoursId(@PathVariable Long idCours) {
        List<Reservation> reservations = reservationService.getReservationsByCoursId(idCours);
        return ResponseEntity.ok(reservations);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("Reservation supprimée");
    }
}