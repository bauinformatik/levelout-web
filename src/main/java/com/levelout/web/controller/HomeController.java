package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
import com.levelout.web.service.ProjectService;
import com.levelout.web.service.TransactionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class HomeController {

	@Autowired
	TransactionDataService transactionDataService;

	@Value("${welcome.message}")
	private String message;

	@Autowired
	ProjectService projectService;

	@GetMapping("/")
	public String root(Model model, @RequestParam(defaultValue = "") String initAction) {
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
		return "revisions";
	}

	@GetMapping("/home")
	public String home(Model model, @RequestParam(defaultValue = "") String initAction) {
		return root(model, initAction);
	}

	// Added only for testing purpose.
	@GetMapping("/setProject/{projectId}")
	public ResponseEntity<String> setTransactionData(@PathVariable Long projectId) {
		transactionDataService.setTransactionData(projectId);
		return ResponseEntity.ok(CommonConstants.SUCCESS);
	}
}