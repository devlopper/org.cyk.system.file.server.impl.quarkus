package org.cyk.system.file.server.impl.business;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/")
@RegisterRestClient(configKey = "tika")
public interface TikaClient {

	@GET
	@Path("/tika")
	@Produces({MediaType.TEXT_PLAIN})
	String ping();
	
	/* Get text by bytes */
	
	@PUT
	@Path(PATH_GET_TEXT)
	@Consumes({MediaType.APPLICATION_OCTET_STREAM})
	@Produces({MediaType.APPLICATION_JSON})
	TikaDto getTextByBytes(byte[] bytes);
	
	@PUT
	@Path(PATH_GET_TEXT)
	@Consumes({MediaType.APPLICATION_OCTET_STREAM})
	@Produces({MediaType.APPLICATION_JSON})
	TikaDto getTextByBytes(byte[] bytes,@HeaderParam("X-Tika-PDFOcrStrategy") String pdfOcrStrategy,@HeaderParam("X-Tika-PDFextractInlineImages") String pdfExtractInlineImages);
	
	/* Get text by fetch */
	
	@PUT
	@Path(PATH_GET_TEXT)
	@Consumes({MediaType.APPLICATION_OCTET_STREAM})
	@Produces({MediaType.APPLICATION_JSON})
	TikaDto getTextByFetch(@HeaderParam("fetcherName") String fetcherName,@HeaderParam("fetchKey") String fetchKey);
	
	/**/
	
	String PATH_GET_TEXT = "tika/text";
	String HEADER_PARAMETER_X_TIKA_PDF_OCR_STRATEGY_OCR_ONLY = "ocr_only";
	String HEADER_PARAMETER_X_TIKA_PDF_EXTRACT_IN_LINE_IMAGES_TRUE = "true";
}