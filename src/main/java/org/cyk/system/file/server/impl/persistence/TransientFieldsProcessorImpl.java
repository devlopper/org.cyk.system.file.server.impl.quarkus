package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.persistence.query.Filter;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @org.cyk.system.file.server.api.System @Unremovable
public class TransientFieldsProcessorImpl extends org.cyk.utility.persistence.server.TransientFieldsProcessorImpl implements Serializable{

	@Override
	protected void __process__(Class<?> klass,Collection<?> objects,Filter filter, Collection<String> fieldsNames) {
		if(File.class.equals(klass) || FileImpl.class.equals(klass))
			processFiles(CollectionHelper.cast(FileImpl.class, objects),fieldsNames);
		else
			super.__process__(klass,objects,filter, fieldsNames);
	}
	
	public void processFiles(Collection<FileImpl> files,Collection<String> fieldsNames) {
		for(String fieldName : fieldsNames) {
			if(FileImpl.FIELDS_UNIFORM_RESOURCE_LOCATOR_NAME_AND_EXTENSION_MIME_TYPE_SIZE_BYTES.equals(fieldName))
				new FileImplUniformResourceLocatorNameAndExtensionMimeTypeSizeBytesReader().readThenSet(files, null);
		}
	}
}