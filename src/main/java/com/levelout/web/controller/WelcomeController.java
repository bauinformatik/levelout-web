package com.levelout.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.levelout.web.model.Project;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WelcomeController {

	@Value("${welcome.message}")
	private String message;

	@GetMapping("/")
	public String main(@RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {
		List<Project> projects = new ArrayList<>();
		Project project = new Project();
		project.setId(100000001L);
		project.setName("LeveloutFirstUpload");
		project.setAuthor("Helga");
		projects.add(project);

		project = new Project();
		project.setId(100000002L);
		project.setName("LeveloutSecondUpload");
		project.setAuthor("Amol");
		projects.add(project);

		model.addAttribute("message", name == null || name.isEmpty() ? message : name);
		model.addAttribute("projects", projects);

		return "welcome";
	}
}