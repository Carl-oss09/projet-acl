package com.example.mywebapp;

import java.awt.Desktop;
import java.net.URI;
import java.net.http.HttpRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MywebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MywebappApplication.class, args);

		// Ouvrir l'URL dans le navigateur après le démarrage
		openBrowser("http://localhost:8082/login");
	}

	private static void openBrowser(String url) {
		try {
			// Vérifier si l'OS supporte l'ouverture dans un navigateur
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(URI.create(url));
			} else {
				System.out.println("Impossible d'ouvrir le navigateur par défaut");
				System.out.println("Ouvrez le lien suivant : http://localhost:8082/login");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
