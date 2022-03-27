package org.cyk.system.file.server.impl.business;

import java.io.Serializable;

import javax.json.bind.annotation.JsonbProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class TikaDto implements Serializable {

	@JsonbProperty(value = "Content-Type") String contentType;
	@JsonbProperty(value = "X-TIKA:content") String content;
	
}