package com.example.myapi.service;

import com.example.myapi.model.Cours;
import com.example.myapi.repository.CoursRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursService {

    private final CoursRepository coursRepository;

    public CoursService(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    /**
     * Récupère tous les cours.
     *
     * @return la liste de tous les cours.
     */
    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    /**
     * Recherche des cours par date.
     *
     * @param date la date des cours.
     * @return la liste des cours correspondants.
     */
    public List<Cours> rechercherCoursParDate(String date) {
        return coursRepository.findByDate(date);
    }

    /**
     * Recherche des cours par matière.
     *
     * @param matiere la matière des cours.
     * @return la liste des cours correspondants.
     */
    public List<Cours> rechercherCoursParMatiere(String matiere) {
        return coursRepository.findByMatiere(matiere);
    }

    /**
     * Recherche des cours par date et matière.
     *
     * @param date    la date des cours.
     * @param matiere la matière des cours.
     * @return la liste des cours correspondants.
     */
    public List<Cours> rechercherCoursParDateEtMatiere(String date, String matiere) {
        return coursRepository.findByDateAndMatiere(date, matiere);
    }

    /**
     * Recherche des cours dynamiquement en fonction des paramètres fournis.
     * Si aucun paramètre n'est fourni, retourne tous les cours.
     *
     * @param date    la date des cours.
     * @param matiere la matière des cours.
     * @return la liste des cours correspondants.
     */
    public List<Cours> rechercherCours(String date, String matiere) {
        if (date != null && matiere != null) {
            return rechercherCoursParDateEtMatiere(date, matiere);
        } else if (date != null) {
            return rechercherCoursParDate(date);
        } else if (matiere != null) {
            return rechercherCoursParMatiere(matiere);
        } else {
            return getAllCours();
        }
    }

    /**
     * Ajoute un nouveau cours.
     *
     * @param cours l'entité cours à ajouter.
     * @return l'entité cours ajoutée.
     */
    public Cours addCours(Cours cours) {
        return coursRepository.save(cours);
    }

    /**
     * Récupère un cours par son identifiant.
     *
     * @param id l'identifiant du cours.
     * @return le cours correspondant ou null si non trouvé.
     */
    public Cours getCoursById(Long id) {
        return coursRepository.findById(id).orElse(null);
    }

    /**
     * Met à jour un cours existant.
     *
     * @param cours l'entité cours mise à jour.
     */
    public void updateCours(Cours cours) {
        if (coursRepository.existsById(cours.getId())) {
            coursRepository.save(cours);
        }
    }

    /**
     * Recherche des cours par formateurId.
     *
     * @param formateurId l'identifiant du formateur.
     * @return la liste des cours correspondants.
     */
    public List<Cours> getCoursParFormateurId(Long formateurId) {
        return coursRepository.findByFormateurId(formateurId);
    }

    /**
     * Recherche des cours par date dans une plage donnée.
     *
     * @param startDate la date de début de la plage.
     * @param endDate   la date de fin de la plage.
     * @return la liste des cours dans la plage de dates.
     */
    public List<Cours> rechercherCoursParPlageDeDates(String startDate, String endDate) {
        return coursRepository.findByDateBetween(startDate, endDate);
    }

    /**
     * Supprime un cours par son identifiant.
     *
     * @param id l'identifiant du cours à supprimer.
     */
    public void deleteCours(Long id) {
        if (coursRepository.existsById(id)) {
            coursRepository.deleteById(id);
        }
    }
}
