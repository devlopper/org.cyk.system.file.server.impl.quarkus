package ci.gouv.dgbf.system.collectif.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;

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
	
}