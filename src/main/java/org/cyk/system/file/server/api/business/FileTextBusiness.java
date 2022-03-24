package org.cyk.system.file.server.api.business;

import org.cyk.system.file.server.api.persistence.FileText;

public interface FileTextBusiness extends org.cyk.utility.business.SpecificBusiness<FileText> {

	String normalize(String text);
	
}