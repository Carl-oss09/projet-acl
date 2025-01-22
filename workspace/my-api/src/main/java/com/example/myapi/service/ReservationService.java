package com.example.myapi.service;

import com.example.myapi.model.Cours;
import com.example.myapi.model.Reservation;
import com.example.myapi.repository.CoursRepository;
import com.example.myapi.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CoursRepository coursRepository;


    public ReservationService(ReservationRepository reservationRepository, CoursRepository coursRepository) {
        this.reservationRepository = reservationRepository;
        this.coursRepository = coursRepository;
    }


    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public void updateReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> getReservationsByEleveId(Long idEleve) {
        return reservationRepository.findByIdEleve(idEleve);
    }

    public List<Cours> getCoursByEleveId(Long idEleve) {
        // Récupérer les réservations liées à l'élève
        List<Reservation> reservations = reservationRepository.findByIdEleve(idEleve);

        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }

        // Récupérer les IDs des cours
        List<Long> coursIds = reservations.stream()
                .map(Reservation::getIdCours)
                .collect(Collectors.toList());

        // Retourner les cours correspondants
        return coursRepository.findAllById(coursIds);
    }


    public List<Reservation> getReservationsByCoursId(Long idCours) {
        return reservationRepository.findByIdCours(idCours);
    }

}