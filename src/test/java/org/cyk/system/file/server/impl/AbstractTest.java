package org.cyk.system.file.server.impl;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.cyk.system.file.server.impl.configuration.Configuration;

public abstract class AbstractTest extends org.cyk.quarkus.extension.test.AbstractTest {

	@Inject Configuration configuration;
	@Inject protected Assertor assertor;

	String buildUrl(String relativePath) {
		try {
			if(configuration.file().copyToDirectory())
				return Paths.get(ApplicationLifeCycleListener.FILE_DIRECTORY.getAbsolutePath(),relativePath).toUri().toURL().toString();
			return Paths.get(System.getProperty("user.dir")+"/src/test/resources/"+relativePath).toUri().toURL().toString();
		} catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}
}