package org.cyk.system.file.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cyk.system.file.server.impl.business.TikaClient;
import org.cyk.system.file.server.impl.business.TikaDto;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(Profile.Tika.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class TikaTest extends AbstractTest {

	@Inject @RestClient TikaClient service;
	@Inject Configuration configuration;
	
	boolean isTestRunnable() {
		if(!configuration.tika().server().tests().runnable()) {
			LogHelper.logWarning(StringUtils.repeat("#", 20)+" Tika server tests are not runnable "+StringUtils.repeat("#", 20), getClass());
			return false;
		}
		return true;
	}
	
	@Test
	public void ping() throws Exception {
		if(!isTestRunnable())
			return;
		assertThat(service.ping()).startsWith("This is Tika Server");
	}
	
	@Test
	public void getText() throws Exception {
		if(!isTestRunnable())
			return;
		byte[] bytes = IOUtils.toByteArray(new FileInputStream(new java.io.File(System.getProperty("user.dir"),"src/test/resources/various_mime_type/Esprit Saint Tu es le don de Dieu-PU.pdf")));
		TikaDto dto = service.getText("ocr_only","true",bytes);
		assertThat(dto).isNotNull();
		assertThat(dto.getContent()).contains("ESPRIT SAINT, TU ES LE DON DE DIEU");
	}
}