package org.cyk.system.file.server.impl;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.persistence.FileBytesPersistence;
import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.FileTextPersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.api.service.FileDto;
import org.cyk.system.file.server.api.service.FileService;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.system.file.server.impl.persistence.FileQueryStringBuilder;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.protocol.http.HttpHelper;
import org.cyk.utility.business.Result;
import org.cyk.utility.persistence.query.Field;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockserver.client.MockServerClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(Profile.File.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class FileTest extends AbstractTest {

	static ClientAndServer SERVER;
	static Integer WEB_SERVER_PORT = 10000;
	
	@Inject EntityManager entityManager;
	@Inject FilePersistence persistence;
	@Inject FileBytesPersistence fileBytesPersistence;
	@Inject FileTextPersistence fileTextPersistence;
	@Inject FileBusiness business;
	@Inject FileService service;
	
	@BeforeAll
	public static void listenBeforeAll() {
		@SuppressWarnings("resource")
		MockServerClient mockServerClient = new MockServerClient("localhost", WEB_SERVER_PORT);
		if(!mockServerClient.hasStarted())
			try {
				SERVER =  ClientAndServer.startClientAndServer(WEB_SERVER_PORT);
			} catch (Exception exception) {}
		ConfigurationProperties.logLevel("WARN");
	}
	
	@Override
	protected void __listenAfterEach__() {
		super.__listenAfterEach__();
		HttpHelper.clear();
		if(SERVER != null)
			SERVER.reset();
	}
	
	@AfterAll
	protected static void listenAfterAll() {
		if(SERVER != null)
			SERVER.stop();
	}
	
	//@Test @Order(10)
	public void persistence_queryStringBuilder_predicate_nameIsNormalized() {
		assertThat(FileQueryStringBuilder.Predicate.nameIsNormalized()).isEqualTo("NOT (name LIKE %é% OR name LIKE %è%)");
	}
	
	@Test @Order(10)
	public void dto_to_string() {
		assertThat(JsonbBuilder.create().toJson(new Filter.Dto().addField(FileDto.JSON_IS_A_DUPLICATE, Boolean.TRUE))).isEqualTo("{\"fields\":[{\"name\":\"is_a_duplicate\",\"value\":{\"container\":\"NONE\",\"value\":\"true\"}}]}");
	}
	
	@Test @Order(10)
	public void string_to_dto() {
		Filter.Dto dto = JsonbBuilder.create().fromJson("{\"fields\":[{\"name\":\"is_a_duplicate\",\"value\":{\"value\":\"true\"}}]}", Filter.Dto.class);
		assertThat(dto.getFields()).isNotEmpty();
		Field.Dto field = dto.getFields().iterator().next();
		assertThat(field.getName()).isEqualTo(FileDto.JSON_IS_A_DUPLICATE);
		assertThat(field.getValue().getValue()).isEqualTo("true");
	}
	
	@Test @Order(10)
	public void persistence_readUniformResourceLocators() {
		assertThat(persistence.readUniformResourceLocators()).containsExactly("http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(10)
	public void service_get_2() {
		io.restassured.response.Response response = given().when()/*.log().all()*/.get("/api/files/2");
		response.then()
		//.log().all()
        	.statusCode(Response.Status.OK.getStatusCode())
        	.body(FileDto.JSON_IDENTIFIER, equalTo("2"))
        	.body(FileDto.JSON_DOWNLOAD_LINK, equalTo("/api/files/2/download"))
        	;
	}
	
	/* Download */
	
	@Test @Order(10)
	public void business_download_existing_bytes() {
		Result result = business.download("2");
		FileImpl file = (FileImpl) result.getValue();
		assertThat(file).isNotNull();
		assertThat(file.getIdentifier()).isEqualTo("2");
		assertThat(file.getNameAndExtension()).isEqualTo("name03.ext03");
		assertThat(file.getMimeType()).isEqualTo("text/plain");
		assertThat(file.getSize()).isEqualTo(300);
		assertThat(new String(file.getBytes())).isEqualTo("hello world!");
	}
	
	@Test @Order(10)
	public void service_download_existing_bytes() {
		io.restassured.response.Response response = given().when()/*.log().all()*/.get("/api/files/2/download");
		response.then()
		//.log().all()
        	.statusCode(Response.Status.OK.getStatusCode())
        	;
		assertThat(response.getBody().asString()).isEqualTo("hello world!");
		assertThat(response.header(HttpHeaders.CONTENT_TYPE)).startsWith("text/plain");
		assertThat(response.header(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo("attachment; filename=name03.ext03");
		assertThat(response.header(HttpHeaders.CONTENT_LENGTH)).isEqualTo("12");
	}
	
	@Test @Order(10)
	public void business_download_not_existing() {
		Result result = business.download("NOT_EXISTING");
		FileImpl file = result == null ? null : (FileImpl) result.getValue();
		assertThat(file).isNull();
	}
	
	@Test @Order(10)
	public void service_download_not_existing() {
		io.restassured.response.Response response = given().when()/*.log().all()*/.get("/api/files/NOT_EXISTING/download");
		response.then()
		//.log().all()
        	.statusCode(Response.Status.NOT_FOUND.getStatusCode())
        	;
	}
	
	@SuppressWarnings("resource")
	@Test @Order(10)
	public void business_download_existing_url_http() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f01.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Good job"));
		
		String identifier = "1";
		Result result = business.download(identifier);
		FileImpl file = (FileImpl) result.getValue();
		assertThat(file).isNotNull();
		assertThat(file.getIdentifier()).isEqualTo(identifier);
		assertThat(file.getNameAndExtension()).isEqualTo("name02.ext02");
		assertThat(file.getMimeType()).isEqualTo("text/html");
		assertThat(file.getSize()).isEqualTo(200);
		assertThat(new String(file.getBytes())).contains("Good job");
	}
	
	@SuppressWarnings("resource")
	@Test @Order(10)
	public void service_download_existing_url_http() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f01.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Good job"));
		
		io.restassured.response.Response response = given().when()/*.log().all()*/.get("/api/files/1/download");
		response.then()
		//.log().all()
        	.statusCode(Response.Status.OK.getStatusCode())
        	;
		assertThat(response.getBody().asString()).isEqualTo("Good job");
		assertThat(response.header(HttpHeaders.CONTENT_TYPE)).startsWith("text/html");
		assertThat(response.header(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo("attachment; filename=name02.ext02");
		assertThat(response.header(HttpHeaders.CONTENT_LENGTH)).isEqualTo("8");
	}
	
	/**/
	
	/* Count in directories*/
	
	@Test @Order(20)
	public void business_countInDirectories_specific() {
		assertThat(business.countInDirectories(List.of("src/test/resources/files/d1"), null, null, null).getValue()).isEqualTo(5l);
	}
	
	@Test @Order(20)
	public void business_countInDirectories_all() {
		assertThat(business.countInDirectories(null, null, null, null).getValue()).isEqualTo(11l);
	}
	
	/* Import */
	
	@Test @Order(20)
	public void business_import_all_null_exception() {
		Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
			business.import_(null, null, null, null,null,null);
	    });
		assertThat(exception.getMessage()).isEqualTo("Le nom d'utilisateur est requis");
	}
	
	@Test @Order(20)
	public void business_import_specific() {
		business.import_(List.of("src/test/resources/files/d1"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(21)
	public void business_import_all() {
		business.import_(null, null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(22)
	public void business_import_all_again() {
		business.import_(null, null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(23)
	public void business_import_specific_outside() {
		business.import_(List.of("src/test/resources/files_outside/d1"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(24)
	public void business_import_all_outside() {
		business.import_(List.of("src/test/resources/files_outside"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/f1_outside.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(25)
	public void business_import_all_again_outside() {
		business.import_(List.of("src/test/resources/files_outside"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/f1_outside.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(26)
	public void business_download_existing_url_file() {
		String identifier = entityManager.createQuery("SELECT t.identifier FROM FileImpl t WHERE t.uniformResourceLocator = :uniformResourceLocator", String.class).setParameter("uniformResourceLocator", buildUrl("files/f1.txt")).getSingleResult();
		Result result = business.download(identifier);
		FileImpl file = (FileImpl) result.getValue();
		assertThat(file).isNotNull();
		assertThat(file.getIdentifier()).isEqualTo(identifier);
		assertThat(file.getNameAndExtension()).isEqualTo("f1.txt");
		assertThat(file.getMimeType()).isEqualTo("text/plain");
		assertThat(file.getSize()).isEqualTo(23);
		assertThat(new String(file.getBytes())).isEqualTo("files - This is a test!");
	}
	
	@Test @Order(27)
	public void service_import_specific() {
		service.import_(List.of("src/test/resources/files_service/d1"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/f1_outside.txt"),buildUrl("files_service/d1/d1_f1.txt")
				,"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(28)
	public void service_import_all() {
		service.import_(List.of("src/test/resources/files_service"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/f1_outside.txt"),buildUrl("files_service/d1/d1_f1.txt")
				,buildUrl("files_service/f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(29)
	public void service_import_all_again() {
		service.import_(List.of("src/test/resources/files_service"), null, null, null,null,"christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/f1_outside.txt"),buildUrl("files_service/d1/d1_f1.txt")
				,buildUrl("files_service/f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	/* Compute Sha1 */
	
	@Test @Order(30)
	public void business_countDuplicates() {
		assertThat(persistence.count(new QueryExecutorArguments().addFilterFieldsValues(Parameters.IS_A_DUPLICATE,Boolean.TRUE))).isEqualTo(0l);
	}
	
	@Test @Order(31)
	public void business_deleteDuplicates() {
		business.import_(List.of("src/test/resources/files_outside"), null, null, null, Boolean.TRUE, "christian");
		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/d1/duplicate_of_d1_f1_in_files_d1.txt"),buildUrl("files_outside/f1_outside.txt"),buildUrl("files_service/d1/d1_f1.txt")
				,buildUrl("files_service/f1.txt"),"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
		
		Long count = persistence.count();
		Result result = business.deleteDuplicates("meliane");
		assertThat(result.getCountsMap()).as("count map").isNotNull();
		assertThat(result.getCountsMap().get(FileImpl.class)).as("file's count map").isNotNull();
		assertThat(result.getCountsMap().get(FileImpl.class).get(Action.DELETE)).isEqualTo(1);
		assertThat(persistence.count(new QueryExecutorArguments().addFilterFieldsValues(Parameters.IS_A_DUPLICATE,Boolean.TRUE))).isEqualTo(0l);
		assertThat(persistence.count()).isEqualTo(count-1);

		assertor.assertUniformResourceLocators(buildUrl("files/d1/d1_f1.txt"),buildUrl("files/d2/d2_f1.txt"),buildUrl("files/d3/d3_f1.txt"),buildUrl("files/d4/d4_f1.txt"),buildUrl("files/f1.txt")
				,buildUrl("files02/d1/d1_f1.txt"),buildUrl("files02/f1.txt"),buildUrl("files_outside/f1_outside.txt"),buildUrl("files_service/d1/d1_f1.txt"),buildUrl("files_service/f1.txt")
				,"http://localhost:10000/f01.txt","http://localhost:10000/f02.txt","http://localhost:10000/f03.txt");
	}
	
	/* Search */
	
	@Test @Order(32)
	public void persistence_filterAsString_noResult() {
		assertor.assertFilterAsString("XXX_UNKNOWN_XXX");
	}
	
	@Test @Order(32)
	public void persistence_filterAsString_outside() {
		assertor.assertFilterAsString("outside",buildUrl("files_outside/f1_outside.txt"));
	}
	
	@Test @Order(32)
	public void persistence_filterAsString_name() {
		assertor.assertFilterAsString("name","http://localhost:10000/f02.txt","http://localhost:10000/f01.txt","http://localhost:10000/f03.txt");
	}
	
	@Test @Order(32)
	public void persistence_filterAsString_name02() {
		assertor.assertFilterAsString("name02","http://localhost:10000/f01.txt");
	}
	
	@Test @Order(32)
	public void persistence_filterAsString_d1_f1() {
		assertor.assertFilterAsString("d1_f1",buildUrl("files_service/d1/d1_f1.txt"),buildUrl("files/d1/d1_f1.txt"),buildUrl("files02/d1/d1_f1.txt"));
	}
	
	@Test @Order(32)
	public void persistence_filterAsString_you_are_lord() {
		assertor.assertFilterAsString("you","http://localhost:10000/f03.txt");
	}
	
	/* Export bytes */
	
	@SuppressWarnings("resource")
	@Test @Order(33)
	public void business_extract_bytes_1() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f01.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Good job"));
		
		assertor.assertBytesByUniformResourceLocator("http://localhost:10000/f01.txt",null);
		assertThat(fileBytesPersistence.count()).isEqualTo(1l);
		business.extractBytes("meliane", "1");
		assertor.assertBytesByUniformResourceLocator("http://localhost:10000/f01.txt","Good job".getBytes());
		assertThat(fileBytesPersistence.count()).isEqualTo(2l);
	}
	
	@SuppressWarnings("resource")
	@Test @Order(34)
	public void business_extract_bytes_2() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f03.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("hello world!"));
		
		assertor.assertBytesByUniformResourceLocator("http://localhost:10000/f03.txt","hello world!".getBytes());
		assertThat(fileBytesPersistence.count()).isEqualTo(2l);
		business.extractBytes("meliane", "2");
		assertor.assertBytesByUniformResourceLocator("http://localhost:10000/f03.txt","hello world!".getBytes());
		assertThat(fileBytesPersistence.count()).isEqualTo(2l);
	}
	
	@SuppressWarnings("resource")
	@Test @Order(34)
	public void business_extract_text_3() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f02.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Jesus"));
		
		assertor.assertTextByUniformResourceLocator("http://localhost:10000/f02.txt",null);
		assertThat(fileTextPersistence.count()).isEqualTo(1l);
		business.extractText("meliane", "3");
		assertThat(fileTextPersistence.count()).isEqualTo(2l);
		assertor.assertTextByUniformResourceLocator("http://localhost:10000/f02.txt","Jesus");
	}
	
	@SuppressWarnings("resource")
	@Test @Order(35)
	public void business_extract_text_2() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f03.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("You are lord"));
		
		assertor.assertTextByUniformResourceLocator("http://localhost:10000/f03.txt","You are lord");
		assertThat(fileTextPersistence.count()).isEqualTo(2l);
		business.extractText("meliane", "2");
		assertThat(fileTextPersistence.count()).isEqualTo(2l);
		assertor.assertTextByUniformResourceLocator("http://localhost:10000/f03.txt","You are lord");
	}
	
	@Test @Order(36)
	public void business_extract_bytes_all() {
		assertThat(fileBytesPersistence.count()).isEqualTo(2l);
		business.extractBytesOfAll("meliane");
		assertThat(fileBytesPersistence.count()).isEqualTo(12l);
	}
	
	@Test @Order(37)
	public void business_extract_bytes_all_again() {
		assertThat(fileBytesPersistence.count()).isEqualTo(12l);
		business.extractBytesOfAll("meliane");
		assertThat(fileBytesPersistence.count()).isEqualTo(12l);
	}
	
	@Test @Order(38)
	public void business_extract_text_all() {
		assertThat(fileTextPersistence.count()).isEqualTo(2l);
		business.extractTextOfAll("meliane");
		assertThat(fileTextPersistence.count()).isEqualTo(13l);
	}
	
	@Test @Order(39)
	public void business_extract_text_all_again() {
		assertThat(fileTextPersistence.count()).isEqualTo(13l);
		business.extractTextOfAll("meliane");
		assertThat(fileTextPersistence.count()).isEqualTo(13l);
	}
	
	@Test @Order(40)
	public void business_extract_text_pdf() {
		business.import_(List.of("src/test/resources/various_mime_type"), null, null, null, null, "meliane");
		/*Result result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/aube_nouvelle.pdf")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION);
		
		/*result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/bientot_le_jour_se_levera.pdf")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION);
		
		/*result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/fiche_activite.pdf")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_TEXT);
		
		/*result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/pdf_as_image.pdf")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION);
		
		/*result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/word.docx")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_OTHERS);
		
		/*result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/worddocx")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_OTHERS);
		
		/*result = */business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/rtf.rtf")));
		//assertThat(result.getMapValueByKey(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR)).isEqualTo(FileBusinessImpl.ResultKey.TEXT_EXTRACTOR_OTHERS);
		
		business.extractText("a", persistence.readIdentifierByUniformResourceLocator(buildUrl("various_mime_type/6 - Prière universelle.pdf")));
		
		assertThat(fileTextPersistence.count()).isEqualTo(21l);
		
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/aube_nouvelle.pdf"),"Pour sauver son peuple");
		assertor.assertTextContainsIgnoreCaseByUniformResourceLocator(buildUrl("various_mime_type/bientot_le_jour_se_levera.pdf"),"le jour");
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/fiche_activite.pdf"),"22086 Budget");
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/pdf_as_image.pdf"),"TELEPHONE");
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/word.docx"),"This is my sheet with extension");
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/worddocx"),"This is my sheet");
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/rtf.rtf"),"This is my sheet rtf too");
		assertor.assertTextContainsByUniformResourceLocator(buildUrl("various_mime_type/6 - Prière universelle.pdf"),"Nous nous tournons vers toi Seigneur");
	}
}