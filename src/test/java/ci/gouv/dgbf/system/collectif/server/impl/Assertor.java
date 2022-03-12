package ci.gouv.dgbf.system.collectif.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.persistence.query.QueryExecutorArguments;

@ApplicationScoped
public class Assertor {

	@Inject FilePersistence filePersistence;
	
	public void assertUniformResourceLocators(String...expectedUniformResourceLocators) {
		Collection<String> uniformResourceLocators = filePersistence.readUniformResourceLocators();
		if(CollectionHelper.isEmpty(uniformResourceLocators))
			assertThat(expectedUniformResourceLocators).isNull();
		else
			assertThat(uniformResourceLocators).containsExactly(expectedUniformResourceLocators);
	}
	
	public void assertSha1ByUniformResourceLocator(String uniformResourceLocator,String expectedSha1) {
		FileImpl file = (FileImpl) filePersistence.readOne(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_SHA1)
				.addFilterFieldsValues(Parameters.UNIFORM_RESOURCE_LOCATOR,uniformResourceLocator));
		assertThat(file).as("File at <<"+uniformResourceLocator+">> found").isNotNull();
		assertThat(file.getSha1()).as("Sha1 of file at <<"+uniformResourceLocator+">>").isEqualTo(expectedSha1);
	}
}