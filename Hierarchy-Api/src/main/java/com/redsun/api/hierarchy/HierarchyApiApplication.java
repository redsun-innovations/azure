package com.redsun.api.hierarchy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for the Hierarchy API Application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
public class HierarchyApiApplication {

	/**
	 * The entry point of the Hierarchy API Application.
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(HierarchyApiApplication.class, args);
	}
}