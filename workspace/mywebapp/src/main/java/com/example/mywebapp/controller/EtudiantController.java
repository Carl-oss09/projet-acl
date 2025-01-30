package com.example.mywebapp.controller;


import com.example.mywebapp.client.EtudiantClient;
import com.example.mywebapp.client.FormateurClient;
import com.example.mywebapp.model.Cours;
import com.example.mywebapp.model.Etudiant;
import com.example.mywebapp.model.Formateur;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EtudiantController {

    @Autowired
    private EtudiantClient etudiantClient;

    @Autowired
    private FormateurClient formateurClient;

    @GetMapping("/etudiants")
    public String getEtudiants(Model model) {
        List<Etudiant> etudiants = etudiantClient.getAllEtudiants();
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("etudiant", new Etudiant()); // Ajoute un étudiant vide pour le formulaire
        return "etudiants";  // Retourne le template etudiants.html
    }

    @GetMapping("/connexion")
    public String ConnexionAffichage(Model model) {
        if (!model.containsAttribute("etudiant")) {
            model.addAttribute("etudiant", new Etudiant()); // Ajoute un objet vide pour le formulaire étudiant
        }
        if (!model.containsAttribute("formateur")) {
            model.addAttribute("formateur", new Formateur()); // Ajoute un objet vide pour le formulaire formateur
        }
        return "login"; // Nom de votre template
    }


    @PostMapping("/connexion")
    public String login(@RequestParam("id") String id, HttpSession session, Model model) {
        model.addAttribute("etudiant", new Etudiant());
        model.addAttribute("formateur", new Formateur());
        // Vérifier si l'ID respecte le format attendu
        if (!id.matches("^[16]\\d{5,}$")) {
            model.addAttribute("error", "L'ID doit commencer par 1 ou 6 et contenir au moins 6 chiffres.");
            return "login"; // Retourne la page de connexion avec le message d'erreur
        }

        try {
            // Vérifier si l'ID correspond à un formateur
            Long idLong = Long.parseLong(id);
            Formateur formateur = formateurClient.getFormateurById(idLong);
            if (formateur != null) {
                session.setAttribute("userId", idLong);
                session.setAttribute("userType", "formateur");
                model.addAttribute("formateur", formateur);
                return "formateurs"; // Page des formateurs
            }
        } catch (Exception e) {
            // Ignorer l'exception si l'ID ne correspond pas à un formateur
        }

        try {
            // Vérifier si l'ID correspond à un étudiant
            Long idLong = Long.parseLong(id);
            Etudiant etudiant = etudiantClient.getEtudiantById(idLong);
            if (etudiant != null) {
                session.setAttribute("userId", idLong);
                session.setAttribute("userType", "etudiant");
                model.addAttribute("etudiant", etudiant);



                return "etudiants"; // Page des étudiants
            }
        } catch (Exception e) {
            // Ignorer l'exception si l'ID ne correspond pas à un étudiant
        }

        // Si l'ID n'est pas reconnu, afficher un message d'erreur
        model.addAttribute("error", "ID non reconnu ou inexistant.");
        return "login"; // Retourne la page de connexion avec le message d'erreur
    }






    @GetMapping("/login")
    public String afficherFormulaireConnexion0(Model model) {
        model.addAttribute("etudiant", new Etudiant()); // Ajouter un objet vide pour le formulaire étudiant
        model.addAttribute("formateur", new Formateur()); // Ajouter un objet vide pour le formulaire formateur
        return "login";
    }


    @GetMapping("/profil-etudiant")
    public String afficherProfil(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null || userType == null) {
            return "redirect:/connexion"; // Redirige vers la connexion si l'utilisateur n'est pas connecté
        }

        if (userType.equals("etudiant")) {
            Etudiant etudiant = etudiantClient.getEtudiantById(userId);
            model.addAttribute("etudiant", etudiant);
            return "profil-etudiant";}

        return "redirect:/connexion";
    }

    @GetMapping("/profil-formateur")
    public String afficherProfilFormateur(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null || userType == null) {
            return "redirect:/connexion"; // Redirige vers la connexion si l'utilisateur n'est pas connecté
        }

        if (userType.equals("formateur")) {
            Formateur formateur = formateurClient.getFormateurById(userId);
            model.addAttribute("formateur", formateur);
            return "profil-formateur";
        }

        return "redirect:/connexion";
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate(); // Invalide toute la session
        return "redirect:/connexion"; // Redirige vers la page de connexion
    }


    @GetMapping("/login/etudiants")
    public String afficherFormulaireConnexion(Model model) {
        model.addAttribute("etudiant", new Etudiant()); // Ajouter un objet vide pour le formulaire
        return "login";
    }

    @GetMapping("etudiants/{id}")
    public String getEtudiantById(@RequestParam("id") Long id) {
        etudiantClient.getEtudiantById(id);
        return "redirect:/etudiants";
    }

    @PostMapping("/login/etudiants")
    public String addEtudiant(@ModelAttribute("etudiant") Etudiant etudiant, RedirectAttributes redirectAttributes) {
        // Ajoute l'étudiant via l'API
        etudiantClient.addEtudiant(etudiant);

        // Récupère la liste de tous les étudiants
        List<Etudiant> etudiants = etudiantClient.getAllEtudiants();

        // Trouve l'étudiant avec l'ID le plus élevé
        Etudiant newEtudiant = etudiants.stream()
                .max((e1, e2) -> Long.compare(e1.getId(), e2.getId()))
                .orElse(null);

        if (newEtudiant != null) {
            Long newEtudiantId = newEtudiant.getId();
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie ! Votre ID de connexion est : " + newEtudiantId);
        } else {
            redirectAttributes.addFlashAttribute("error", "Inscription réussie, mais impossible de récupérer votre ID. Veuillez contacter l'administration.");
        }

        return "redirect:/connexion";  // Redirige vers la page de connexion
    }



    @PostMapping("/etudiants/update")
    public String updateEtudiant(
            HttpSession session,
            Model model,  // Ajout du Model
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam(value = "L1", defaultValue = "false") boolean L1,
            @RequestParam(value = "L2", defaultValue = "false") boolean L2,
            @RequestParam(value = "L3", defaultValue = "false") boolean L3) {

        Long userId = (Long) session.getAttribute("userId");

        Etudiant etudiant = new Etudiant();
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setL1(L1);
        etudiant.setL2(L2);
        etudiant.setL3(L3);

        etudiantClient.updateEtudiant(userId, etudiant);

        // Récupérer l'étudiant mis à jour depuis la base
        Etudiant updatedEtudiant = etudiantClient.getEtudiantById(userId);

        // Ajouter l'étudiant au modèle pour Thymeleaf
        model.addAttribute("etudiant", updatedEtudiant);

        return "profil-etudiant";
    }

    @PostMapping("/etudiants/delete")
    public String deleteEtudiant(@RequestParam("id") Long id) {
        etudiantClient.deleteEtudiant(id);
        return "redirect:/login";
    }
}