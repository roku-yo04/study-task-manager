package com.mta.studytaskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StudyTaskManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyTaskManagerApplication.class, args);
	}

}
