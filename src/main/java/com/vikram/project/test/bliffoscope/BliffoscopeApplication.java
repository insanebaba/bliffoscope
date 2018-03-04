package com.vikram.project.test.bliffoscope;

import com.vikram.project.test.bliffoscope.service.StorageProperties;
import com.vikram.project.test.bliffoscope.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class BliffoscopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BliffoscopeApplication.class, args);
	}


	@Bean
    CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}



}
