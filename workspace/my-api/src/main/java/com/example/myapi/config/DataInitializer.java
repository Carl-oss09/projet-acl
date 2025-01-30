package com.example.myapi.config;

import com.example.myapi.model.Cours;
import com.example.myapi.model.Etudiant;
import com.example.myapi.model.Formateur;
import com.example.myapi.model.Reservation;
import com.example.myapi.repository.CoursRepository;
import com.example.myapi.repository.EtudiantRepository;
import com.example.myapi.repository.FormateurRepository;
import com.example.myapi.repository.ReservationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            EtudiantRepository etudiantRepository,
            FormateurRepository formateurRepository,
            CoursRepository coursRepository,
            ReservationRepository reservationRepository) {

        return args -> {
            // ✅ Vérifier si les données existent déjà
            if (etudiantRepository.count() > 0) {
                System.out.println("⚠ Données déjà présentes. Aucune insertion nécessaire.");
                return;
            }

            System.out.println("🚀 Initialisation des données...");

            // 📌 Ajout d'étudiants
            Etudiant etudiant1 = new Etudiant("Dupont", "Jean", true, false, false);
            Etudiant etudiant2 = new Etudiant("Martin", "Sophie", false, true, false);
            etudiantRepository.saveAll(List.of(etudiant1, etudiant2));
            System.out.println("✅ Étudiants ajoutés.");

            // 📌 Ajout de formateurs
            Formateur formateur1 = new Formateur("Durand", "Paul", true, true, false);
            Formateur formateur2 = new Formateur("Lemoine", "Claire", false, false, true);
            formateurRepository.saveAll(List.of(formateur1, formateur2));
            System.out.println("✅ Formateurs ajoutés.");

            // 📌 Ajout de cours
            Cours cours1 = new Cours("Mathématiques", "Algebra", "Introduction aux bases", "2025-02-05", false, formateur1.getId(), 20);
            Cours cours2 = new Cours("Physique", "Mécanique", "Les lois de Newton", "2025-02-06", true, formateur2.getId(), 15);
            coursRepository.saveAll(List.of(cours1, cours2));
            System.out.println("✅ Cours ajoutés.");

            // 📌 Ajout de réservations
            Reservation reservation1 = new Reservation(cours1.getId(), etudiant1.getId());
            Reservation reservation2 = new Reservation(cours2.getId(), etudiant2.getId());
            reservationRepository.saveAll(List.of(reservation1, reservation2));
            System.out.println("✅ Réservations ajoutées.");

            System.out.println("🎉 Initialisation terminée !");
        };
    }
}
