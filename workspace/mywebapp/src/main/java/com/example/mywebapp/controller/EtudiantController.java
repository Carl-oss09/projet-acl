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
        model.addAttribute("etudiant", new Etudiant()); // Ajouter un objet vide pour le formulaire étudiant
        model.addAttribute("formateur", new Formateur()); // Ajouter un objet vide pour le formulaire formateur
        return "login"; // Retourne le nom du template login.html
    }

    @PostMapping("/connexion")
    public String login(@RequestParam("id") Long id, HttpSession session, Model model) {
        // Vérifier si l'ID correspond à un étudiant
        try {
            Etudiant etudiant = etudiantClient.getEtudiantById(id);
            if (etudiant != null) {
                // Stocker l'ID utilisateur dans la session
                session.setAttribute("userId", id);
                session.setAttribute("userType", "etudiant");
                model.addAttribute("etudiant", etudiant);
                return "etudiants"; // Page des étudiants
            }
        } catch (Exception e) {
            // Ignorer si l'ID n'est pas trouvé
        }

        // Vérifier si l'ID correspond à un formateur
        try {
            Formateur formateur = formateurClient.getFormateurById(id);
            if (formateur != null) {
                // Stocker l'ID utilisateur dans la session
                session.setAttribute("userId", id);
                session.setAttribute("userType", "formateur");
                model.addAttribute("formateur", formateur);
                return "formateurs"; // Page des formateurs
            }
        } catch (Exception e) {
            // Ignorer si l'ID n'est pas trouvé
        }

        // Si l'ID n'est pas reconnu, afficher un message d'erreur
        model.addAttribute("error", "ID non reconnu");
        return "login"; // Reste sur la page de connexion
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
    public String addEtudiant(@ModelAttribute("etudiant") Etudiant etudiant) {
        etudiantClient.addEtudiant(etudiant);  // Ajoute l'étudiant via l'API
        return "redirect:/login";  // Redirige vers la liste pour afficher la mise à jour
    }

    @PostMapping("/etudiants/update")
    public String updateEtudiant(
            @RequestParam("id") Long id,
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            @RequestParam("L1") boolean L1,
            @RequestParam("L2") boolean L2,
            @RequestParam("L3") boolean L3) {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(id);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setL1(L1);
        etudiant.setL2(L2);
        etudiant.setL3(L3);

        etudiantClient.updateEtudiant(id, etudiant);

        return "redirect:/etudiants";
    }

    @PostMapping("/etudiants/delete")
    public String deleteEtudiant(@RequestParam("id") Long id) {
        etudiantClient.deleteEtudiant(id);
        return "redirect:/etudiants";
    }
}