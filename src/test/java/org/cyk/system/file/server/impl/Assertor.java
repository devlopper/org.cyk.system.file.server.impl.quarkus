package org.cyk.system.file.server.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.persistence.query.QueryExecutorArguments;

@ApplicationScoped
public class Assertor {

	@Inject FilePersistence filePersistence;
	
	public void assertFilterAsString(String search,String...expectedUniformResourceLocators) {
		if(search == null)
			return;
		Collection<FileImpl> files = CollectionHelper.cast(FileImpl.class, filePersistence.readMany(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR)
				.addFilterFieldsValues(filePersistence.getParameterNameFilterAsString(),search).setNumberOfTuples(100)));
		if(CollectionHelper.isEmpty(files))
			assertThat(expectedUniformResourceLocators).isEmpty();
		else
			assertThat(files.stream().map(file -> file.getUniformResourceLocator()).collect(Collectors.toList())).containsExactlyInAnyOrder(expectedUniformResourceLocators);
	}
	
	public void assertUniformResourceLocators(String...expectedUniformResourceLocators) {
		Collection<String> uniformResourceLocators = filePersistence.readUniformResourceLocators();
		if(CollectionHelper.isEmpty(uniformResourceLocators))
			assertThat(ArrayHelper.isEmpty(expectedUniformResourceLocators)).isTrue();
		else
			assertThat(uniformResourceLocators).containsExactly(expectedUniformResourceLocators);
	}
	
	public void assertSha1ByUniformResourceLocator(String uniformResourceLocator,String expectedSha1) {
		FileImpl file = (FileImpl) filePersistence.readOne(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_SHA1)
				.addFilterFieldsValues(Parameters.UNIFORM_RESOURCE_LOCATOR,uniformResourceLocator));
		assertThat(file).as("File at <<"+uniformResourceLocator+">> found").isNotNull();
		assertThat(file.getSha1()).as("Sha1 of file at <<"+uniformResourceLocator+">>").isEqualTo(expectedSha1);
	}
	
	public void assertBytesByUniformResourceLocator(String uniformResourceLocator,byte[] expectedBytes) {
		FileImpl file = (FileImpl) filePersistence.readOne(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_BYTES)
				.addFilterFieldsValues(Parameters.UNIFORM_RESOURCE_LOCATOR,uniformResourceLocator));
		assertThat(file).as("File at <<"+uniformResourceLocator+">> found").isNotNull();
		assertThat(file.getBytes()).as("bytes of file at <<"+uniformResourceLocator+">>").isEqualTo(expectedBytes);
	}
	
	public void assertTextByUniformResourceLocator(String uniformResourceLocator,String expectedText) {
		FileImpl file = (FileImpl) filePersistence.readOne(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_TEXT)
				.addFilterFieldsValues(Parameters.UNIFORM_RESOURCE_LOCATOR,uniformResourceLocator));
		assertThat(file).as("File at <<"+uniformResourceLocator+">> found").isNotNull();
		assertThat(file.getText()).as("text of file at <<"+uniformResourceLocator+">>").isEqualTo(expectedText);
	}
	
	public void assertTextContainsByUniformResourceLocator(String uniformResourceLocator,String expectedText) {
		FileImpl file = (FileImpl) filePersistence.readOne(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_TEXT)
				.addFilterFieldsValues(Parameters.UNIFORM_RESOURCE_LOCATOR,uniformResourceLocator));
		assertThat(file).as("File at <<"+uniformResourceLocator+">> found").isNotNull();
		assertThat(file.getText()).as("text of file at <<"+uniformResourceLocator+">>").contains(expectedText);
	}
	
	public void assertTextContainsIgnoreCaseByUniformResourceLocator(String uniformResourceLocator,String expectedText) {
		FileImpl file = (FileImpl) filePersistence.readOne(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_TEXT)
				.addFilterFieldsValues(Parameters.UNIFORM_RESOURCE_LOCATOR,uniformResourceLocator));
		assertThat(file).as("File at <<"+uniformResourceLocator+">> found").isNotNull();
		assertThat(file.getText()).as("text of file at <<"+uniformResourceLocator+">>").containsIgnoringCase(expectedText);
	}
}