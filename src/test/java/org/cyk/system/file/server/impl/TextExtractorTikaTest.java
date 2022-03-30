package org.cyk.system.file.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.cyk.system.file.server.api.business.TextExtractor;
import org.cyk.system.file.server.impl.business.TextExtractorTikaImpl;
import org.cyk.utility.__kernel__.file.FileHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(Profile.TextExtractorTika.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class TextExtractorTikaTest extends AbstractTikaTest {
	
	@Inject @Tika TextExtractor textExtractor;
	
	@Test
	public void fetchers() throws Exception {
		if(!isTestRunnable())
			return;
		String[] array = ((TextExtractorTikaImpl)textExtractor).computeFetchDetails(buildUrl("various_mime_type/Esprit Saint Tu es le don de Dieu-PU.pdf"));
		assertThat(array).hasSize(2);
		assertThat(array[0]).isEqualTo("file_management_system_integration_test");
		assertThat(array[1]).isEqualTo("Esprit%20Saint%20Tu%20es%20le%20don%20de%20Dieu-PU.pdf");
	}
	
	@Test
	public void extractFromSearchablePdf() throws Exception {
		if(!isTestRunnable())
			return;
		byte[] bytes = FileHelper.getBytesByUniformResourceIdentifier(buildUrl("various_mime_type/Esprit Saint Tu es le don de Dieu-PU.pdf"));
		assertThat(textExtractor.extract(bytes, null, null,null)).contains("Harmonisation:  Joseph Franck Mpola");
	}
	
	@Test
	public void extractFromScannedPdf() throws Exception {
		if(!isTestRunnable())
			return;
		byte[] bytes = FileHelper.getBytesByUniformResourceIdentifier(buildUrl("various_mime_type/pdf_as_image.pdf"));
		assertThat(textExtractor.extract(bytes, null, null,null)).contains("Permit me to introduce you to the facility of facsimile");
	}
	
	@Test
	public void extractFromFileSystem() {
		if(!isTestRunnable())
			return;
		assertThat(textExtractor.extract(null, buildUrl("various_mime_type/word.docx"), null,null)).contains("This is my sheet with extension");
	}
	
	@Test
	public void extractFromFileSystem_accent() {
		if(!isTestRunnable())
			return;
		assertThat(textExtractor.extract(null, buildUrl("various_mime_type/6 - Prière universelle.pdf"), null,null)).contains("Dieu plus grand que notre coeur");
	}
}