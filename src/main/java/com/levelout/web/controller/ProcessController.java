package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
public class ProcessController {
    final static Log logger = LogFactory.getLog(ProcessController.class);
    @Autowired
    ProcessService processService;

    @Autowired
    ProjectService projectService;

    @PostMapping("/process/checkIn/{projectId}")
    public ResponseEntity<ProcessDto> checkIn(
            @PathVariable long projectId, @RequestParam MultipartFile file
    ) throws Exception {
        return checkInForSchema(projectId, file, IfcSchema.ifc4);
    }

    @PostMapping("/process/checkIn/{projectId}/{schema}")
    public ResponseEntity<ProcessDto> checkIn(
            @PathVariable long projectId, @PathVariable IfcSchema schema, @RequestParam MultipartFile file
    ) throws Exception {
        return checkInForSchema(projectId, file, schema);
    }

    private ResponseEntity<ProcessDto> checkInForSchema(long projectId, MultipartFile file, IfcSchema schema) throws Exception {
        boolean isNew = false;
        if(projectId ==0) {
            String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
            ProjectDto project = new ProjectDto();
            project.setDescription(fileNameWithOutExt);
            project.setName(fileNameWithOutExt+"_"+ DateTimeUtils.getCurrentlyDateTime());
            project.setSchema(schema);
            project.setExportLengthMeasurePrefix(SSIPrefix.meter);
            projectService.createProject(project);
            projectId = project.getProjectId();
            isNew = true;
        }
        logger.info("CheckIn process about to start for projectId: "+ projectId);
        return ResponseEntity.ok(processService.checkIn(projectId, file, isNew));
    }

    @GetMapping("/process/status/{projectId}/{topicId}")
    public ResponseEntity<ProcessDto> getProcessStatus(
            @PathVariable long projectId, @PathVariable long topicId
    ) throws Exception {
        return ResponseEntity.ok(processService.getProcessStatus(projectId, topicId));
    }

    @GetMapping("/process/progress/{projectId}")
    public ResponseEntity<String> getProcessTopicsForProject(
            @PathVariable long projectId
    ) throws Exception {
        processService.getProgress(projectId);
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }

    @GetMapping("/process/progress")
    public ResponseEntity<String> getAllProcessTopics(
            @PathVariable long projectId
    ) throws Exception {
        processService.getProgress(projectId);
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }

    public static void main(String args[]) {
        List<Integer> num = Arrays.asList(1,2,3,4);
        num.stream().filter(s->s%2==1).mapToInt(s->s.intValue()).sum();
        System.out.println(

        );
    }
}