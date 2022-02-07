package org.cyk.quarkus.extension.test;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractClientTest extends AbstractTest {

	//@io.quarkus.test.common.http.TestHTTPResource java.net.URL url;
	
	protected void __listenBeforeEach__() {
		System.setProperty(getServerClientRestUriIdentifier(), getServerClientRestUri()+"api");
	}
	
	protected String getServerClientRestUriIdentifier() {
		return String.format(SERVER_CLIENT_REST_URI_IDENTIFIER_FORMAT, getServerIdentifier());
	}
	
	protected String getServerIdentifier() {
		return StringUtils.substringBetween(getClass().getPackageName(), ".system.", ".server.impl");
	}
	
	protected abstract String getServerClientRestUri();
	
	private static final String SERVER_CLIENT_REST_URI_IDENTIFIER_FORMAT = "%s.server.client.rest.uri";
}