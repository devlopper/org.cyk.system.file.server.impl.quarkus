package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cyk.quarkus.extension.hibernate.orm.AbstractSpecificPersistenceImpl;
import org.cyk.system.file.server.api.persistence.FileBytes;
import org.cyk.system.file.server.api.persistence.FileBytesPersistence;

@ApplicationScoped
public class FileBytesPersistenceImpl extends AbstractSpecificPersistenceImpl<FileBytes>  implements FileBytesPersistence,Serializable{

	@Inject EntityManager entityManager;
	
	public FileBytesPersistenceImpl() {
		entityClass = FileBytes.class;
		entityImplClass = FileBytesImpl.class;
	}
}