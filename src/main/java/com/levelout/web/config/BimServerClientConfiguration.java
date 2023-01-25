package com.levelout.web.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bimserver.client.BimServerClient;
import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.shared.ChannelConnectionException;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.bimserver.shared.exceptions.BimServerClientException;
import org.bimserver.shared.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BimServerClientConfiguration {

	final static Log logger = LogFactory.getLog(BimServerClientConfiguration.class);
	@Value("${bimserver.host}")
	String host;

	@Value("${bimserver.user}")
	String user;

	@Value("${bimserver.password}")
	String password;

	@Value("${bimserver.certificate:#{null}}")
	String certificate;

	@Bean
	public JsonBimServerClientFactory bimServerClientFactory()
			throws MalformedURLException, BimServerClientException, ServiceException, ChannelConnectionException {
		logger.debug("Creating instance of BimServerClientFactory for host: " + host);
		JsonBimServerClientFactory factory;
		if (certificate == null || certificate.isEmpty()) {
			factory = new JsonBimServerClientFactory(host);
		} else {
			File trustedCert = new File(certificate);
			URL trustedCertificate = trustedCert.exists() ? trustedCert.toURI().toURL()
					: getClass().getClassLoader().getResource(certificate);
			factory = new JsonBimServerClientFactory(host, trustedCertificate);
		}
		logger.debug("Created instance of BimServerClientFactory for host: " + host);
		return factory;
	}

	@Bean
	public BimServerClient bimServerClient(
			@Qualifier("bimServerClientFactory") JsonBimServerClientFactory bimServerClientFactory)
			throws MalformedURLException, BimServerClientException, ServiceException, ChannelConnectionException {
		logger.debug("Creating BIM client for host: " + host);
		BimServerClient client = bimServerClientFactory.create(new UsernamePasswordAuthenticationInfo(user, password));
		logger.debug("Successfully Created BIM client for the host: " + host);
		return client;
	}
}
