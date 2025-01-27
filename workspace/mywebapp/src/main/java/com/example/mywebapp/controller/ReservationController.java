package com.example.mywebapp.controller;

import com.example.mywebapp.client.ReservationClient;
import com.example.mywebapp.client.CoursClient;
import com.example.mywebapp.model.Cours;
import com.example.mywebapp.model.Reservation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ReservationController {

    @Autowired
    private ReservationClient reservationClient;
    @Autowired
    private CoursClient coursClient;



    @GetMapping("/reservations")
    public String getReservations(Model model) {
        List<Reservation> reservationList = reservationClient.getAllReservations();
        model.addAttribute("reservationList", reservationList);
        model.addAttribute("reservation", new Reservation()); // Ajoute une réservation vide pour le formulaire
        return "reservations";  // Retourne le template reservations.html
    }

    @GetMapping("/cours/inscrire")
    public String inscrireCours(@RequestParam("idCours") Long idCours, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        // Vérification de connexion et du type d'utilisateur
        if (userId == null || userType == null || !userType.equals("etudiant")) {
            return "redirect:/connexion";
        }

        // Création de la réservation
        Reservation reservation = new Reservation(idCours, userId);
        reservationClient.addReservation(reservation);

        return "redirect:/recherche";
    }

    @PostMapping("/inscription/reservation")
    public String inscrireEtudiant(
            @RequestParam("idCours") Long idCours,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long idEleve = (Long) session.getAttribute("userId"); // Récupérer l'ID de l'élève depuis la session

        if (idEleve == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vous devez être connecté pour vous inscrire.");
            return "redirect:/connexion"; // Redirige vers la page de connexion si l'élève n'est pas connecté
        }

        try {
            // Créer une réservation et envoyer au service
            Reservation reservation = new Reservation(idEleve, idCours);
            reservationClient.addReservation(reservation);

            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'inscription. Veuillez réessayer.");
            e.printStackTrace();
        }

        // Retourne à la liste des cours
        return "redirect:/recherche"; // Redirige vers la page de recherche (ou ajustez selon vos besoins)
    }



    @PostMapping("/reservations")
    public String addReservation(@ModelAttribute Reservation reservation) {
        reservationClient.addReservation(reservation);  // Ajoute la réservation via l'API
        return "redirect:/reservations";  // Redirige vers la liste pour afficher la mise à jour
    }

    @PostMapping("/reservations/update")
    public String updateReservation(
            @RequestParam("id") Long id,
            @RequestParam("id_cours") Long id_cours,
            @RequestParam("id_eleve") Long id_eleve) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setIdCours(id_cours);
        reservation.setIdEleve(id_eleve);

        reservationClient.updateReservation(id, reservation);

        return "redirect:/reservations";
    }

    @PostMapping("/reservations/delete")
    public String deleteReservation(@RequestParam("id") Long id) {
        reservationClient.deleteReservation(id);
        return "redirect:/reservations";
    }
}