package com.example.my_batch.config;

import com.example.my_batch.model.Cours;
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

    public ListItemReader<Cours> reader(Long eleveId) {
        List<Cours> coursList = fetchCoursForNextWeekFromAPI(eleveId);

        // ✅ Affichage des cours récupérés
        System.out.println("\n📚 [Reader] Cours récupérés pour l'étudiant ID " + eleveId + " :");
        if (coursList.isEmpty()) {
            System.out.println("⚠ Aucun cours trouvé.");
        } else {
            coursList.forEach(cours ->
                    System.out.println("🔹 Cours : " + cours.getTitre() + " | Date : " + cours.getDate()));
        }

        return new ListItemReader<>(coursList);
    }

    public List<Cours> fetchCoursForNextWeekFromAPI(Long eleveId) {
        String apiUrl = "http://localhost:8080/api/reservations/eleve/" + eleveId + "/cours";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Cours[]> response = restTemplate.getForEntity(apiUrl, Cours[].class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<Cours> coursList = Arrays.stream(response.getBody())
                    .filter(cours -> isNextWeek(cours.getDate())) // ✅ Filtrage des cours de la semaine prochaine
                    .collect(Collectors.toList());
            return coursList;
        } else {
            throw new RuntimeException("❌ Erreur lors de l'appel à l'API.");
        }
    }


    public ItemWriter<Cours> writer() {
        return items -> {
            System.out.println("\n📌 [Writer] Écriture des cours :");
            if (items.isEmpty()) {
                System.out.println("⚠ Aucun cours à écrire.");
            } else {
                items.forEach(cours ->
                        System.out.println("✏ Cours : " + cours.getTitre() + " | Date : " + cours.getDate()));
            }
        };
    }

    public Step coursStep(Long eleveId) {
        return new StepBuilder("coursStep", jobRepository)
                .<Cours, Cours>chunk(10, transactionManager)
                .reader(reader(eleveId))
                .processor(processor())  // ✅ Appel direct sans injection
                .writer(writer())
                .transactionManager(transactionManager)
                .build();
    }

    private boolean isNextWeek(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);  // ✅ Conversion de la date

            // ✅ Définition des limites de la semaine prochaine (lundi - vendredi)
            LocalDate nextWeekStart = LocalDate.of(2025, 2, 3); // 3 février 2025 (lundi)
            LocalDate nextWeekEnd = LocalDate.of(2025, 2, 7);   // 7 février 2025 (vendredi)

            // ✅ Vérification si la date est dans la semaine prochaine
            boolean isValid = !date.isBefore(nextWeekStart) && !date.isAfter(nextWeekEnd);

            return isValid;
        } catch (Exception e) {
            System.err.println("⚠ Erreur : Format de date invalide - " + dateString);
            return false;
        }
    }


    public SimpleJob importCoursJob(Long eleveId) {
        SimpleJob job = new SimpleJob();
        job.setJobRepository(jobRepository);
        job.addStep(coursStep(eleveId));
        return job;
    }

    public void triggerBatchJob(Long eleveId) {
        try {
            System.out.println("\n🚀 Déclenchement du batch pour l'étudiant ID " + eleveId);
            jobLauncher.run(importCoursJob(eleveId), new org.springframework.batch.core.JobParameters());
            System.out.println("✅ Batch exécuté à " + LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("❌ Échec du batch : " + e.getMessage());
        }
    }

    // ✅ Test : Exécution toutes les 20 secondes
    @Scheduled(cron = "0 */1 8-23 ? * TUE")
    public void testScheduledExecution() {
        Long testStudentId = 6000000L;
        System.out.println("\n⏰ [Planificateur] Lancement du batch toutes les 20s... Heure actuelle : " + LocalDateTime.now());
        triggerBatchJob(testStudentId);
    }
}
