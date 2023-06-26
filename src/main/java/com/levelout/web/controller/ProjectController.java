package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
import com.levelout.web.model.ProjectModel;
import com.levelout.web.model.RevisionModel;
import com.levelout.web.service.ProjectService;
import com.levelout.web.service.TransactionDataService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.bimserver.interfaces.objects.SFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjectController {
    final static Log logger = LogFactory.getLog(ProjectController.class);

    @Autowired
    ProjectService projectService;

    @Autowired
    TransactionDataService transactionDataService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectModel> get(@PathVariable long projectId) throws Exception {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @GetMapping("/project/revisions")
    public ResponseEntity<List<RevisionModel>> getRevisions() throws Exception {
        return ResponseEntity.ok(projectService.getAllRevisions(transactionDataService.getProjectId()));
    }

    @GetMapping("/project/projectName/{projectName}")
    public ResponseEntity<List<ProjectModel>> get(@PathVariable String projectName) throws Exception {
        return ResponseEntity.ok(projectService.getProjectsByName(projectName));
    }

    @PutMapping(value = "/project")
    public ResponseEntity<ProjectModel> create(@RequestBody ProjectModel projectModel) throws Exception {
        projectService.createProject(projectModel);
        return ResponseEntity.ok(projectModel);
    }

	@PostMapping(value = "/project")
    public ResponseEntity<ProjectModel> update(@RequestBody ProjectModel projectModel) throws Exception {
        projectService.updateProject(projectModel);
        return ResponseEntity.ok(projectModel);
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

    @GetMapping("/project/download/report/{reportId}")
    public ResponseEntity<String> downloadReport(@PathVariable Integer reportId, HttpServletResponse response) throws Exception {
        SFile reportData = projectService.getReportData(reportId);
        byte[] reportArray = reportData.getData();
        response.setContentLength(reportArray.length);
        response.setContentType(reportData.getMime());
        response.setHeader("Content-Disposition", "attachment; filename="+reportData.getFilename());
        response.getOutputStream().write(reportArray);
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }
}