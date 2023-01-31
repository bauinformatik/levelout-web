package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
import com.levelout.web.model.ProcessDto;
import com.levelout.web.service.ProcessService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ProcessController {
    final static Log logger = LogFactory.getLog(ProcessController.class);
    @Autowired
    ProcessService processService;

    @PostMapping("/process/checkIn/{projectId}")
    public ResponseEntity<ProcessDto> checkIn(
            @PathVariable long projectId, @RequestParam MultipartFile file
    ) throws Exception {
        logger.info("CheckIn process about to start for projectId: "+projectId);
        return ResponseEntity.ok(processService.checkIn(projectId, file));
    }

    @GetMapping("/process/status/{projectId}/{topicId}")
    public ResponseEntity<ProcessDto> getProcessStatus(
            @PathVariable long projectId, @PathVariable long topicId
    ) throws Exception {
        return ResponseEntity.ok(processService.getProcessStatus(projectId, topicId));
    }

    @GetMapping("/process/progress/{projectId}")
    public ResponseEntity<String> getProcessTopicsForProject(
            @PathVariable long projectId, @PathVariable long topicId
    ) throws Exception {
        processService.getProgress(projectId);
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }

    @GetMapping("/process/progress")
    public ResponseEntity<String> getAllProcessTopics(
            @PathVariable long projectId, @PathVariable long topicId
    ) throws Exception {
        processService.getProgress(projectId);
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }
}