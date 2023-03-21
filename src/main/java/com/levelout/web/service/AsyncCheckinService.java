package com.levelout.web.service;

import com.levelout.web.config.BimServerClientWrapper;
import com.levelout.web.utils.DateTimeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    BimServerClientWrapper bimServerClient;

    @Async
    public void checkInAsync(long projectId, MultipartFile bimFile, SDeserializerPluginConfiguration pluginConfig, long topicId) throws UserException, ServerException, IOException {
        logger.info("Starting check-in process for the project: "+projectId);
        String revisionDescription = bimFile.getOriginalFilename() + " uploaded at " + DateTimeUtils.getCurrentlyDateTime();
        try {
            bimServerClient.checkinAsync(
                    projectId, revisionDescription, pluginConfig.getOid(), false,
                    bimFile.getSize(), bimFile.getName(), bimFile.getInputStream(), topicId
            );
        } catch (RuntimeException e) {
            logger.error("Check-in process could not be initiated due to runtime error: "+e.getMessage(), e);
            throw e;
        } finally {
            bimServerClient.getServiceInterface().cleanupLongAction(topicId);
        }
        logger.info("Finished check-in process for the project: "+projectId);
    }
}
