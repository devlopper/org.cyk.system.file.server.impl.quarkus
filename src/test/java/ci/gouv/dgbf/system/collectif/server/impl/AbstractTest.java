package ci.gouv.dgbf.system.collectif.server.impl;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import javax.inject.Inject;

public abstract class AbstractTest extends org.cyk.quarkus.extension.test.AbstractTest {

	@Inject protected Assertor assertor;

	String buildUrl(String relativePath) {
		try {
			return Paths.get(System.getProperty("user.dir")+"/src/test/resources/"+relativePath).toUri().toURL().toString();
		} catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}
}