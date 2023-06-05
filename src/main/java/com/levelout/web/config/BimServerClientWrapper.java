package com.levelout.web.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.client.BimServerClient;
import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.shared.ChannelConnectionException;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.ServiceException;
import org.bimserver.shared.exceptions.UserException;
import org.bimserver.shared.interfaces.NotificationRegistryInterface;
import org.bimserver.shared.interfaces.PluginInterface;
import org.bimserver.shared.interfaces.ServiceInterface;

import java.io.InputStream;

public class BimServerClientWrapper {
    final static Log logger = LogFactory.getLog(BimServerClientWrapper.class);

    private JsonBimServerClientFactory bimServerClientFactory;

    private String host;

    private String user;

    private String password;

    private BimServerClient client;

    protected BimServerClientWrapper(JsonBimServerClientFactory bimServerClientFactory, String host, String user, String password) {
        this.bimServerClientFactory = bimServerClientFactory;
        this.host = host;
        this.user = user;
        this.password = password;
        this.client = null;
    }

    private BimServerClient getClient() {
        if(this.client!=null)
            return this.client;

        logger.debug("Creating BIM client for host: " + host);
        try {
            this.client = bimServerClientFactory.create(new UsernamePasswordAuthenticationInfo(user, password));
            logger.debug("Successfully Created BIM client for the host: " + host);
            return this.client;
        } catch (ServiceException e) {
            throw new RuntimeException("BIMServer: Service Exception Occurred", e);
        } catch (ChannelConnectionException e) {
            throw new RuntimeException("BIMServer: Channel Connection Exception Occurred", e);
        }
    }

    public ServiceInterface getServiceInterface() {
        return this.getClient().getServiceInterface();
    }

    public PluginInterface getPluginInterface() {
        return this.getClient().getPluginInterface();
    }

    public NotificationRegistryInterface getRegistry() {
        return this.getClient().getRegistry();
    }

    public long checkinAsync(long projectId, String revisionDescription, long oid, boolean b, long size, String name, InputStream inputStream, long topicId) throws ServerException, UserException {
        return this.getClient().checkinAsync(projectId, revisionDescription, oid, b, size, name, inputStream, topicId);
    }
}
