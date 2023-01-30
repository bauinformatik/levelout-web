package com.levelout.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.client.BimServerClient;
import org.bimserver.interfaces.objects.SDeserializerPluginConfiguration;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AsyncProcessService {
    final static Log logger = LogFactory.getLog(AsyncProcessService.class);
    @Autowired
    BimServerClient bimServerClient;

    @Async
    public void checkInAsync(long projectId, MultipartFile bimFile, SDeserializerPluginConfiguration pluginConfig, SProject project, long topicId) throws UserException, ServerException, IOException {
        bimServerClient.checkinAsync(
                projectId, project.getDescription(), pluginConfig.getOid(), false,
                bimFile.getSize(), bimFile.getName(), bimFile.getInputStream(), topicId
        );
    }
}
