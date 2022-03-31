package org.cyk.system.file.server.api.persistence;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.persistence.EntityManager;

public interface FilePersistence extends org.cyk.utility.persistence.SpecificPersistence<File>{

	String readIdentifierByUniformResourceLocator(String uniformResourceLocator);
	
	Collection<String> readUniformResourceLocators();
	
	Collection<String> readNames();

	Collection<String> readSha1s();
	
	Collection<String> readSha1HavingCountSha1GreaterThanOne();
	
	Collection<File> readWhereSha1IsNull();
	
	Integer updateAuditsByIdentifiers(Collection<String> identifiers,String auditIdentifier,String auditFunctionality,String auditWho,LocalDateTime auditWhen,EntityManager entityManager);
	
	Integer deleteByIdentifiers(Collection<String> identifiers,EntityManager entityManager);
}