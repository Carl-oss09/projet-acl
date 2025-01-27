package com.example.my_batch.repository;

import com.example.my_batch.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByIdEleve(Long idEleve); // Utilisez le nom du champ dans l'entité

    List<Reservation> findByIdCours(Long idCours); // Utilisez le nom du champ dans l'entité
}
