package org.cyk.system.file.server.api.business;

import org.cyk.utility.business.Result;

public interface TextNormalizer {

	String normalize(String text,Result result);
	String normalize(String text);
}