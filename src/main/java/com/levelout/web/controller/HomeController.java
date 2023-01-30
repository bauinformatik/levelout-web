package com.levelout.web.controller;

import com.levelout.web.model.ProjectDto;
import com.levelout.web.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

	@Value("${welcome.message}")
	private String message;

	@Autowired
	ProjectService projectService;

	@GetMapping("/")
	public String home(@RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {
		try {
			List<ProjectDto> projects = projectService.getAllProjects();
			model.addAttribute("message", name == null || name.isEmpty() ? message : name);
			model.addAttribute("projects", projects);
		} catch (Exception e) {
			return "error";
		}
		return "welcome";
	}
}