package ci.gouv.dgbf.system.collectif.server.impl;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.api.service.FileDto;
import org.cyk.system.file.server.api.service.FileService;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.protocol.http.HttpHelper;
import org.cyk.utility.business.Result;
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
		assertThat(business.countInDirectories(List.of("src/test/resources/files/d1"), null, null, null).getValue()).isEqualTo(1l);
	}
	
	@Test @Order(20)
	public void business_countInDirectories_all() {
		assertThat(business.countInDirectories(null, null, null, null).getValue()).isEqualTo(7l);
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
		assertThat(persistence.count(new QueryExecutorArguments().addFilterFieldsValues(Parameters.DUPLICATED,Boolean.TRUE))).isEqualTo(0l);
	}
	
	@SuppressWarnings("resource")
	@Test @Order(31)
	public void business_computeSha1() {
		new MockServerClient("localhost", WEB_SERVER_PORT)
	    .when(HttpRequest.request().withMethod("GET").withPath("/f01.txt"))
	    .respond(org.mockserver.model.HttpResponse.response().withStatusCode(200).withBody("Good job"));
		
		assertor.assertSha1ByUniformResourceLocator("http://localhost:10000/f02.txt", null);
		assertor.assertSha1ByUniformResourceLocator("http://localhost:10000/f01.txt", null);
		assertor.assertSha1ByUniformResourceLocator("http://localhost:10000/f03.txt", null);
		business.computeSha1("christian");
		assertor.assertSha1ByUniformResourceLocator("http://localhost:10000/f02.txt", null);
		assertor.assertSha1ByUniformResourceLocator("http://localhost:10000/f01.txt", "e86b3d9ac0c078a920c6ef791ee224c144ddfbbd");
		assertor.assertSha1ByUniformResourceLocator("http://localhost:10000/f03.txt", null);
	}
	
	@Test @Order(32)
	public void business_countDuplicatesAgain() {
		assertThat(persistence.count(new QueryExecutorArguments().addFilterFieldsValues(Parameters.DUPLICATED,Boolean.TRUE))).isEqualTo(0l);
	}
	
	@Test @Order(33)
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
		assertThat(persistence.count(new QueryExecutorArguments().addFilterFieldsValues(Parameters.DUPLICATED,Boolean.TRUE))).isEqualTo(0l);
		assertThat(persistence.count()).isEqualTo(count-1);
	}
}