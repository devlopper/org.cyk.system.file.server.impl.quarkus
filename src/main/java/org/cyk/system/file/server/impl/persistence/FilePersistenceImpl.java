package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cyk.quarkus.extension.hibernate.orm.AbstractSpecificPersistenceImpl;
import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.persistence.entity.EntityLifeCycleListener;

@ApplicationScoped
public class FilePersistenceImpl extends AbstractSpecificPersistenceImpl<File>  implements FilePersistence,Serializable{

	@Inject EntityManager entityManager;
	
	public FilePersistenceImpl() {
		entityClass = File.class;
		entityImplClass = FileImpl.class;
	}

	@Override
	public Collection<String> readUniformResourceLocators() {
		return entityManager.createNamedQuery(FileImpl.QUERY_READ_UNIFORM_RESOURCE_LOCATOR,String.class).getResultList();
	}
	
	@Override
	public Collection<String> readSha1HavingCountSha1GreaterThanOne() {
		return entityManager.createNamedQuery(FileImpl.QUERY_READ_SHA1_HAVING_COUNT_SHA1_GREATER_THAN_ONE,String.class).getResultList();
	}
	
	@Override
	public Collection<File> readWhereSha1IsNull() {
		return CollectionHelper.cast(File.class, entityManager.createNamedQuery(FileImpl.QUERY_READ_WHERE_SHA1_IS_NULL, FileImpl.class).getResultList());
	}

	@Override
	public Integer updateAuditsByIdentifiers(Collection<String> identifiers,String auditIdentifier,String auditFunctionality,String auditWho,LocalDateTime auditWhen) {
		return entityManager.createNamedQuery(FileImpl.QUERY_UPDATE_AUDITS_BY_IDENTIFIERS).setParameter("identifiers", identifiers).setParameter("auditIdentifier", auditIdentifier)
				.setParameter("auditFunctionality", auditFunctionality).setParameter("auditWhat", EntityLifeCycleListener.Event.UPDATE.getValue()).setParameter("auditWho", auditWho).setParameter("auditWhen", auditWhen).executeUpdate();
	}

	@Override
	public Integer deleteByIdentifiers(Collection<String> identifiers) {
		return entityManager.createNamedQuery(FileImpl.QUERY_DELETE_BY_IDENTIFIERS).setParameter("identifiers", identifiers).executeUpdate();
	}
}