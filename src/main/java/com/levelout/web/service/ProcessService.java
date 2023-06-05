package com.levelout.web.service;

import com.levelout.web.config.BimServerClientWrapper;
import com.levelout.web.enums.ProcessStatusType;
import com.levelout.web.enums.ProcessType;
import com.levelout.web.model.ProcessModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.interfaces.objects.*;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import java.io.IOException;
import java.util.*;

@Service
public class ProcessService {
    final static Log logger = LogFactory.getLog(ProcessService.class);
    @Autowired
    BimServerClientWrapper bimServerClient;

    @Autowired
    AsyncCheckinService asyncCheckinService;

    @Value("#{${server.reports.serializers}}")
    private Map<String,Long> serializers;

    public ProcessModel prepareCheckIn(long projectId) throws Exception {
        SDeserializerPluginConfiguration pluginConfig = bimServerClient.getServiceInterface().getSuggestedDeserializerForExtension("ifc", projectId);
        long topicId = bimServerClient.getServiceInterface().initiateCheckin(projectId, pluginConfig.getOid());
        logger.info("CheckIn Process Prepared");
        //checkIn(projectId, bimFile, topicId);
        return getProcessStatus(projectId, topicId);
    }

    public ProcessModel checkIn(long projectId, MultipartFile bimFile, long topicId) throws UserException, ServerException, IOException {
        SDeserializerPluginConfiguration pluginConfig = bimServerClient.getServiceInterface().getSuggestedDeserializerForExtension("ifc", projectId);
        logger.info("Starting CheckIn Process now");
        asyncCheckinService.checkInAsync(projectId, bimFile, pluginConfig, topicId);
        logger.info("CheckIn Process Added to Queue");
        return getProcessStatus(projectId, topicId);
    }

    public ProcessModel getProcessStatus(long projectId, long topicId) throws ServerException, UserException {
        SLongActionState progress = bimServerClient.getRegistry().getProgress(topicId);
        return mapToProcessDto(projectId, topicId, progress);
    }

    private ProcessModel mapToProcessDto(long projectId, long topicId, SLongActionState progress) {
        ProcessModel process = new ProcessModel();
        process.setProjectId(projectId);
        process.setTopicId(topicId);
        process.setProcessStatusType(ProcessStatusType.IN_PROGRESS);
        process.setProcessType(ProcessType.CHECK_IN);
        process.setPercentage(progress==null?1:progress.getProgress());
        process.setActionState(progress==null?SActionState.STARTED:progress.getState());
        process.setTitle(progress==null?"PROCESS IN QUEUE":progress.getTitle());
        process.setRevisionId(progress==null?0L:progress.getRid());
        return process;
    }

    public void getProgress(Long projectId) throws Exception {
        SProject project = bimServerClient.getServiceInterface().getProjectByPoid(projectId);
        bimServerClient.getServiceInterface().cleanupLongAction(project.getLastRevisionId());
    }

    public Map<Long, List<ProcessModel>> getStatusOfProcessesForProject(Long projectId) throws ServerException, UserException {
        Map<Long, List<ProcessModel>> processMap = new HashMap<>();
        bimServerClient.getRegistry()
                .getProgressTopicsOnProject(projectId)
                .stream()
                .map(topicId-> {
                    try {
                        return getProcessStatus(projectId, topicId);
                    } catch (ServerException e) {
                        throw new RuntimeException(e);
                    } catch (UserException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(processModel -> {
                    List<ProcessModel> processesForRevision = processMap.get(processModel.getRevisionId());
                    if(processesForRevision==null) {
                        processesForRevision = new ArrayList<>();
                        processMap.put(processModel.getRevisionId(), processesForRevision);
                    }
                    processesForRevision.add(processModel);
                });

        return processMap;
    }

    public ProcessModel initSerialization(Long revisionId, String serializerName, long projectId) throws ServerException, UserException {
        String query = "{\"doublebuffer\":true,\"defines\":{\"AllFields\":{\"includeAllFields\":true,\"includes\":" +
                "[\"AllFields\",{\"type\":\"IfcProduct\",\"field\":\"geometry\",\"include\":{\"type\":\"GeometryInfo\"," +
                "\"field\":\"data\",\"include\":{\"type\":\"GeometryData\",\"fields\":[\"indices\",\"vertices\"," +
                "\"normals\",\"colorsQuantized\"]}}}]}},\"queries\":[{\"includeAllFields\":true,\"include\":" +
                "\"AllFields\"}]}";
        // Commenting as getSerializerByName is not working
        //SSerializerPluginConfiguration serializer = bimServerClient.getServiceInterface().getSerializerByName(serializerName);
        SSerializerPluginConfiguration serializer = bimServerClient.getServiceInterface().getSerializerById(serializers.get(serializerName));
        long topicId = bimServerClient
                .getServiceInterface().download(new HashSet<>(Arrays.asList(revisionId)), query, serializer.getOid(), false);
        return getProcessStatus(projectId, topicId);
    }

    public DataHandler downloadSerialized(long topicId) throws ServerException, UserException {
        return bimServerClient
                .getServiceInterface().getDownloadData(topicId).getFile();
    }
}
