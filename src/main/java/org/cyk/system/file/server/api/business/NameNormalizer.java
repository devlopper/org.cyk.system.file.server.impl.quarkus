package org.cyk.system.file.server.api.business;

import org.cyk.utility.business.Result;

public interface NameNormalizer {

	String normalize(String name,Result result);
	String normalize(String name);
}