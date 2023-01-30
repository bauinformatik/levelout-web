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
        return ResponseEntity.ok(processService.checkIn(projectId, file));
    }

    @GetMapping("/process/status/{projectId}/{topicId}")
    public ResponseEntity<ProcessDto> getProcessStatus(
            @PathVariable long projectId, @PathVariable long topicId
    ) throws Exception {
        return ResponseEntity.ok(processService.getProcessStatus(projectId, topicId));
    }
//
//    /**
//     * Location to save uploaded files on server
//     */
//    private static String UPLOAD_PATH = "F:\\data\\test\\upload";
//    @PostMapping("/api/fileupload")
//    public void uploadFile(@RequestParam("multipartFile") MultipartFile uploadfile) {
//
//        if (uploadfile.isEmpty()) {
//            System.out.println("please select a file!");
//        }
//
//        try {
//            byte[] bytes = uploadfile.getBytes();
//            Path path = Path.of(UPLOAD_PATH, uploadfile.getOriginalFilename());
//            Files.write(path, bytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}