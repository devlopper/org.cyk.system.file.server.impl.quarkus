package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cyk.quarkus.extension.hibernate.orm.AbstractSpecificPersistenceImpl;
import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.api.persistence.FilePersistence;

@ApplicationScoped
public class FilePersistenceImpl extends AbstractSpecificPersistenceImpl<File>  implements FilePersistence,Serializable{

	@Inject EntityManager entityManager;
	
	public FilePersistenceImpl() {
		entityClass = File.class;
		entityImplClass = FileImpl.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> readUniformResourceLocators() {
		return entityManager.createNamedQuery(FileImpl.QUERY_READ_UNIFORM_RESOURCE_LOCATOR).getResultList();
	}
}