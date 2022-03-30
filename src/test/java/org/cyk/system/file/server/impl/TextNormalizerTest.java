package org.cyk.system.file.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.cyk.system.file.server.api.business.TextExtractor;
import org.cyk.system.file.server.api.business.TextNormalizer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(Profile.TextNormalizer.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class TextNormalizerTest extends AbstractTest {

	@Inject TextNormalizer normalizer;
	@Inject @Tika TextExtractor extractor;
	
	@Test
	public void normalize() {
		assertThat(normalizer.normalize("Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ\r\nHello")).isEqualTo("This is a funky String\r\nHello");
	}
	
	@Test
	public void normalize_double_space() {
		assertThat(normalizer.normalize("aa  aab")).isEqualTo("aa aab");
	}
	
	@Test
	public void normalize_triple_space() {
		assertThat(normalizer.normalize("aaa   ab")).isEqualTo("aaa ab");
	}
	
	@Test
	public void normalize_double_endline() {
		assertThat(normalizer.normalize("abc\r\n\r\n")).isEqualTo("abc");
	}
	
	@Test
	public void normalize_line_having_only_words_of_one_character() {
		assertThat(normalizer.normalize("a b c d")).isNull();
	}
	
	@Test
	public void normalize_trim() {
		assertThat(normalizer.normalize(" abc ")).isEqualTo("abc");
		assertThat(normalizer.normalize(" Alto")).isEqualTo("Alto");
	}
	
	@Test
	public void normalize_line_minimal_lenght() {
		assertThat(normalizer.normalize("abc\r\n12\r\n789\r\n          a  ")).isEqualTo("abc\r\n789");
	}
	
	@Test
	public void normalize_special_character() {
		assertThat(normalizer.normalize("---")).isNull();
	}
	
	@Test
	public void normalize_file_01() throws Exception {
		String text = extractor.extract(null, buildUrl("various_mime_type/to_be_normilized_01.txt"), null,null);	
		text = normalizer.normalize(text);
		assertThat(text).contains("Chantons");
	}
	
	@Test
	public void normalize_file_02() throws Exception {
		String text = extractor.extract(null, buildUrl("various_mime_type/to_be_normilized_02.txt"), null,null);	
		text = normalizer.normalize(text);
		assertThat(text).contains("Chantons");
	}
}