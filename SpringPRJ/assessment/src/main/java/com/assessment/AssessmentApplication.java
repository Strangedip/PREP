package com.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AssessmentApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(AssessmentApplication.class, args);

		Partner p = context.getBean(Partner.class);

		System.out.println(p);
	}

	

}
