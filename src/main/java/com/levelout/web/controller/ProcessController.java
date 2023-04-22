package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
import com.levelout.web.enums.IfcSchema;
import com.levelout.web.model.ProcessModel;
import com.levelout.web.model.ProjectModel;
import com.levelout.web.model.TransactionDataModel;
import com.levelout.web.service.ProcessService;
import com.levelout.web.service.ProjectService;
import com.levelout.web.service.TransactionDataService;
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

    @Autowired
    TransactionDataService transactionDataService;

    @PostMapping("/process/checkIn")
    public ResponseEntity<ProcessModel> checkIn(
            @RequestParam MultipartFile file
    ) throws Exception {
        return checkInForSchema(file, IfcSchema.ifc4);
    }

    @PostMapping("/process/checkIn/{schema}")
    public ResponseEntity<ProcessModel> checkIn(
            @PathVariable IfcSchema schema, @RequestParam MultipartFile file
    ) throws Exception {
        return checkInForSchema(file, schema);
    }

    private ResponseEntity<ProcessModel> checkInForSchema(MultipartFile file, IfcSchema schema) throws Exception {
        boolean isNew = false;
        TransactionDataModel transactionData = transactionDataService.getTransactionData();
        if(transactionData==null) {
            String fileNameWithOutExt = FilenameUtils.removeExtension(file.getOriginalFilename());
            ProjectModel project = new ProjectModel();
            project.setDescription(fileNameWithOutExt);
            project.setName(fileNameWithOutExt+"_"+ DateTimeUtils.getCurrentlyDateTime());
            project.setSchema(schema);
            project.setExportLengthMeasurePrefix(SSIPrefix.meter);
            projectService.createProject(project);
            transactionData = transactionDataService.setTransactionData(project.getProjectId());
            isNew = true;
        }
        logger.info("CheckIn process about to start for projectId: "+ transactionData.getProjectId());
        return ResponseEntity.ok(processService.checkIn(transactionData.getProjectId(), file, isNew));
    }

    @GetMapping("/process/status/{topicId}")
    public ResponseEntity<ProcessModel> getProcessStatus(
            @PathVariable long topicId
    ) throws Exception {
        return ResponseEntity.ok(processService.getProcessStatus(transactionDataService.getTransactionData().getProjectId(), topicId));
    }

    @GetMapping("/process/progress")
    public ResponseEntity<String> getProcessTopicsForProject() throws Exception {
        processService.getProgress(transactionDataService.getTransactionData().getProjectId());
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }

    public static void main(String args[]) {
        List<Integer> num = Arrays.asList(1,2,3,4);
        num.stream().filter(s->s%2==1).mapToInt(s->s.intValue()).sum();
        System.out.println(

        );
    }
}