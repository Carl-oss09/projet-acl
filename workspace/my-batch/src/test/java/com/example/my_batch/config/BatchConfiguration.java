package com.example.my_batch.config;
import com.example.my_batch.model.Product;
import com.example.my_batch.repository.ProductRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final ProductRepository productRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobLauncher jobLauncher;

    public BatchConfiguration(ProductRepository productRepository, JobRepository jobRepository,
                              PlatformTransactionManager transactionManager, JobLauncher jobLauncher) {
        this.productRepository = productRepository;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.jobLauncher = jobLauncher;
    }

    // 1. FlatFileItemReader to read CSV file
    @Bean
    public FlatFileItemReader<Product> reader() {
        FlatFileItemReader<Product> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("products.csv"));
        reader.setLinesToSkip(1); // Skip header line

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "price"); // Matches CSV columns to Product fields

        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    // 2. ItemProcessor to process each Product (optional, in this case it just passes the data through)
    @Bean
    public ItemProcessor<Product, Product> processor() {
        return product -> product;
    }

    // 3. ItemWriter to save Product to the database
    @Bean
    public ItemWriter<Product> writer() {
        return products -> productRepository.saveAll(products);
    }

    // 4. Step to process the data using the reader, processor, and writer
    @Bean
    public Step productStep() {
        return new StepBuilder("productStep", jobRepository)
                .<Product, Product>chunk(10, transactionManager) // Processing 10 items at a time
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .transactionManager(transactionManager)
                .build();
    }

    // 5. Job to execute the batch process
    @Bean
    public Job importProductJob() {
        SimpleJob job = new SimpleJob();
        job.setJobRepository(jobRepository);
        job.addStep(productStep());
        return job;
    }

    // 6. CommandLineRunner to trigger the job when the application starts
    @Bean
    public CommandLineRunner runBatchJob() {
        return args -> {
            try {
                System.out.println("Starting the batch job...");
                jobLauncher.run(importProductJob(), new JobParameters());
            } catch (Exception e) {
                System.out.println("Job failed: " + e.getMessage());
            }
        };
    }
}
