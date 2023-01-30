package com.levelout.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAutoConfiguration
@SpringBootApplication
@EnableAsync
public class LeveloutWebApplication {

	/**
	 * 
	 * @param args : runtime arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(LeveloutWebApplication.class, args);
	}
}
