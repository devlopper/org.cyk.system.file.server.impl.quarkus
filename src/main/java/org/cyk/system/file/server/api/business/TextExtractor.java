package org.cyk.system.file.server.api.business;

import org.cyk.utility.business.Result;

public interface TextExtractor {

	String extract(byte[] bytes,String uniformResourceLocator,String mimeType,Result result);
	
}
