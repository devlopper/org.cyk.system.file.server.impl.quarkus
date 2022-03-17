package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import org.cyk.utility.persistence.server.query.string.QueryStringBuilder;

public class FileImplBytesReader extends AbstractFileImplReader implements Serializable {

	@Override
	protected QueryStringBuilder.Arguments instantiateQueryStringBuilderArguments() {
		QueryStringBuilder.Arguments arguments =  super.instantiateQueryStringBuilderArguments();
		arguments.getTuple(Boolean.TRUE).addJoins(String.format("LEFT JOIN %1$s %2$s ON %2$s.%3$s = t.%3$s",FileBytesImpl.ENTITY_NAME,"fb",FileBytesImpl.FIELD_IDENTIFIER));
		arguments.getProjection(Boolean.TRUE).addFromTuple("t",FileImpl.FIELD_IDENTIFIER);
		arguments.getProjection(Boolean.TRUE).addFromTuple("fb",FileBytesImpl.FIELD_BYTES);
		return arguments;
	}
	
	@Override
	protected void __set__(FileImpl file, Object[] array) {
		Integer index = 0;
		file.setIdentifier(getAsString(array, index++));
		file.setBytes((byte[]) array[index++]);
	}
	
}