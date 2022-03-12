package org.cyk.system.file.server.api.business;

import java.util.Collection;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.utility.business.Result;

public interface FileBusiness extends org.cyk.utility.business.SpecificBusiness<File> {

	Result countInDirectories(Collection<String> pathsNames,String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize);
	
	String IMPORT_AUDIT_IDENTIFIER = "FILE_IMPORTATION";
	Result import_(Collection<String> pathsNames,String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,String auditWho);
	
	String DOWNLOAD_AUDIT_IDENTIFIER = "FILE_DOWNLOAD";
	Result download(String identifier);
	
	String COMPUTE_SHA1_AUDIT_IDENTIFIER = "FILE_SHA1_COMPUTATION";
	Result computeSha1(String auditWho);
	
	String READ_DUPLICATED_SHA1_AUDIT_IDENTIFIER = "FILE_DUPLICATED_SHA1_READ";
	Result readDuplicatedSha1();
	
	String COUNT_DUPLICATED_SHA1_AUDIT_IDENTIFIER = "FILE_DUPLICATED_SHA1_COUNT";
	Result countDuplicatedSha1();
	
	String COUNT_DUPLICATED_AUDIT_IDENTIFIER = "FILE_DUPLICATED_COUNT";
	Result countDuplicated();
	
	String DELETE_DUPLICATED_AUDIT_IDENTIFIER = "FILE_DELETE_DUPLICATED";
	Result deleteDuplicated(String auditWho);
	
	/*
	String EXTRACT_BYTES_AUDIT_IDENTIFIER = "FILE_BYTES_EXTRACTION";
	Result extractBytes(Collection<String> identifiers);
	Result extractBytes(String...identifiers);
	Result extractBytesAll();
	
	String EXTRACT_TEXT_AUDIT_IDENTIFIER = "FILE_TEXT_EXTRACTION";
	Result extractText(Collection<String> identifiers);
	Result extractText();
	*/
	
}