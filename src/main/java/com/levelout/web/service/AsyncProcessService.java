package com.levelout.web.service;

import com.levelout.web.enums.ProcessStatusType;
import com.levelout.web.enums.ProcessType;
import com.levelout.web.model.ProcessDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.client.BimServerClient;
import org.bimserver.interfaces.objects.SDeserializerPluginConfiguration;
import org.bimserver.interfaces.objects.SLongActionState;
import org.bimserver.interfaces.objects.SLongCheckinActionState;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.plugins.services.ProgressHandler;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AsyncProcessService {
    final static Log logger = LogFactory.getLog(AsyncProcessService.class);
    @Autowired
    BimServerClient bimServerClient;

    Map<Long, MyProgressHandler> progressHandlerMap = new HashMap<>();

    @Async
    public MyProgressHandler checkInAsync(long projectId, MultipartFile bimFile, SDeserializerPluginConfiguration pluginConfig, SProject project, long topicId, boolean isNew) throws UserException, ServerException, IOException {
        MyProgressHandler progressHandler = new MyProgressHandler();
        bimServerClient.getNotificationsManager().registerProgressHandler(topicId, progressHandler);
        progressHandlerMap.put(isNew?0:projectId, progressHandler);
        bimServerClient.checkinAsync(
                projectId, project.getDescription(), pluginConfig.getOid(), false,
                bimFile.getSize(), bimFile.getName(), bimFile.getInputStream(), topicId
        );
        bimServerClient.getNotificationsManager().unregisterProgressHandler(topicId, progressHandler);
        return progressHandler;
    }

    public ProcessDto getProgressHandler(long projectId, long topicId) {
        MyProgressHandler progressHandler = getMyProgressHandler(projectId);
        return mapToProcessDto(projectId, topicId, progressHandler.getState());
    }

    public MyProgressHandler getMyProgressHandler(long projectId) {
        MyProgressHandler progressHandler = progressHandlerMap.get(projectId);
        return progressHandler;
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
}
