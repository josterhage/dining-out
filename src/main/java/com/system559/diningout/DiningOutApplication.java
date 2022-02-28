package com.system559.diningout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DiningOutApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiningOutApplication.class, args);
	}

}
