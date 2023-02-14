package com.levelout.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.client.BimServerClient;
import org.bimserver.interfaces.objects.*;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AsyncCheckinService {
    final static Log logger = LogFactory.getLog(AsyncCheckinService.class);
    @Autowired
    BimServerClient bimServerClient;

    @Async
    public void checkInAsync(long projectId, MultipartFile bimFile, SDeserializerPluginConfiguration pluginConfig, SProject project, long topicId, boolean isNew) throws UserException, ServerException, IOException {
        logger.info("Starting check-in process for the project: "+projectId);
        bimServerClient.checkinAsync(
                projectId, project.getDescription(), pluginConfig.getOid(), false,
                bimFile.getSize(), bimFile.getName(), bimFile.getInputStream(), topicId
        );
        logger.info("Finished check-in process for the project: "+projectId);
    }
}
