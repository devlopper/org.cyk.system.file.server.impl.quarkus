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
public class TikaTest extends AbstractTikaTest {

	@Inject @RestClient TikaClient client;

	@Test
	public void ping() throws Exception {
		if(!isTestRunnable())
			return;
		assertThat(client.ping()).startsWith("This is Tika Server");
	}
	
	@Test
	public void getTextFromPdfSearchable() throws Exception {
		if(!isTestRunnable())
			return;
		byte[] bytes = IOUtils.toByteArray(new FileInputStream(new java.io.File(System.getProperty("user.dir"),"src/test/resources/various_mime_type/Esprit Saint Tu es le don de Dieu-PU.pdf")));
		TikaDto dto = client.getTextByBytes(bytes);
		assertThat(dto).isNotNull();
		assertThat(dto.getContent()).contains("Harmonisation:  Joseph Franck Mpola");
		
		dto = client.getTextByBytes(bytes,null,null);
		assertThat(dto).isNotNull();
		assertThat(dto.getContent()).contains("Harmonisation:  Joseph Franck Mpola");
		
		dto = client.getTextByBytes(bytes,"no_ocr",null);
		assertThat(dto).isNotNull();
		assertThat(dto.getContent()).contains("Harmonisation:  Joseph Franck Mpola");
	}
	
	@Test
	public void getTextFromPdfScanned() throws Exception {
		if(!isTestRunnable())
			return;
		byte[] bytes = IOUtils.toByteArray(new FileInputStream(new java.io.File(System.getProperty("user.dir"),"src/test/resources/various_mime_type/pdf_as_image.pdf")));
		TikaDto dto = client.getTextByBytes(bytes,"ocr_only","true");
		assertThat(dto).isNotNull();
		assertThat(dto.getContent()).contains("Permit me to introduce you to the facility of facsimile");
	}
	
	@Test
	public void getTextByFetch() {
		if(!isTestRunnable())
			return;
		if(configuration.tika().server().tests().fetchs().isEmpty()) {
			String message = String.format("%1$s %2$s %1$s",  StringUtils.repeat("#", 20),"Get text by fetch has not been tested because no fetch has been provided. Please provide some.");
			LogHelper.logInfo(message, getClass());
			return;	
		}
		for(Configuration.Tika.Server.Tests.Fetch fetch : configuration.tika().server().tests().fetchs()) {
			LogHelper.logInfo(String.format("%1$s Fetch %2$s using %3$s %1$s",  StringUtils.repeat("#", 20),fetch.key(),fetch.fetcherName()), getClass());
			TikaDto dto = client.getTextByFetch(fetch.fetcherName(), fetch.key(),null,null);
			assertThat(dto).isNotNull();
			assertThat(dto.getContent()).contains(fetch.result().subString());
		}		
	}
}