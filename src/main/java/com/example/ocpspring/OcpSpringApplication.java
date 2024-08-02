package com.example.ocpspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OcpSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(OcpSpringApplication.class, args);
	}

}
