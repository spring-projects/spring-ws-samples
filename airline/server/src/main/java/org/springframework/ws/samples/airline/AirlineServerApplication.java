package org.springframework.ws.samples.airline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AirlineServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlineServerApplication.class, args);
	}
}
