package org.cyk.system.file.server.impl.business;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.cyk.system.file.server.api.business.NameNormalizer;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Result;

public abstract class AbstractNameNormalizerImpl extends AbstractObject implements NameNormalizer,Serializable {

	@Inject Configuration configuration;
	
	@Override
	public String normalize(String name, Result result) {
		name = StringUtils.stripToNull(name);
		if(StringHelper.isBlank(name))
			return null;

		name = StringUtils.stripAccents(name); //Normalizer.normalize(name, configuration.file().name().normalizer().form());
		if(StringHelper.isBlank(name))
			return null;
			
		return name;
	}
	
	@Override
	public String normalize(String name) {
		return normalize(name, null);
	}
}