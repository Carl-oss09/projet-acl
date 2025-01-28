package com.example.my_batch;

import com.example.my_batch.config.BatchConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Active les tâches planifiées
public class MyBatchApplication {

	@Autowired
	private BatchConfiguration batchConfiguration;

	public static void main(String[] args) {
		SpringApplication.run(MyBatchApplication.class, args);
	}
}
