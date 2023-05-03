package com.levelout.web.controller;

import com.levelout.web.model.RevisionModel;
import com.levelout.web.service.ProjectService;
import com.levelout.web.service.TransactionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

	@Autowired
	TransactionDataService transactionDataService;

	@Value("${welcome.message}")
	private String message;

	@Autowired
	ProjectService projectService;

	@GetMapping("/")
	public String home(Model model, @RequestParam(defaultValue = "") String initAction) {
		try {
			model.addAttribute("message", message);
			model.addAttribute("initAction", initAction);
			model.addAttribute("projectId", transactionDataService.getProjectId());
			model.addAttribute("revisions",
					transactionDataService.getProjectId()==null || transactionDataService.getProjectId()==0 ?
							new ArrayList<>() :
							projectService.getAllRevisions(transactionDataService.getProjectId())
			);
		} catch (Exception e) {
			return "error";
		}
		return "revisions_ajax";
	}

	@GetMapping("/home")
	public String home1(Model model) {
		try {
			model.addAttribute("message", message);
		} catch (Exception e) {
			return "error";
		}
		return "welcome";
	}

	@GetMapping("/project")
	public String myProject(Model model) {
		try {
			Long projectId = transactionDataService.getProjectId();
			List<RevisionModel> revisions = projectService.getAllRevisions(projectId);
			model.addAttribute("message", message);

			if(projectId==null || projectId==0)
				return "welcome";

			model.addAttribute("revisions", revisions);
			model.addAttribute("projectId", projectId);
		} catch (Exception e) {
			return "error";
		}
		return "revisions";
	}
}