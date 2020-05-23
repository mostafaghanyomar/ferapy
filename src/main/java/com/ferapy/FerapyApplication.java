package com.ferapy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.ferapy.util.ApplicationUtil.setupSpringApplication;

@SpringBootApplication
public class FerapyApplication {

	public static void main(String[] args) {
		SpringApplication application = setupSpringApplication();
		application.run(args);
	}

}
