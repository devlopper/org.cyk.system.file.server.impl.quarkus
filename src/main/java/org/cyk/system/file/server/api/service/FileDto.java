package org.cyk.system.file.server.api.service;

public interface FileDto extends org.cyk.utility.service.entity.IdentifiableSystemScalarString {

	String JSON_IDENTIFIER = "identifier";
	String JSON_DOWNLOAD_LINK = "download_link";
	String JSON_UNIFORM_RESOURCE_LOCATOR = "uniform_resource_locator";
	String JSON_NAME = "name";
	String JSON_EXTENSION = "extension";
	String JSON_MIME_TYPE = "mime_type";
	String JSON_SIZE = "size";
	String JSON_SHA1 = "sha1";
	String JSON_NAME_AND_EXTENSION = "name_and_extension";
	String JSON_TEXT = "text";
	String JSON_IS_A_DUPLICATE = "is_a_duplicate";
}