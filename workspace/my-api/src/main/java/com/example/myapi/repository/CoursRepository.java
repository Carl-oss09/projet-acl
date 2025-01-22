package com.example.myapi.repository;

import com.example.myapi.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des entités Cours.
 */
@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {

    /**
     * Trouve les cours par date.
     *
     * @param date la date des cours.
     * @return la liste des cours correspondants.
     */
    List<Cours> findByDate(String date);

    // Méthode pour récupérer plusieurs cours à partir d'une liste d'IDs
    List<Cours> findAllById(Iterable<Long> ids);


    /**
     * Trouve les cours par matière.
     *
     * @param matiere la matière des cours.
     * @return la liste des cours correspondants.
     */
    List<Cours> findByMatiere(String matiere);

    /**
     * Trouve les cours par formateurId.
     *
     * @param formateurId l'identifiant du formateur.
     * @return la liste des cours correspondants.
     */
    List<Cours> findByFormateurId(Long formateurId);

    /**
     * Trouve les cours par date et matière.
     *
     * @param date    la date des cours.
     * @param matiere la matière des cours.
     * @return la liste des cours correspondants.
     */
    List<Cours> findByDateAndMatiere(String date, String matiere);

    /**
     * Trouve les cours dont la date correspond à une plage donnée.
     * Exemple d'ajout de méthode personnalisée.
     *
     * @param startDate la date de début de la plage.
     * @param endDate   la date de fin de la plage.
     * @return la liste des cours dans la plage de dates.
     */
    List<Cours> findByDateBetween(String startDate, String endDate);
}
