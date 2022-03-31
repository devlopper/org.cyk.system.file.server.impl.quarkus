package org.cyk.system.file.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.cyk.system.file.server.api.business.NameNormalizer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(Profile.NameNormalizer.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class NameNormalizerTest extends AbstractTest {

	@Inject NameNormalizer normalizer;
	
	@Test
	public void normalize_accent() {
		assertThat(normalizer.normalize("6 - Prière universelle.pdf")).isEqualTo("6 - Priere universelle.pdf");
	}
	
}