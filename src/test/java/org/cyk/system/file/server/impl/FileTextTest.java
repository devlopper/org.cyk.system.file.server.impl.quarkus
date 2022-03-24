package org.cyk.system.file.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.business.FileTextBusiness;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@QuarkusTest
@TestProfile(Profile.FileText.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class FileTextTest extends AbstractTest {

	@Inject FileTextBusiness business;
	@Inject FileBusiness fileBusiness;
	
	@Inject
	Configuration configuration;
	
	@Test
	public void normalize() {
		assertThat(business.normalize("Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ\r\nHello")).isEqualTo("This is a funky String\r\nHello");
	}
	
	@Test
	public void normalize_double_space() {
		assertThat(business.normalize("aa  aab")).isEqualTo("aa aab");
	}
	
	@Test
	public void normalize_triple_space() {
		assertThat(business.normalize("aaa   ab")).isEqualTo("aaa ab");
	}
	
	@Test
	public void normalize_double_endline() {
		assertThat(business.normalize("abc\r\n\r\n")).isEqualTo("abc");
	}
	
	@Test
	public void normalize_line_having_only_words_of_one_character() {
		assertThat(business.normalize("a b c d")).isNull();
	}
	
	@Test
	public void normalize_trim() {
		assertThat(business.normalize(" abc ")).isEqualTo("abc");
		assertThat(business.normalize(" Alto")).isEqualTo("Alto");
	}
	
	@Test
	public void normalize_line_minimal_lenght() {
		assertThat(business.normalize("abc\r\n12\r\n789\r\n          a  ")).isEqualTo("abc\r\n789");
	}
	
	@Test
	public void normalize_special_character() {
		assertThat(business.normalize("---")).isNull();
	}
	
	@Test
	public void normalize_file_01() throws Exception {
		PDDocument document = PDDocument.load(IOUtils.toByteArray(new FileInputStream(new java.io.File(System.getProperty("user.dir"),"src/test/resources/various_mime_type/Cantique_De_Paques.pdf"))));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		ITesseract tesseract = new Tesseract();
		tesseract.setDatapath("tesseract/data");
		tesseract.setLanguage("fra");
		StringBuilder stringBuilder = new StringBuilder();
		for (int page = 0; page < document.getNumberOfPages(); page++) {
		    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
		    String string = tesseract.doOCR(bufferedImage);
		    if(StringHelper.isBlank(string))
		    	continue;
		    stringBuilder.append(string);
		}
		
		String text = StringUtils.stripToNull(stringBuilder.toString());	
		text = business.normalize(text);
		assertThat(text).contains("Chantons");
	}
	
	@Test
	public void normalize_file_02() throws Exception {
		PDDocument document = PDDocument.load(IOUtils.toByteArray(new FileInputStream(new java.io.File(System.getProperty("user.dir"),"src/test/resources/various_mime_type/Esprit Saint Tu es le don de Dieu-PU.pdf"))));
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		ITesseract tesseract = new Tesseract();
		tesseract.setDatapath("tesseract/data");
		tesseract.setLanguage("fra");
		StringBuilder stringBuilder = new StringBuilder();
		for (int page = 0; page < document.getNumberOfPages(); page++) {
		    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
		    String string = tesseract.doOCR(bufferedImage);
		    if(StringHelper.isBlank(string))
		    	continue;
		    stringBuilder.append(string);
		}
		
		String text = StringUtils.stripToNull(stringBuilder.toString());	
		text = business.normalize(text);
		assertThat(text).contains("ESPRIT SAINT TU ES LE DON DE DIEU");
	}
}