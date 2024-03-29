package com.levelout.web.controller;

import com.levelout.web.constants.CommonConstants;
import com.levelout.web.enums.IfcSchema;
import com.levelout.web.model.DownloadStreamModel;
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

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProcessController {
    final static Log logger = LogFactory.getLog(ProcessController.class);
    @Autowired
    ProcessService processService;

    @Autowired
    ProjectService projectService;

    @Autowired
    TransactionDataService transactionDataService;

    @PostMapping("/process/prepareCheckIn")
    public ResponseEntity<ProcessModel> checkIn(
            @RequestParam String file
    ) throws Exception {
        return checkInForSchema(file, IfcSchema.ifc4);
    }

    @PostMapping("/process/prepareCheckIn/{schema}")
    public ResponseEntity<ProcessModel> checkIn(
            @PathVariable IfcSchema schema, @RequestParam String file
    ) throws Exception {
        return checkInForSchema(file, schema);
    }

    @PostMapping("/process/checkIn/{topicId}")
    public ResponseEntity<ProcessModel> checkIn(
            @PathVariable long topicId,
            @RequestParam MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(
                processService.checkIn(
                        transactionDataService.getTransactionData().getProjectId(), file, topicId
                )
        );
    }

    private ResponseEntity<ProcessModel> checkInForSchema(String file, IfcSchema schema) throws Exception {
        TransactionDataModel transactionData = transactionDataService.getTransactionData();
        if(transactionData==null) {
            String fileNameWithOutExt = FilenameUtils.removeExtension(file);
            ProjectModel project = new ProjectModel();
            project.setDescription(fileNameWithOutExt);
            project.setName(fileNameWithOutExt+"_"+ DateTimeUtils.getCurrentlyDateTime());
            project.setSchema(schema);
            project.setExportLengthMeasurePrefix(SSIPrefix.meter);
            projectService.createProject(project);
            transactionData = transactionDataService.setTransactionData(project.getProjectId());
        }
        logger.info("CheckIn process about to start for projectId: "+ transactionData.getProjectId());
        return ResponseEntity.ok(processService.prepareCheckIn(transactionData.getProjectId()));
    }

    @GetMapping("/process/status/{topicId}")
    public ResponseEntity<ProcessModel> getProcessStatus(
            @PathVariable long topicId
    ) throws Exception {
        return ResponseEntity.ok(processService.getProcessStatus(transactionDataService.getTransactionData().getProjectId(), topicId));
    }

    @GetMapping("/process/services/status")
    public ResponseEntity<Map<Long, List<ProcessModel>>> getStatusForAll() throws Exception {
        return ResponseEntity.ok(
                processService.getStatusOfProcessesForProject(
                        transactionDataService.getProjectId()
                )
        );
    }

    @PostMapping("/process/download/{revisionId}/{serializer}")
    public ResponseEntity<String> download(
            @PathVariable Long revisionId, @PathVariable String serializer, HttpServletResponse response
    ) throws Exception {
        TransactionDataModel transactionData = transactionDataService.getTransactionData();
        DownloadStreamModel downloadStream = processService.download(revisionId, serializer);
        byte[] dataArray = downloadStream.getInputStream().readAllBytes();
        response.setContentLength(dataArray.length);
        response.setContentType(downloadStream.getContentType());
        response.setHeader("Content-Disposition", "attachment; filename="
                +projectService.getRevision(revisionId).getDescription()+"."+downloadStream.getExtension());
        response.getOutputStream().write(dataArray);
        processService.cleanupOnTopic(downloadStream.getTopicId());
        return ResponseEntity.ok(CommonConstants.SUCCESS);
    }

    @GetMapping("/process/progressTopics")
    public ResponseEntity<Map<Long, List<ProcessModel>>> progressTopics(
    ) throws Exception {
        if(transactionDataService.getTransactionData()==null) {
            return ResponseEntity.ok(new HashMap<>());
        }
        long projectId = transactionDataService.getTransactionData().getProjectId();
        return ResponseEntity.ok(
                processService.getProgressTopics(
                        projectId,
                        projectService.getAllRevisions(projectId)
                )
        );
    }

}