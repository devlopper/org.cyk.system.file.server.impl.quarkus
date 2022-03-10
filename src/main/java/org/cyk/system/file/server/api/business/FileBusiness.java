package org.cyk.system.file.server.api.business;

import java.util.Collection;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.utility.business.Result;

public interface FileBusiness extends org.cyk.utility.business.SpecificBusiness<File> {

	String IMPORT_AUDIT_IDENTIFIER = "FILE_IMPORTATION";
	Result import_(Collection<String> pathsNames,String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,String auditWho);
	
	Result countInDirectories(Collection<String> pathsNames,String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize);
	/*
	String EXTRACT_BYTES_AUDIT_IDENTIFIER = "FILE_BYTES_EXTRACTION";
	Result extractBytes(Collection<String> identifiers);
	Result extractBytes(String...identifiers);
	Result extractBytesAll();
	
	String EXTRACT_TEXT_AUDIT_IDENTIFIER = "FILE_TEXT_EXTRACTION";
	Result extractText(Collection<String> identifiers);
	Result extractText();
	*/
	String DOWNLOAD_AUDIT_IDENTIFIER = "FILE_DOWNLOADING";
	Result download(String identifier);
}