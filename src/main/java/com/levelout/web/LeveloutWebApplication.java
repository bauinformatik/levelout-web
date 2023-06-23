package com.levelout.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.SessionTrackingMode;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class LeveloutWebApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(LeveloutWebApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LeveloutWebApplication.class);
	}

	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return servletContext -> {
			servletContext.getSessionCookieConfig().setHttpOnly(true);
			servletContext.setSessionTrackingModes(Set.of(SessionTrackingMode.COOKIE));
		};
	}
}
