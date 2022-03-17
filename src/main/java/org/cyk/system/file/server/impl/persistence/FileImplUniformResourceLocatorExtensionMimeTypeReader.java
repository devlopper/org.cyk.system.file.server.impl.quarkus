package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import org.cyk.utility.persistence.server.query.string.QueryStringBuilder;

public class FileImplUniformResourceLocatorExtensionMimeTypeReader extends AbstractFileImplReader implements Serializable {

	@Override
	protected QueryStringBuilder.Arguments instantiateQueryStringBuilderArguments() {
		QueryStringBuilder.Arguments arguments =  super.instantiateQueryStringBuilderArguments();
		arguments.getProjection(Boolean.TRUE).addFromTuple("t",FileImpl.FIELD_IDENTIFIER,FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR,FileImpl.FIELD_EXTENSION,FileImpl.FIELD_MIME_TYPE);
		return arguments;
	}
	
	@Override
	protected void __set__(FileImpl file, Object[] array) {
		Integer index = 0;
		file.setIdentifier(getAsString(array, index++));
		file.setUniformResourceLocator(getAsString(array, index++));
		file.setExtension(getAsString(array, index++));		
		file.setMimeType(getAsString(array, index++));
	}
	
}