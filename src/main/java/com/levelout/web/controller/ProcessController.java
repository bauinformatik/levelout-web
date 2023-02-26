package com.levelout.web.controller;

import com.levelout.web.enums.IfcSchema;
import com.levelout.web.model.ProcessDto;
import com.levelout.web.model.ProjectDto;
import com.levelout.web.service.ProcessService;
import com.levelout.web.service.ProjectService;
import com.levelout.web.utils.DateTimeUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.interfaces.objects.SSIPrefix;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@RestController
public class ProcessController {
    final static Log logger = LogFactory.getLog(ProcessController.class);
    @Autowired
    ProcessService processService;

    @Autowired
    HttpSession session;

    @Autowired
    ProjectService projectService;

    @PostMapping("/process/checkIn")
    public ResponseEntity<ProcessDto> checkIn(
             @RequestParam MultipartFile file
    ) throws Exception {
        Long projectId = (Long) session.getAttribute("projectId");
        if (projectId == null){
            projectId = createProject(file, IfcSchema.ifc4);
            session.setAttribute("projectId", projectId);
        }
        logger.info("CheckIn process about to start for projectId: "+ projectId);
        return ResponseEntity.ok(processService.checkIn(projectId, file));
    }

    private long createProject(MultipartFile file, IfcSchema schema) throws ServerException, UserException {
        long projectId;
        String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
        ProjectDto project = new ProjectDto();
        project.setDescription(fileNameWithOutExt);
        project.setName(fileNameWithOutExt+"_"+ DateTimeUtils.getCurrentlyDateTime());
        project.setSchema(schema);
        project.setExportLengthMeasurePrefix(SSIPrefix.meter);
        projectService.createProject(project);
        projectId = project.getProjectId();
        return projectId;
    }

    @GetMapping("/process/status/{topicId}")
    public ResponseEntity<ProcessDto> getProcessStatus(
            @PathVariable long topicId
    ) throws Exception {
        // TODO get the topicId from projectId (session)
        return ResponseEntity.ok(processService.getProcessStatus(topicId));
    }

}