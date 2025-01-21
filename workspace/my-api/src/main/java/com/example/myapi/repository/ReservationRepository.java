package com.example.myapi.repository;

import com.example.myapi.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByIdEleve(Long idEleve); // Utilisez le nom du champ dans l'entité
}
