package com.api.data_migrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class DataMigratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataMigratorApplication.class, args);
	}

}
