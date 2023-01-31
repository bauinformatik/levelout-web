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
        logger.info("CheckIn Process Initialized");
        asyncProcessService.checkInAsync(projectId, bimFile, pluginConfig, project, topicId);
        logger.info("CheckIn Process Started");
        return getProcessStatus(projectId, topicId);
    }

    public ProcessDto getProcessStatus(long projectId, long topicId) throws ServerException, UserException {
        MyProgressHandler progressHandler = new MyProgressHandler();
        bimServerClient.getNotificationsManager().registerProgressHandler(topicId, progressHandler);
        final SLongActionState state = getActionState(progressHandler);
        if(state.getProgress()>=100 && state.getState()==SActionState.FINISHED) {
            bimServerClient.getServiceInterface().cleanupLongAction(topicId);
        }
        bimServerClient.getNotificationsManager().unregisterProgressHandler(topicId, progressHandler);
        return mapToProcessDto(projectId, topicId, state);
    }

    private SLongActionState getActionState(MyProgressHandler progressHandler) {
        while(progressHandler.getState() ==null) {

        }
        // Don't Move next line above the while loop. It will assign null to progress always
        final SLongActionState state = progressHandler.getState();
        return state;
    }

    public void getProgress(Long projectId) throws Exception {
        SProject project = bimServerClient.getServiceInterface().getProjectByPoid(projectId);
        bimServerClient.getServiceInterface().cleanupLongAction(project.getLastRevisionId());
    }

    private ProcessDto mapToProcessDto(long projectId, long topicId, SLongActionState progress) {
        ProcessDto process = new ProcessDto();
        process.setProjectId(projectId);
        process.setProcessStatusType(ProcessStatusType.IN_PROGRESS);
        process.setProcessType(ProcessType.CHECK_IN);
        process.setTopicId(topicId);
        process.setPercentage(progress.getProgress());
        process.setActionState(progress.getState());
        process.setTitle(progress.getTitle());
        return process;
    }

    class MyProgressHandler implements ProgressHandler {
        private SLongActionState state =null;
        @Override
        public void progress(SLongActionState state) {
            this.state = state;
        }
        public SLongActionState getState() {
            return state;
        }
    }
}
