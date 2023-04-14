package com.levelout.web.service;

import com.levelout.web.config.BimServerClientWrapper;
import org.bimserver.interfaces.objects.*;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

/**
 * Created by akiajk on 4/4/2017.
 */
@Service
public class PluginService {

    @Autowired
    BimServerClientWrapper bimServerClient;

    public void addLevelOutServiceToProject(SProject project) throws ServerException, UserException {
        Optional<SServiceDescriptor> levelOut = bimServerClient.getServiceInterface().getAllLocalServiceDescriptors().stream().filter((serviceDescriptor -> serviceDescriptor.getName().startsWith("LevelOut"))).findFirst();
        if (levelOut.isEmpty()) Assert.fail("service not found");
        SServiceDescriptor serviceDescriptor = levelOut.get();
        SProfileDescriptor profile = bimServerClient.getServiceInterface().getAllLocalProfiles(serviceDescriptor.getIdentifier()).get(0);
        SExtendedDataSchema writeExtendedDataSchema = bimServerClient.getServiceInterface().getExtendedDataSchemaByName(serviceDescriptor.getWriteExtendedData());
        SService service = createService(serviceDescriptor, profile, writeExtendedDataSchema);
        bimServerClient.getServiceInterface().addLocalServiceToProject(project.getOid(), service, Long.valueOf(service.getProfileIdentifier()));
    }

    private static SService createService(SServiceDescriptor serviceDescriptor, SProfileDescriptor profile, SExtendedDataSchema writeExtendedDataSchema) {
        SService service = new SService();
        service.setName(serviceDescriptor.getName()+ new Random().nextInt(1000)); // This must be unique in project
        service.setProviderName(serviceDescriptor.getProviderName());
        service.setServiceName(serviceDescriptor.getName());
        service.setServiceIdentifier(serviceDescriptor.getIdentifier());
        service.setUrl(serviceDescriptor.getUrl());
        service.setToken(serviceDescriptor.getToken());
        service.setNotificationProtocol(serviceDescriptor.getNotificationProtocol());
        service.setDescription(serviceDescriptor.getDescription());
        service.setTrigger(serviceDescriptor.getTrigger());
        service.setProfileIdentifier(profile.getIdentifier());
        service.setProfileName(profile.getName());
        service.setProfileDescription(profile.getDescription());
        service.setProfilePublic(profile.isPublicProfile());
        service.setReadRevision(serviceDescriptor.isReadRevision());
        service.setWriteRevisionId(-1);
        service.setWriteExtendedDataId(writeExtendedDataSchema.getOid());
        return service;
    }

}
