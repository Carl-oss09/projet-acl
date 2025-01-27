package com.example.my_batch.config;

import com.example.my_batch.model.Cours;
import com.example.my_batch.repository.CoursRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final CoursRepository coursRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;

    public BatchConfiguration(CoursRepository coursRepository, JobRepository jobRepository,
                              PlatformTransactionManager transactionManager, JobLauncher jobLauncher) {
        this.coursRepository = coursRepository;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jobLauncher = jobLauncher;
    }

    // 1. Reader - Fetch data dynamically from API
    @Bean
    public ListItemReader<Cours> reader() {
        List<Cours> coursList = fetchCoursForNextWeekFromAPI();
        return new ListItemReader<>(coursList);
    }

    private List<Cours> fetchCoursForNextWeekFromAPI() {
        Long eleveId = getConnectedStudentId();
        if (eleveId == null) {
            throw new IllegalStateException("Élève non connecté");
        }

        String apiUrl = "http://localhost:8080/api/reservation/eleve/" + eleveId + "/cours";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Cours[]> response = restTemplate.getForEntity(apiUrl, Cours[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return Arrays.stream(response.getBody())
                    .filter(cours -> isNextWeek(cours.getDate())) // Filtrer les cours de la semaine prochaine
                    .collect(Collectors.toList());
        } else {
            throw new RuntimeException("Erreur lors de l'appel à l'API");
        }
    }

    private boolean isNextWeek(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate nextWeekStart = today.plusDays(1).with(DayOfWeek.MONDAY);
        LocalDate nextWeekEnd = nextWeekStart.plusDays(6);
        return (date.isAfter(nextWeekStart) || date.isEqual(nextWeekStart)) &&
                (date.isBefore(nextWeekEnd) || date.isEqual(nextWeekEnd));
    }

    private Long getConnectedStudentId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpSession session = attributes.getRequest().getSession(false);
            if (session != null) {
                return (Long) session.getAttribute("studentId");
            }
        }
        return null;
    }

    // 2. Processor - Optional processing logic
    @Bean
    public ItemProcessor<Cours, Cours> processor() {
        return cours -> {
            // Add any additional processing logic here if needed
            System.out.println("Processing course: " + cours.getTitre());
            return cours;
        };
    }

    // 3. Writer - Save to the database or log the result
    @Bean
    public ItemWriter<Cours> writer() {
        return items -> {
            for (Cours cours : items) {
                System.out.println("Saving course: " + cours);
                coursRepository.save(cours);
            }
        };
    }

    // 4. Step - Combine Reader, Processor, and Writer
    @Bean
    public Step coursStep() {
        return new StepBuilder("coursStep", jobRepository)
                .<Cours, Cours>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .transactionManager(transactionManager)
                .build();
    }

    // 5. Job - Define the batch job
    @Bean
    public Job importCoursJob() {
        SimpleJob job = new SimpleJob();
        job.setJobRepository(jobRepository);
        job.addStep(coursStep());
        return job;
    }

    // 6. CommandLineRunner - Trigger the batch job on application startup
    @Bean
    public CommandLineRunner runBatchJob() {
        return args -> {
            try {
                System.out.println("Starting the batch job...");
                jobLauncher.run(importCoursJob(), new JobParameters());
            } catch (Exception e) {
                System.err.println("Job failed: " + e.getMessage());
            }
        };
    }
}
