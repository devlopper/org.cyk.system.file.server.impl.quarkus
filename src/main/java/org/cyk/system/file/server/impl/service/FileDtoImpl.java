package org.cyk.system.file.server.impl.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.json.bind.annotation.JsonbProperty;

import org.cyk.system.file.server.api.service.FileDto;
import org.cyk.utility.persistence.entity.AbstractIdentifiableSystemScalarStringIdentifiableBusinessStringNamableImpl;
import org.cyk.utility.service.entity.AbstractIdentifiableSystemScalarStringAuditedImpl;
import org.cyk.utility.service.server.AbstractServiceImpl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class FileDtoImpl extends AbstractIdentifiableSystemScalarStringAuditedImpl implements FileDto,Serializable {

	@JsonbProperty(value = JSON_UNIFORM_RESOURCE_LOCATOR) String uniformResourceLocator;
	@JsonbProperty(value = JSON_NAME) String name;
	@JsonbProperty(value = JSON_EXTENSION) String extension;
	@JsonbProperty(value = JSON_MIME_TYPE) String mimeType;
	@JsonbProperty(value = JSON_SIZE) Long size;
	@JsonbProperty(value = JSON_SHA1) String sha1;
	@JsonbProperty(value = JSON_NAME_AND_EXTENSION) String nameAndExtension;
	@JsonbProperty(value = JSON_TEXT) String text;
	@JsonbProperty(value = JSON_DOWNLOAD_LINK) String downloadLink;
	
	public FileDtoImpl setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}
	
	static {
		Map<String,String> map = new HashMap<>();
		map.putAll(Map.of(
				JSON_IDENTIFIER,AbstractIdentifiableSystemScalarStringIdentifiableBusinessStringNamableImpl.FIELD_IDENTIFIER
    			));
		
		AbstractServiceImpl.setProjections(FileDtoImpl.class, map);
	}
}