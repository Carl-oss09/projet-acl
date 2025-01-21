package com.example.mywebapp.controller;

import com.example.mywebapp.client.FormateurClient;
import com.example.mywebapp.client.CoursClient;
import com.example.mywebapp.model.Cours;
import com.example.mywebapp.model.Formateur;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FormateurController {

    @Autowired
    private FormateurClient formateurClient;

    @Autowired
    private CoursClient coursClient;

    @GetMapping("/formateurs")
    public String getFormateurs(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userType = (String) session.getAttribute("userType");

        if (userId == null || !"formateur".equals(userType)) {
            return "redirect:/connexion"; // Redirige vers la connexion si l'utilisateur n'est pas connecté ou n'est pas un formateur
        }

        // Récupérer les informations du formateur connecté
        Formateur formateur = formateurClient.getFormateurById(userId);

        // Ajouter les données nécessaires au modèle
        model.addAttribute("formateur", formateur); // Formateur connecté
        model.addAttribute("cours", new Cours()); // Objet Cours vide pour le formulaire
        return "formateurs"; // Retourne le template formateurs.html
    }


    @GetMapping("/login/formateurs")
    public String afficherFormulaireConnexion(Model model) {
        model.addAttribute("formateur", new Formateur()); // Ajouter un objet vide pour le formulaire
        return "login";
    }

    @PostMapping("/login/formateurs")
    public String addFormateur(@ModelAttribute("formateur") Formateur formateur, RedirectAttributes redirectAttributes) {
        // Ajoute le formateur via l'API
        formateurClient.addFormateur(formateur);

        // Récupère la liste de tous les formateurs
        List<Formateur> formateurs = formateurClient.getAllFormateurs();

        // Trouve le formateur avec l'ID le plus élevé
        Formateur newFormateur = formateurs.stream()
                .max((f1, f2) -> Long.compare(f1.getId(), f2.getId()))
                .orElse(null);

        if (newFormateur != null) {
            Long newFormateurId = newFormateur.getId();
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie ! Votre ID de connexion est : " + newFormateurId);
        } else {
            redirectAttributes.addFlashAttribute("error", "Inscription réussie, mais impossible de récupérer votre ID. Veuillez contacter l'administration.");
        }

        return "redirect:/connexion";  // Redirige vers la page de connexion
    }


    @PostMapping("/formateurs/update")
    public String updateFormateur(
            @RequestParam("id") Long id,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("L1") boolean L1,
            @RequestParam("L2") boolean L2,
            @RequestParam("L3") boolean L3) {
        Formateur formateur = new Formateur();
        formateur.setId(id);
        formateur.setNom(nom);
        formateur.setPrenom(prenom);
        formateur.setL1(L1);
        formateur.setL2(L2);
        formateur.setL3(L3);

        formateurClient.updateFormateur(id, formateur);

        return "redirect:/formateurs";
    }

    @PostMapping("/formateurs/delete")
    public String deleteFormateur(@RequestParam("id") Long id) {
        formateurClient.deleteFormateur(id);
        return "redirect:/formateurs";
    }
}