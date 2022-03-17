package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cyk.quarkus.extension.hibernate.orm.AbstractSpecificPersistenceImpl;
import org.cyk.system.file.server.api.persistence.FileText;
import org.cyk.system.file.server.api.persistence.FileTextPersistence;

@ApplicationScoped
public class FileTextPersistenceImpl extends AbstractSpecificPersistenceImpl<FileText>  implements FileTextPersistence,Serializable{

	@Inject EntityManager entityManager;
	
	public FileTextPersistenceImpl() {
		entityClass = FileText.class;
		entityImplClass = FileTextImpl.class;
	}
}