package org.cyk.system.file.server.impl.business;
import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.klass.ClassHelper;
import org.cyk.utility.__kernel__.random.RandomHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Initializer;
import org.cyk.utility.file.FileHelper;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @org.cyk.system.file.server.api.System @Unremovable
public class InitializerImpl extends Initializer.AbstractImpl implements Serializable {

	@Override
	protected <T> void ____initialize____(Class<T> klass, T entity, Object actionIdentifier) {
		super.____initialize____(klass, entity, actionIdentifier);
		if(Boolean.TRUE.equals(ClassHelper.isInstanceOf(klass, FileImpl.class)))
			initialize((FileImpl) entity,actionIdentifier);
	}
	
	/**/
	
	private void initialize(FileImpl file,Object actionIdentifier) {
		if(StringHelper.isBlank(file.getSha1())) {
			if(file.getBytes() == null) {
				//TODO get a way to compute sha1 : from given uniform resource locator
			}else
				file.setSha1(FileHelper.computeSha1(file.getBytes()));
		}

		if(StringHelper.isBlank(file.getName()) && StringHelper.isNotBlank(file.getNameAndExtension()))				
			file.setName(FileHelper.getName(file.getNameAndExtension()));
		
		if(StringHelper.isBlank(file.getExtension()) && StringHelper.isNotBlank(file.getNameAndExtension()))				
			file.setExtension(FileHelper.getExtension(file.getNameAndExtension()));
		
		if(StringHelper.isBlank(file.getMimeType()) && StringHelper.isNotBlank(file.getExtension()))			
			file.setMimeType(FileHelper.getMimeTypeByExtension(file.getExtension()));
		
		if(StringHelper.isBlank(file.getMimeType()) && StringHelper.isNotBlank(file.getNameAndExtension()))
			file.setMimeType(FileHelper.getMimeTypeByNameAndExtension(file.getNameAndExtension()));
		
		if(file.getSize() == null && file.getBytes() != null)				
			file.setSize(Long.valueOf(file.getBytes().length));
		
		if(StringHelper.isBlank(file.getName()))
			file.setName(RandomHelper.getAlphabetic(10));
	}
}