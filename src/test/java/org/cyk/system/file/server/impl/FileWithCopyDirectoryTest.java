package org.cyk.system.file.server.impl;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.utility.persistence.EntityManagerGetter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(Profile.FileWithCopyDirectory.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class FileWithCopyDirectoryTest extends AbstractTest {

	@Inject FileBusiness business;
	
	/* Import */
	
	@Test @Order(1)
	public void business_import_specific() {
		business.import_(List.of("src/test/resources/files/d1"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"));
	}
	
	@Test @Order(2)
	public void business_import_all() {
		business.import_(null, null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"));
	}
	
	@Test @Order(3)
	public void business_import_all_again() {
		business.import_(null, null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"));
	}
	
	@Test @Order(4)
	public void business_import_specific_outside() {
		business.import_(List.of("src/test/resources/files_outside/d1"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"));
	}
	
	@Test @Order(5)
	public void business_import_all_outside() {
		business.import_(List.of("src/test/resources/files_outside"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"),buildUrl("f1_outside.txt"));
	}
	
	@Test @Order(6)
	public void business_import_all_again_outside() {
		business.import_(List.of("src/test/resources/files_outside"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"),buildUrl("f1_outside.txt"));
	}
	
	@Test @Order(7)
	public void business_import_all_service() {
		business.import_(List.of("src/test/resources/files_service"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d1_f1_3.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"),buildUrl("f1_3.txt"),buildUrl("f1_outside.txt"));
	}
	
	@Test @Order(8)
	public void business_import_all_various_mime_type() {
		business.import_(List.of("src/test/resources/various_name"), null, null, null, null, "meliane");
		assertor.assertUniformResourceLocators(buildUrl("Priereuniverselle.txt"),buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d1_f1_3.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"),buildUrl("f1_3.txt"),buildUrl("f1_outside.txt"));
	}
	
	@Test @Order(9)
	public void business_delete_all() {
		EntityManager entityManager = EntityManagerGetter.getInstance().get();
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM FileImpl t").executeUpdate();
		entityManager.getTransaction().commit();
		assertor.assertUniformResourceLocators();
	}
	
	@Test @Order(10)
	public void business_after_delete_all_import_all() {
		business.import_(null, null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("d1_f1.txt"),buildUrl("d1_f1_2.txt"),buildUrl("d2_f1.txt"),buildUrl("d3_f1.txt"),buildUrl("d4_f1.txt"),buildUrl("f1.txt")
				,buildUrl("f1_2.txt"));
	}
}