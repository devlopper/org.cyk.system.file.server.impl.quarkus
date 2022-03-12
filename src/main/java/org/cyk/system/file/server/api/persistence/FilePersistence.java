package org.cyk.system.file.server.api.persistence;

import java.time.LocalDateTime;
import java.util.Collection;

public interface FilePersistence extends org.cyk.utility.persistence.SpecificPersistence<File>{

	Collection<String> readUniformResourceLocators();

	Collection<String> readSha1HavingCountSha1GreaterThanOne();
	
	Collection<File> readWhereSha1IsNull();
	
	Integer updateAuditsByIdentifiers(Collection<String> identifiers,String auditIdentifier,String auditFunctionality,String auditWho,LocalDateTime auditWhen);
	
	Integer deleteByIdentifiers(Collection<String> identifiers);
}