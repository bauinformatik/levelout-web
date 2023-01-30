package com.levelout.web.service;

import com.levelout.web.enums.ProcessStatusType;
import com.levelout.web.enums.ProcessType;
import com.levelout.web.model.ProcessDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.client.BimServerClient;
import org.bimserver.interfaces.objects.*;
import org.bimserver.plugins.services.ProgressHandler;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProcessService {
    final static Log logger = LogFactory.getLog(ProcessService.class);
    @Autowired
    BimServerClient bimServerClient;

    @Autowired
    AsyncProcessService asyncProcessService;

    public ProcessDto checkIn(long projectId, MultipartFile bimFile) throws Exception {
        SDeserializerPluginConfiguration pluginConfig = bimServerClient.getServiceInterface().getSuggestedDeserializerForExtension("ifc", projectId);
        SProject project = bimServerClient.getServiceInterface().getProjectByPoid(projectId);
        long topicId = bimServerClient.getServiceInterface().initiateCheckin(projectId, pluginConfig.getOid());
        asyncProcessService.checkInAsync(projectId, bimFile, pluginConfig, project, topicId);
        return mapToProcessDto(projectId, topicId, 0);
    }

    public ProcessDto getProcessStatus(long projectId, long topicId) {
        class MyProgressHandler implements ProgressHandler {
            private int progress=-1000;
            @Override
            public void progress(SLongActionState state) {
                progress = state.getProgress();
            }
            public int getProgress() {
                return progress;
            }
        }
        MyProgressHandler progressHandler = new MyProgressHandler();
        bimServerClient.getNotificationsManager().registerProgressHandler(topicId, progressHandler);
        while(progressHandler.getProgress()==-1000) {

        }
        bimServerClient.getNotificationsManager().unregisterProgressHandler(topicId, progressHandler);
        return mapToProcessDto(projectId, topicId, progressHandler.getProgress());
    }

    private ProcessDto mapToProcessDto(long projectId, long topicId, int progress) {
        ProcessDto process = new ProcessDto();
        process.setProjectId(projectId);
        process.setProcessStatusType(ProcessStatusType.IN_PROGRESS);
        process.setProcessType(ProcessType.CHECK_IN);
        process.setTopicId(topicId);
        process.setPercentage(progress);
        return process;
    }
}
