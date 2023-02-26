package com.levelout.web.controller;

import com.levelout.web.model.RevisionDto;
import com.levelout.web.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {

	@Value("${welcome.message}")
	private String message;

	@Autowired
	ProjectService projectService;

	@Autowired
	HttpSession session;

	@GetMapping("/")
	public String home(Model model) {
		try {
			model.addAttribute("message", message);
		} catch (Exception e) {
			return "error";
		}
		return "welcome";
	}

	@GetMapping("/myProject")
	public String myProject(
			@RequestParam(name = "projectId") Long projectId,
			Model model) {
		try {
			projectId = (Long) session.getAttribute("projectId");
			List<RevisionDto> revisions = projectId== null ? List.of() : projectService.getAllRevisions(projectId);
			model.addAttribute("message", message);
			model.addAttribute("revisions", revisions);
			model.addAttribute("projectId", projectId);
		} catch (Exception e) {
			return "error";
		}
		return "revisions";
	}
}