package com.example.mywebapp.controller;

import com.example.mywebapp.client.CoursClient;
import com.example.mywebapp.client.ReservationClient;
import com.example.mywebapp.model.Cours;
import com.example.mywebapp.model.Reservation;
import com.example.mywebapp.model.Etudiant;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class CoursController {

    @Autowired
    private CoursClient coursClient;

    @Autowired
    private ReservationClient reservationClient;

    // Affiche tous les cours pour les formateurs
    @GetMapping("/cours")
    public String getCours(Model model) {
        List<Cours> coursList = coursClient.getAllCours();
        model.addAttribute("coursList", coursList);
        model.addAttribute("cours", new Cours()); // Ajoute un cours vide pour le formulaire
        return "formateurs"; // Retourne le template formateurs.html
    }

    // Méthode pour afficher la recherche des cours
    @GetMapping("/recherche")
    public String rechercherCours(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String matiere,
            Model model
    ) {
        try {
            List<Cours> coursList;

            // Logique de filtrage
            if (date == null && (matiere == null || matiere.equalsIgnoreCase("Toutes"))) {
                coursList = coursClient.getAllCours();
            } else if (date == null) {
                coursList = coursClient.rechercherCours(null, matiere);
            } else if (matiere == null || matiere.equalsIgnoreCase("Toutes")) {
                coursList = coursClient.rechercherCours(date, null);
            } else {
                coursList = coursClient.rechercherCours(date, matiere);
            }

            model.addAttribute("coursList", coursList);
            model.addAttribute("date", date);
            model.addAttribute("matiere", matiere);

            return "recherche";
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur s'est produite lors de la recherche des cours.");
            e.printStackTrace();
            return "recherche";
        }
    }

    public String afficherCours(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null || userType == null || !userType.equals("etudiant")) {
            return "redirect:/connexion";
        }

        List<Cours> coursList = coursClient.getAllCours();
        List<Reservation> Resa = reservationClient.getReservationByIdEleve(userId);
        List<Cours> coursReserves = new ArrayList<>();

        for (Reservation reservation : Resa) {
            Cours coursReserve = coursClient.getCoursById(reservation.getId_cours());
            if (coursReserve != null) {
                coursReserves.add(coursReserve);
            }
        }

        model.addAttribute("coursList", coursList);
        model.addAttribute("coursReserves", coursReserves);
        return "recherche";
    }

    @GetMapping("/cours/all")
    public String getAllCours(Model model) {
        List<Cours> coursList = coursClient.getAllCours();
        model.addAttribute("coursList", coursList);
        return "cours";
    }

    @GetMapping("/formateurs/cours")
    public String getFormateurs(Model model) {
        List<Cours> coursList = coursClient.getAllCours();
        model.addAttribute("coursList", coursList);
        model.addAttribute("cours", new Cours());
        return "formateurs";
    }

    @GetMapping("/formateur/cours")
    public String getCoursByFormateur(HttpSession session, Model model) {
        Long formateurId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (formateurId == null || userType == null || !userType.equals("formateur")) {
            return "redirect:/connexion";
        }

        try {
            List<Cours> coursList = coursClient.getCoursByFormateurId(formateurId);
            model.addAttribute("coursList", coursList);
            return "formateur-cours";
        } catch (Exception e) {
            model.addAttribute("error", "Une erreur s'est produite lors de la récupération des cours.");
            e.printStackTrace();
            return "formateur-cours";
        }
    }

    @PostMapping("/formateurs/cours")
    public String addCours(@ModelAttribute("cours") Cours cours, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null || !"formateur".equals(userType)) {
            return "redirect:/connexion";
        }

        cours.setFormateurId(userId); // Utilisation de formateurId
        coursClient.addCours(cours);

        return "redirect:/formateurs";
    }

    @PostMapping("/cours/update")
    public String updateCours(
            @RequestParam("id") Long id,
            @RequestParam("matiere") String matiere,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam("aprem_matin") boolean aprem_matin,
            @RequestParam("formateurId") Long formateurId, // Remplacement de id_prof
            @RequestParam("nb_eleves_max") int nb_eleves_max) {
        Cours cours = new Cours();
        cours.setId(id);
        cours.setMatiere(matiere);
        cours.setTitre(titre);
        cours.setDescription(description);
        cours.setDate(date);
        cours.setAprem_matin(aprem_matin);
        cours.setFormateurId(formateurId); // Utilisation de formateurId
        cours.setNb_eleves_max(nb_eleves_max);

        coursClient.updateCours(id, cours);

        return "redirect:/cours";
    }

    @PostMapping("/cours/delete")
    public String deleteCours(@RequestParam("id") Long id) {
        coursClient.deleteCours(id);
        return "redirect:/cours";
    }

    @PostMapping("/inscription/cours")
    @ResponseBody
    public String inscrireEtudiant(@RequestParam Long idCours, HttpSession session) {
        try {
            // Récupération de l'ID de l'étudiant à partir de la session
            Long idEtudiant = (Long) session.getAttribute("userId");

            // Vérification que l'utilisateur est bien un étudiant connecté
            if (idEtudiant == null) {
                return "Utilisateur non connecté ou session invalide.";
            }

            // Vérification si le cours existe
            Cours cours = coursClient.getCoursById(idCours);
            if (cours == null) {
                return "Cours introuvable.";
            }

            // Vérification si le cours a atteint sa capacité maximale
            List<Reservation> reservationsForCours = reservationClient.getReservationByCoursId(idCours);
            if (reservationsForCours.size() >= cours.getNb_eleves_max()) {
                return "Ce cours est complet.";
            }

            // Vérification si l'étudiant est déjà inscrit à ce cours
            List<Reservation> reservations = reservationClient.getReservationByIdEleve(idEtudiant);
            boolean dejaInscrit = reservations.stream()
                    .anyMatch(reservation -> Objects.equals(reservation.getId_cours(), idCours));

            if (dejaInscrit) {
                return "Vous êtes déjà inscrit à ce cours.";
            }

            // Si pas encore inscrit, créer une nouvelle réservation
            Reservation reservation = new Reservation(idCours, idEtudiant);
            reservationClient.addReservation(reservation);

            return "Inscription réussie !";

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'inscription. Veuillez réessayer PTNNNNN.";
        }
    }
}
