package org.cyk.system.file.server.impl.business;

import java.io.IOException;
import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.cyk.system.file.server.api.business.TextExtractor;
import org.cyk.system.file.server.api.business.TextNormalizer;
import org.cyk.system.file.server.impl.business.FileBusinessImpl.ResultKey;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.file.FileHelper;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceLocatorHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Result;

public abstract class AbstractTextExtractorImpl extends AbstractObject implements TextExtractor,Serializable {

	@Inject Configuration configuration;
	@Inject TextNormalizer textNormalizer;
	
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
			text = textNormalizer.normalize(text,result);
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
		PDDocument document = null;
		try {
			document = PDDocument.load(bytes);
			PDFTextStripper textStripper = new PDFTextStripper();
			return StringUtils.stripToNull(textStripper.getText(document)) == null;
		}catch(Exception exception) {
			throw new RuntimeException(exception);
		}finally {
			if(document != null)
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}