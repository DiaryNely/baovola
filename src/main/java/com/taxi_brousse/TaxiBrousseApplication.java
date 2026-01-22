package com.taxi_brousse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaxiBrousseApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaxiBrousseApplication.class, args);
	}

}
