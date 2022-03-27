package org.cyk.system.file.server.impl.business;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/tika")
@RegisterRestClient(configKey = "tika")
public interface TikaClient {

	@GET
	@Produces({MediaType.TEXT_PLAIN})
	String ping();
	
	@PUT
	@Path("/text")
	@Consumes({MediaType.APPLICATION_OCTET_STREAM})
	@Produces({MediaType.APPLICATION_JSON})
	TikaDto getText(@HeaderParam("X-Tika-PDFOcrStrategy") String pdfOcrStrategy,@HeaderParam("X-Tika-PDFextractInlineImages") String pdfExtractInlineImages,byte[] bytes);
	
}