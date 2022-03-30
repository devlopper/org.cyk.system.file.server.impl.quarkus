package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.cyk.system.file.server.api.business.TextNormalizer;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Result;

public abstract class AbstractTextNormalizerImpl extends AbstractObject implements TextNormalizer,Serializable {

	@Inject Configuration configuration;
	
	@Override
	public String normalize(String text, Result result) {
		text = StringUtils.stripToNull(text);
		if(StringHelper.isBlank(text))
			return null;

		text = Normalizer.normalize(text, configuration.file().text().normalizer().form());
		if(StringHelper.isBlank(text))
			return null;
		
		for(Configuration.File.Text.Replacer replacer : configuration.file().text().replacers()) {
			text = text.replaceAll(replacer.regularExpression(), replacer.replacement());
			if(StringHelper.isBlank(text))
				return null;
		}
		
		text = text.lines().filter(line -> {
			if(line.length() < configuration.file().text().line().minimalLength())
				return Boolean.FALSE;
			String[] words = line.split(configuration.file().text().line().word().separator());
			if(words != null && words.length > 0) {
				Integer count = 0;
				for(String word : words)
					if(word.strip().length() < configuration.file().text().line().word().minimalLength())
						count++;
				
				if( ((words.length-count)*1.0 / words.length) < configuration.file().text().line().word().minimalRate())
					return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}).collect(Collectors.joining(configuration.file().text().line().separator()));
		if(StringHelper.isBlank(text))
			return null;
		
		if(text.length() < configuration.file().text().minimalLength())
			return null;
		
		return text;
	}
	
	@Override
	public String normalize(String text) {
		return normalize(text, null);
	}
}