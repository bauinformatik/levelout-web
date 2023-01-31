package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
import com.levelout.web.model.ProjectDto;
import com.levelout.web.service.ProjectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjectController {
    final static Log logger = LogFactory.getLog(ProjectController.class);

    @Autowired
    ProjectService projectService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectDto> get(@PathVariable long projectId) throws Exception {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @GetMapping("/project/projectName/{projectName}")
    public ResponseEntity<List<ProjectDto>> get(@PathVariable String projectName) throws Exception {
        return ResponseEntity.ok(projectService.getProjectsByName(projectName));
    }

    @PutMapping(value = "/project")
    public ResponseEntity<ProjectDto> create(@RequestBody ProjectDto projectDto) throws Exception {
        projectService.createProject(projectDto);
        return ResponseEntity.ok(projectDto);
    }

	@PostMapping(value = "/project")
    public ResponseEntity<ProjectDto> update(@RequestBody ProjectDto projectDto) throws Exception {
        projectService.updateProject(projectDto);
        return ResponseEntity.ok(projectDto);
    }

    @DeleteMapping("/project")
    public ResponseEntity<String> delete(@RequestBody List<Long> projectIds) throws HttpException {
        List<Long> successIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();
        projectIds.forEach(projectId-> {
            try {
                if(projectService.deleteProject(projectId))
                    successIds.add(projectId);
                else
                    failedIds.add(""+projectId);
            } catch (Exception e) {
                failedIds.add(projectId + ": " + e.getMessage());
            }
        });
        logger.info("Successful Ids: " + successIds);
        if(failedIds.size()>0) {
            logger.info("Failed Ids: " + failedIds);
            throw new HttpException(failedIds.toString());
        }
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<String> delete(@PathVariable long projectId) throws Exception {
        if(projectService.deleteProject(projectId))
            return ResponseEntity.ok(CommonConstants.SUCCESS);

        throw new HttpException("Error while deleting the project with id: "+projectId);
    }

    @DeleteMapping("/project/projectName/{projectName}")
    public ResponseEntity<String> deleteByProjectName(@PathVariable String projectName) throws Exception {
        List<String> failedIds = projectService.deleteProjectsByName(projectName);
        if(failedIds.size()>0) {
            logger.info("Failed Ids: " + failedIds);
            throw new HttpException(failedIds.toString());
        }
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }
}