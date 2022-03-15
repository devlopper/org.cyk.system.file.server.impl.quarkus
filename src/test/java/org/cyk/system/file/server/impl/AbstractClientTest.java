package org.cyk.system.file.server.impl;

import java.net.URL;

import javax.inject.Inject;

import io.quarkus.test.common.http.TestHTTPResource;

public abstract class AbstractClientTest extends org.cyk.quarkus.extension.test.AbstractClientTest {

	@Inject protected Assertor assertor;
	@TestHTTPResource URL url;
	
	protected void __listenBeforeEach__() {
		System.setProperty("collectif.server.client.rest.uri", url.toString()+"api");
	}
	
	@Override
	protected String getServerClientRestUri() {
		return url.toString();
	}
}