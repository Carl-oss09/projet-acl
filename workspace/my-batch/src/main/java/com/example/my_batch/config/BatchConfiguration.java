package com.example.my_batch.config;

import com.example.my_batch.model.Cours;
import com.example.my_batch.model.Etudiant;
import com.example.my_batch.model.Formateur;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component  // ✅ Rend la classe détectable par Spring
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;

    public BatchConfiguration(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JobLauncher jobLauncher) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    public ItemProcessor<Cours, Cours> processor() {
        return cours -> cours; // ✅ Ajoutez ici une logique de transformation si nécessaire
    }

    // ✅ Récupérer tous les étudiants depuis l'API
    public List<Etudiant> fetchAllEtudiants() {
        String apiUrl = "http://my-api:8080/api/etudiants";
        //String apiUrl = "http://localhost:8080/api/etudiants";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Etudiant[]> response = restTemplate.getForEntity(apiUrl, Etudiant[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Etudiant> etudiants = Arrays.asList(response.getBody());

            // ✅ Affichage amélioré
            System.out.println("\n🎓 Liste complète des étudiants récupérés :");
            if (etudiants.isEmpty()) {
                System.out.println("⚠ Aucun étudiant trouvé.");
            } else {
                for (Etudiant etudiant : etudiants) {
                    System.out.println("🔹 ID: " + etudiant.getId() + " | Nom: " + etudiant.getNom() + " | Prénom: " + etudiant.getPrenom());
                }
            }
            System.out.println("----------------------------------------------------");

            return etudiants;
        } else {
            throw new RuntimeException("❌ Erreur lors de la récupération des étudiants depuis l'API.");
        }
    }



    public ListItemReader<Cours> reader(Long eleveId, String nom, String prenom) {
        List<Cours> coursList = fetchCoursForNextWeekFromAPI(eleveId, nom, prenom);

        System.out.println("\n📚 [Reader] Cours récupérés pour " + prenom + " " + nom + " (ID: " + eleveId + ") :");
        if (coursList.isEmpty()) {
            System.out.println("⚠ Aucun cours trouvé.");
        } else {
            for (Cours cours : coursList) {
                String periode = cours.isAprem_matin() ? "Après-midi" : "Matin";
                String formateurNom = fetchFormateurNameById(cours.getFormateurId());
                System.out.println("🔹 Cours : " + cours.getTitre() + " | Date : " + cours.getDate() +
                        " | Période : " + periode + " | Prof : " + formateurNom);
            }
        }

        return new ListItemReader<>(coursList);
    }


    // ✅ Récupérer un formateur par son ID
    public String fetchFormateurNameById(Long formateurId) {
        if (formateurId == null) {
            return "❌ Formateur inconnu";
        }

        String apiUrl = "http://my-api:8080/api/formateurs/" + formateurId;
        //String apiUrl = "http://localhost:8080/api/formateurs/" + formateurId;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Formateur> response = restTemplate.getForEntity(apiUrl, Formateur.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Formateur formateur = response.getBody();
                return formateur.getPrenom() + " " + formateur.getNom();
            } else {
                return "❌ Formateur non trouvé";
            }
        } catch (Exception e) {
            return "❌ Erreur lors de la récupération du formateur";
        }
    }



    // ✅ Récupérer les cours pour la semaine prochaine avec l'étudiant
    public List<Cours> fetchCoursForNextWeekFromAPI(Long eleveId, String nom, String prenom) {
        String apiUrl = "http://my-api:8080/api/reservations/eleve/" + eleveId + "/cours";
        //String apiUrl = "http://localhost:8080/api/reservations/eleve/" + eleveId + "/cours";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Cours[]> response = restTemplate.getForEntity(apiUrl, Cours[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Cours> coursList = Arrays.stream(response.getBody())
                        .filter(cours -> isNextWeek(cours.getDate()))
                        .collect(Collectors.toList());

                // 📂 Enregistrer les cours dans un fichier
                saveCoursesToFile(eleveId, nom, prenom, coursList);

                return coursList;
            } else {
                return List.of();
            }
        } catch (Exception e) {
            return List.of();
        }
    }




    public ItemWriter<Cours> writer() {
        return items -> {
            System.out.println("\n📌 [Writer] Écriture des cours :");
            if (items.isEmpty()) {
                System.out.println("⚠ Aucun cours à écrire.");
            } else {
                for (Cours cours : items) {
                    String periode = cours.isAprem_matin() ? "Après-midi" : "Matin";
                    String formateurNom = fetchFormateurNameById(cours.getFormateurId());
                    System.out.println("✏ Cours : " + cours.getTitre() + " | Date : " + cours.getDate() +
                            " | Période : " + periode + " | Prof : " + formateurNom);
                }
            }
        };
    }

    public Step coursStep(Long eleveId, String nom, String prenom) {
        return new StepBuilder("coursStep", jobRepository)
                .<Cours, Cours>chunk(10, transactionManager)
                .reader(reader(eleveId, nom, prenom))
                .processor(processor())  // ✅ Appel direct sans injection
                .writer(writer())
                .transactionManager(transactionManager)
                .build();
    }

    private boolean isNextWeek(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);  // ✅ Conversion de la date
            LocalDate today = LocalDate.now();
            LocalDate nextWeekStart = today.with(java.time.DayOfWeek.MONDAY).plusWeeks(1);
            LocalDate nextWeekEnd = nextWeekStart.plusDays(4);

            boolean isValid = !date.isBefore(nextWeekStart) && !date.isAfter(nextWeekEnd);

            return isValid;
        } catch (Exception e) {
            System.err.println("⚠ Erreur : Format de date invalide - " + dateString);
            return false;
        }
    }

    public SimpleJob importCoursJob(Long eleveId, String nom, String prenom) {
        SimpleJob job = new SimpleJob("jobFor_" + eleveId + "_" + System.currentTimeMillis()); // ✅ Nom unique
        job.setJobRepository(jobRepository);
        job.addStep(coursStep(eleveId, nom, prenom));
        return job;
    }



    // ✅ Exécute le batch pour tous les étudiants
    public void triggerBatchJobForAllStudents() {
        try {
            List<Etudiant> etudiants = fetchAllEtudiants();

            if (etudiants.isEmpty()) {
                System.out.println("⚠ Aucun étudiant trouvé.");
                return;
            }

            for (Etudiant etudiant : etudiants) {
                Long etudiantId = etudiant.getId();
                System.out.println("\n🚀 Déclenchement du batch pour " + etudiant.getPrenom() + " " + etudiant.getNom() + " (ID: " + etudiantId + ")");

                try {
                    jobLauncher.run(
                            importCoursJob(etudiantId, etudiant.getNom(), etudiant.getPrenom()),
                            new org.springframework.batch.core.JobParameters()
                    );

                } catch (Exception e) {
                }
            }

            System.out.println("✅ Batch exécuté pour tous les étudiants à " + LocalDateTime.now());
        } catch (Exception e) {
        }
    }


    public void saveCoursesToFile(Long etudiantId, String nom, String prenom, List<Cours> coursList) {
        // 📅 Déterminer la date du batch
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String batchFolderName = "Batch_suivi_" + todayDate;

        // 📂 Définir le chemin de workspace
        String workspacePath = System.getenv("WORKSPACE_PATH");
        if (workspacePath == null) {
            workspacePath = "/workspace"; // Chemin par défaut dans le conteneur
        }

        //String workspacePath = "C:\\Users\\berwi\\OneDrive - UNIVERSITE CATHOLIQUE DE L'OUEST\\Doc\\M2 IMA\\Architecture et CL (Buron)\\projet-buron\\workspace";


        // 📂 Construire le chemin du dossier du batch
        File batchFolder = new File(workspacePath, batchFolderName);

        // 📂 Construire le chemin du sous-dossier de l'étudiant
        File studentDir = new File(batchFolder, String.valueOf(etudiantId));

        // 📂 Créer les dossiers s'ils n'existent pas
        if (!studentDir.exists()) {
            studentDir.mkdirs();
        }

        // 📄 Fichier de cours
        File coursFile = new File(studentDir, "cours.txt");

        try (FileWriter writer = new FileWriter(coursFile)) {
            writer.write("📚 Cours pour " + prenom + " " + nom + " (ID: " + etudiantId + ")\n");
            writer.write("------------------------------------------------------\n");

            if (coursList.isEmpty()) {
                writer.write("⚠ Aucun cours trouvé pour la semaine prochaine.\n");
            } else {
                for (Cours cours : coursList) {
                    String periode = cours.isAprem_matin() ? "Après-midi" : "Matin";
                    String formateurNom = fetchFormateurNameById(cours.getFormateurId());
                    writer.write("🔹 Cours : " + cours.getTitre() + " | Date : " + cours.getDate() +
                            " | Période : " + periode + " | Prof : " + formateurNom + "\n");
                }
            }

            writer.write("------------------------------------------------------\n");
            System.out.println("✅ Fichier de cours enregistré : " + coursFile.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'écriture du fichier : " + e.getMessage());
        }
    }




    // ✅ Exécution automatique tous les vendredis à 17h pour tous les étudiants
    @Scheduled(cron = "0 00 17 * * FRI")
    public void testScheduledExecution() {
        System.out.println("\n⏰ [Planificateur] Lancement du batch pour tous les étudiants... Heure actuelle : " + LocalDateTime.now());
        triggerBatchJobForAllStudents();
    }
}