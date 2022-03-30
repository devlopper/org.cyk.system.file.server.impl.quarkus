package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.system.file.server.impl.Tika;
import org.cyk.system.file.server.impl.business.FileBusinessImpl.ResultKey;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceLocatorHelper;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.business.Result;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped @Tika
public class TextExtractorTikaImpl extends AbstractTextExtractorImpl implements Serializable {

	@Inject @RestClient TikaClient client;
	
	@Override
	String computeMimeType(byte[] bytes, String uniformResourceLocator) {
		if(bytes != null)
			return client.getMimeType(bytes,"attachment; "+uniformResourceLocator);
		return null;
	}

	@Override
	String __extractWhereMimeIsNotText__(byte[] bytes, String mimeType) {
		return TikaDto.getContent(client.getTextByBytes(bytes));
	}

	@Override
	String __extractUsingOpticalCharacterRecognition__(byte[] bytes, String mimeType) {
		return TikaDto.getContent(client.getTextByBytes(bytes,TikaClient.HEADER_PARAMETER_X_TIKA_PDF_OCR_STRATEGY_OCR_ONLY,TikaClient.HEADER_PARAMETER_X_TIKA_PDF_EXTRACT_IN_LINE_IMAGES_TRUE));
	}
	
	@Override
	String __extract__(String uniformResourceLocator, String mimeType,Result result) {
		String[] fetchDetails = computeFetchDetails(uniformResourceLocator);
		if(fetchDetails == null)
			return super.__extract__(uniformResourceLocator, mimeType,result);
		Boolean isScannedPdf = isScannedPdf(UniformResourceLocatorHelper.getBytes(uniformResourceLocator), mimeType);
		if(result != null)
			result.map(ResultKey.TEXT_EXTRACTOR,isScannedPdf ? ResultKey.TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION : ResultKey.TEXT_EXTRACTOR_OTHERS);
		return TikaDto.getContent(client.getTextByFetch(fetchDetails[0], fetchDetails[1],isScannedPdf ? TikaClient.HEADER_PARAMETER_X_TIKA_PDF_OCR_STRATEGY_OCR_ONLY : null
				,isScannedPdf ? TikaClient.HEADER_PARAMETER_X_TIKA_PDF_EXTRACT_IN_LINE_IMAGES_TRUE : null));
	}
	
	public String[] computeFetchDetails(String uniformResourceLocator) {
		for(Configuration.Tika.Server.File.Fetcher fetcher : configuration.tika().server().file().fetchers()) {
			Matcher matcher = Pattern.compile(fetcher.path().regularExpression()).matcher(uniformResourceLocator);
			if(matcher.find())
				return new String[] {fetcher.name(),matcher.group(2)};
		}
		LogHelper.logWarning(String.format("No fetcher found for <<%s>>", uniformResourceLocator), getClass());
		return null;
	}
}