package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.cyk.system.file.server.api.business.FileTextBusiness;
import org.cyk.system.file.server.api.persistence.FileText;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.server.AbstractSpecificBusinessImpl;

@ApplicationScoped
public class FileTextBusinessImpl extends AbstractSpecificBusinessImpl<FileText> implements FileTextBusiness,Serializable {

	@Inject Configuration configuration;
	
	@Override
	public String normalize(String text) {
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
			if(line.length() < configuration.file().text().line().minimalLenght())
				return Boolean.FALSE;
			String[] words = line.split(configuration.file().text().line().word().separator());
			if(words != null && words.length > 0) {
				Integer count = 0;
				for(String word : words)
					if(word.strip().length() < configuration.file().text().line().word().minimalLenght())
						count++;
				
				if( ((words.length-count)*1.0 / words.length) < configuration.file().text().line().word().minimalRate())
					return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}).collect(Collectors.joining(configuration.file().text().line().separator()));
		if(StringHelper.isBlank(text))
			return null;
		
		return text;
	}
}