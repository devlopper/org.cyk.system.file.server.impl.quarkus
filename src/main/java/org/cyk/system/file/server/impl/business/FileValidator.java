package org.cyk.system.file.server.impl.business;

import java.util.Collection;
import java.util.List;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.throwable.ThrowablesMessages;
import org.cyk.utility.business.Validator;

public interface FileValidator {

	static void validatePathsNames(List<String> directories,Collection<String> pathsNames,ThrowablesMessages throwablesMessages) {
		//String directory = directories.get(0);//TODO for now we handle only one but do it for many
		if(CollectionHelper.isNotEmpty(pathsNames))
			pathsNames.forEach(pathName -> {
				//if(!pathName.startsWith(directory))
				//	throwablesMessages.add(String.format("%s must be a sub path of %s", pathName,directory));
			});
	}
	
	/* Import */
	
	static void validateImportInputs(List<String> directories,Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,String auditWho,Boolean isDuplicateAllowed,ThrowablesMessages throwablesMessages) {
		validatePathsNames(directories, pathsNames, throwablesMessages);
		Validator.getInstance().validateAuditWho(auditWho, throwablesMessages);	
	}
	
	static void validateImportInputs(List<String> directories,Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,Boolean isDuplicateAllowed,String auditWho) {
		ThrowablesMessages throwablesMessages = new ThrowablesMessages();
		validateImportInputs(directories,pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize, auditWho,isDuplicateAllowed,throwablesMessages);
		throwablesMessages.throwIfNotEmpty();
	}
	
	/* Count in directories */
	
	static void validateCountInDirectoriesInputs(List<String> directories,Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,ThrowablesMessages throwablesMessages) {
		validatePathsNames(directories, pathsNames, throwablesMessages);
	}
	
	static void validateCountInDirectoriesInputs(List<String> directories,Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize) {
		ThrowablesMessages throwablesMessages = new ThrowablesMessages();
		validateCountInDirectoriesInputs(directories,pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize,throwablesMessages);
		throwablesMessages.throwIfNotEmpty();
	}
	
	/* Download */
	
	static void validateDownloadInputs(String identifier,ThrowablesMessages throwablesMessages) {
		Validator.AbstractImpl.validateIdentifier(identifier, File.NAME, throwablesMessages);
	}
	
	static void validateDownloadInputs(String identifier) {
		ThrowablesMessages throwablesMessages = new ThrowablesMessages();
		validateDownloadInputs(identifier,throwablesMessages);
		throwablesMessages.throwIfNotEmpty();
	}
	
	/* Compute sha1 */
	
	static void validateComputeSha1Inputs(String auditWho,ThrowablesMessages throwablesMessages) {
		Validator.getInstance().validateAuditWho(auditWho, throwablesMessages);
	}
	
	static void validateComputeSha1Inputs(String auditWho) {
		ThrowablesMessages throwablesMessages = new ThrowablesMessages();
		validateComputeSha1Inputs(auditWho,throwablesMessages);
		throwablesMessages.throwIfNotEmpty();
	}
	
	/* Delete duplicated */
	
	static void validateDeleteDuplicatedInputs(String auditWho,ThrowablesMessages throwablesMessages) {
		Validator.getInstance().validateAuditWho(auditWho, throwablesMessages);
	}
	
	static void validateDeleteDuplicatedInputs(String auditWho) {
		ThrowablesMessages throwablesMessages = new ThrowablesMessages();
		validateDeleteDuplicatedInputs(auditWho,throwablesMessages);
		throwablesMessages.throwIfNotEmpty();
	}
}