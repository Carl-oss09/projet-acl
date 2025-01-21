package com.example.mywebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MywebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MywebappApplication.class, args);
		System.out.println("Ouvrez cette URL dans votre navigateur : http://localhost:8082/login");

	}

}
