package com.example.mywebapp.client;

import com.example.mywebapp.model.Reservation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "reservationClient", url = "http://localhost:8080")
public interface ReservationClient {

    @GetMapping("/api/reservations")
    List<Reservation> getAllReservations();

    @PostMapping("/api/reservations")
    void addReservation(@RequestBody Reservation reservation);

    @DeleteMapping("/api/reservations/{id}")
    void deleteReservation(@PathVariable("id") Long id);

    @PutMapping("/api/reservations/{id}")
    void updateReservation(@PathVariable("id") Long id, @RequestBody Reservation reservation);

    @GetMapping("/api/reservations/{id}")
    List<Reservation> getReservationById(@PathVariable("id") Long id);

    @GetMapping("/api/reservations/student/{id}")
    List<Reservation> getReservationByIdEleve(@PathVariable("id") Long id);
}