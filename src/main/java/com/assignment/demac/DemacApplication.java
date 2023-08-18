package com.assignment.demac;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemacApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemacApplication.class, args);
	}


	@PostConstruct
	public void fetchMail(){

	}
}
