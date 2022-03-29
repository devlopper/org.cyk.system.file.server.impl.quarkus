package org.cyk.system.file.server.impl;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.cyk.system.file.server.impl.business.TikaClient;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.eclipse.microprofile.rest.client.inject.RestClient;

public abstract class AbstractTikaTest extends AbstractTest {

	@Inject @RestClient TikaClient client;
	@Inject Configuration configuration;
	
	boolean isTestRunnable() {
		if(!configuration.tika().server().tests().runnable()) {
			LogHelper.logWarning(String.format("%1$s %2$s %1$s", StringUtils.repeat("#", 30),"Tika server tests are not runnable"), getClass());
			return false;
		}
		return true;
	}
}