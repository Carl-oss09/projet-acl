package com.example.mywebapp.controller;

import com.example.mywebapp.client.CoursClient;
import com.example.mywebapp.client.ReservationClient;
import com.example.mywebapp.model.Cours;
import com.example.mywebapp.model.Reservation;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @GetMapping("/recherche")
    public String rechercherCours(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String matiere,
            Model model,
            HttpSession session
    ) {
        try {
            List<Cours> coursList;

            // Récupération de l'id de l'étudiant connecté
            Long idEtudiant = (Long) session.getAttribute("userId");

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

            // Calcul des informations supplémentaires pour chaque cours
            for (Cours cours : coursList) {
                List<Reservation> reservations = reservationClient.getReservationByCoursId(cours.getId());
                cours.setNbInscriptions(reservations.size());
                cours.setEstDejaInscrit(reservations.stream()
                        .anyMatch(reservation -> Objects.equals(reservation.getIdEleve(), idEtudiant)));
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

    @GetMapping("/etudiant2/cours")
    public String getCoursPourEtudiant(HttpSession session, Model model) {
        Long etudiantId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (etudiantId == null || !"etudiant".equals(userType)) {
            return "redirect:/connexion";
        }

        // Récupérer les réservations de l'étudiant
        List<Reservation> reservations = reservationClient.getCoursByEleveId(etudiantId);

        // Récupérer les cours associés
        List<Cours> coursInscrits = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Cours cours = coursClient.getCoursById(reservation.getIdCours());
            if (cours != null) {
                coursInscrits.add(cours);
            }
        }

        model.addAttribute("coursInscrits", coursInscrits);
        return "etudiants"; // Nom du fichier HTML pour afficher les cours
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
    public String inscrireEtudiant(@RequestParam Long idCours, HttpSession session) {
        System.out.println(idCours);
        try {
            // Récupération de l'ID de l'étudiant à partir de la session
            Long idEtudiant = (Long) session.getAttribute("userId");

            // Vérification que l'utilisateur est bien un étudiant connecté
            if (idEtudiant == null) {
                System.out.println("Utilisateur non connecté ou session invalide.");
            }

            // Vérification si le cours existe
            Cours cours = coursClient.getCoursById(idCours);
            if (cours == null) {
                System.out.println("Cours introuvable.");
            }

            // Vérification si le cours a atteint sa capacité maximale
            List<Reservation> reservationsForCours = reservationClient.getReservationByCoursId(idCours);
            if (reservationsForCours.size() >= cours.getNb_eleves_max()) {
                System.out.println("Ce cours est complet.");
            }

            // Vérification si l'étudiant est déjà inscrit à ce cours
            List<Reservation> reservations = reservationClient.getReservationByIdEleve(idEtudiant);
            boolean dejaInscrit = reservations.stream()
                    .anyMatch(reservation -> Objects.equals(reservation.getIdCours(), idCours));

            if (dejaInscrit) {
                System.out.println("Vous êtes déjà inscrit à ce cours.");
            }

            // Si pas encore inscrit, créer une nouvelle réservation
            Reservation reservation = new Reservation(idCours, idEtudiant);
            reservationClient.addReservation(reservation);

            System.out.println("Inscription réussie !");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'inscription. Veuillez réessayer PTNNNNN.");
        }
        return "recherche";
    }

    @GetMapping("/reservations/eleve/cours")
    @ResponseBody
    public Map<String, Object> getCoursByEleveId(HttpSession session) {
        Long idEtudiant = (Long) session.getAttribute("userId");
        List<Cours> coursList = coursClient.getCoursByEleveId(idEtudiant);

        // Retournez une réponse structurée sous forme de JSON
        Map<String, Object> response = new HashMap<>();
        response.put("coursList", coursList); // Assurez-vous que la clé correspond
        return response;
    }

    @GetMapping("/cours/formateur")
    @ResponseBody
    public Map<String, Object> getCoursByFormateurId(HttpSession session) {
        Long idFormateur = (Long) session.getAttribute("userId");
        List<Cours> coursList = coursClient.getCoursByFormateurId(idFormateur);

        // Retournez une réponse structurée sous forme de JSON
        Map<String, Object> response = new HashMap<>();
        response.put("coursList", coursList); // Assurez-vous que la clé correspond
        return response;
    }


}
