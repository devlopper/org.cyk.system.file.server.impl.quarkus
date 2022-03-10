package org.cyk.system.file.server.api.persistence;

import java.util.Collection;

public interface FilePersistence extends org.cyk.utility.persistence.SpecificPersistence<File>{

	Collection<String> readUniformResourceLocators();

}