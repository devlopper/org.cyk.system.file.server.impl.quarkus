package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.cyk.system.file.server.api.business.TextExtractor;
import org.cyk.system.file.server.impl.business.FileBusinessImpl.ResultKey;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.file.FileHelper;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceLocatorHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Result;

public abstract class AbstractTextExtractorImpl extends AbstractObject implements TextExtractor,Serializable {

	@Inject Configuration configuration;
	
	@Override
	public String extract(byte[] bytes,String uniformResourceLocator, String mimeType,Result result) {
		if(StringHelper.isBlank(uniformResourceLocator) && bytes == null)
			throw new IllegalArgumentException("bytes or uniform resource locator must be provided");
		if(StringHelper.isBlank(mimeType) && StringHelper.isNotBlank(uniformResourceLocator))
			mimeType = FileHelper.getMimeTypeByNameAndExtension(uniformResourceLocator);
			
		if(StringHelper.isBlank(mimeType))
			mimeType = computeMimeType(bytes,uniformResourceLocator);
		return __extract__(bytes,uniformResourceLocator, mimeType,result);
	}
	
	abstract String computeMimeType(byte[] bytes,String uniformResourceLocator);
	
	String __extract__(byte[] bytes,String uniformResourceLocator,String mimeType,Result result) {
		if(bytes != null)
			return __extract__(bytes, mimeType,result);
		return __extract__(uniformResourceLocator, mimeType,result);
	}
	
	String __extract__(byte[] bytes,String mimeType,Result result) {
		if(isText(bytes, mimeType)) {
			if(result != null)
				result.map(ResultKey.TEXT_EXTRACTOR, ResultKey.TEXT_EXTRACTOR_TEXT);
			return __extractWhereMimeIsText__(bytes);
		}
		if(isScannedPdf(bytes, mimeType)) {
			if(result != null)
				result.map(ResultKey.TEXT_EXTRACTOR, ResultKey.TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION);
			String text = __extractUsingOpticalCharacterRecognition__(bytes, mimeType);
			text = normalize(text);
			return text;
		}
		if(result != null)
			result.map(ResultKey.TEXT_EXTRACTOR, ResultKey.TEXT_EXTRACTOR_OTHERS);
		return __extractWhereMimeIsNotText__(bytes, mimeType);
	}
	
	String __extractWhereMimeIsText__(byte[] bytes) {
		return new String(bytes);
	}

	abstract String __extractUsingOpticalCharacterRecognition__(byte[] bytes,String mimeType);
	
	abstract String __extractWhereMimeIsNotText__(byte[] bytes,String mimeType);
	
	String __extract__(String uniformResourceLocator,String mimeType,Result result) {
		byte[] bytes = UniformResourceLocatorHelper.getBytes(uniformResourceLocator);
		if(bytes == null)
			return null;
		return __extract__(bytes, mimeType,result);
	}
	
	/**/
	
	Boolean isText(byte[] bytes,String mimeType) {
		return StringUtils.startsWithIgnoreCase(mimeType, "text/");
	}
	
	Boolean isScannedPdf(byte[] bytes,String mimeType) {
		if(!StringUtils.startsWithIgnoreCase(mimeType, "application/pdf"))
			return Boolean.FALSE;
		try {
			PDDocument document = PDDocument.load(bytes);
			PDFTextStripper textStripper = new PDFTextStripper();
			return StringUtils.stripToNull(textStripper.getText(document)) == null;
		}catch(Exception exception) {
			throw new RuntimeException(exception);
		}		
	}
	
	/**/
	
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
}