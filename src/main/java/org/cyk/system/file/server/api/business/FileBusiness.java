package org.cyk.system.file.server.api.business;

import java.util.Collection;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.utility.business.Result;

public interface FileBusiness extends org.cyk.utility.business.SpecificBusiness<File> {

	Result countInDirectories(Collection<String> pathsNames,String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize);
	
	String IMPORT_AUDIT_IDENTIFIER = "FILE_IMPORTATION";
	Result import_(Collection<String> pathsNames,String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,Boolean isDuplicateAllowed,String auditWho);
	
	String DOWNLOAD_AUDIT_IDENTIFIER = "FILE_DOWNLOAD";
	Result download(String identifier);
	
	String DELETE_DUPLICATES_AUDIT_IDENTIFIER = "FILE_DUPLICATES_DELETION";
	Result deleteDuplicates(String auditWho);
	
	String EXTRACT_BYTES_AUDIT_IDENTIFIER = "FILE_BYTES_EXTRACTION";
	Result extractBytes(Collection<String> identifiers,String auditWho);
	Result extractBytes(String auditWho,String...identifiers);
	
	String EXTRACT_BYTES_OF_ALL_AUDIT_IDENTIFIER = "FILE_BYTES_OF_ALL_EXTRACTION";
	Result extractBytesOfAll(String auditWho);
	
	String EXTRACT_TEXT_AUDIT_IDENTIFIER = "FILE_TEXT_EXTRACTION";
	Result extractText(Collection<String> identifiers,String auditWho);
	Result extractText(String auditWho,String...identifiers);
	
	String EXTRACT_TEXT_OF_ALL_AUDIT_IDENTIFIER = "FILE_TEXT_OF_ALL_EXTRACTION";
	Result extractTextOfAll(String auditWho);
}