package org.cyk.system.file.server.impl.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.api.service.FileDto;
import org.cyk.system.file.server.api.service.FileService;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.constant.ConstantString;
import org.cyk.utility.business.Result;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.FilterFormat;
import org.cyk.utility.service.server.AbstractSpecificServiceImpl;

@ApplicationScoped
public class FileServiceImpl extends AbstractSpecificServiceImpl<FileDto,FileDtoImpl,File,FileImpl> implements FileService,Serializable{

	@Inject FileBusiness business;
	@Inject FileDtoImplMapper mapper;
	
	public FileServiceImpl() {
		this.serviceEntityClass = FileDto.class;
		this.serviceEntityImplClass = FileDtoImpl.class;
		this.persistenceEntityClass = File.class;
		this.persistenceEntityImplClass = FileImpl.class;
	}
	
	@Override
	public Response countInDirectories(List<String> pathsNames, String acceptedPathNameRegularExpression, Long minimalSize,Long maximalSize) {
		return buildResponseOk(business.countInDirectories(pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize));
	}
	
	@Override
	public Response import_(List<String> pathsNames, String acceptedPathNameRegularExpression, Long minimalSize,Long maximalSize,Boolean isDuplicateAllowed, String auditWho) {
		return buildResponseOk(business.import_(pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize,isDuplicateAllowed, auditWho));
	}

	@Override
	public Response download(String identifier, Boolean isInline) {
		Result result = business.download(identifier);
		if(result == null || result.getValue() == null)
			return Response.status(Status.NOT_FOUND).build();
		FileImpl file = (FileImpl) result.getValue();
		ResponseBuilder responseBuilder = Response.ok(file.getBytes());
		responseBuilder.header(HttpHeaders.CONTENT_TYPE, file.getMimeType());
		responseBuilder.header(HttpHeaders.CONTENT_DISPOSITION,String.format(CONTENT_DISPOSITION_FORMAT, Boolean.TRUE.equals(isInline) ? ConstantString.INLINE : ConstantString.ATTACHMENT,file.getNameAndExtension()));
		responseBuilder.header(HttpHeaders.CONTENT_LENGTH, file.getBytes().length);
		return responseBuilder.build();
	}
	
	@Override
	public Response computeSha1(String auditWho) {
		return buildResponseOk(business.computeSha1(auditWho));
	}
	
	@Override
	public Response getDuplicatesIdentifiers() {
		return get(JsonbBuilder.create().toJson(new Filter.Dto().addField(Parameters.DUPLICATED, Boolean.TRUE)), FilterFormat.JSON, List.of(FileImpl.FIELD_IDENTIFIER), Boolean.FALSE, Boolean.FALSE, null, null);
	}
	
	@Override
	public Response countDuplicates() {
		return count(JsonbBuilder.create().toJson(new Filter.Dto().addField(Parameters.DUPLICATED, Boolean.TRUE)), FilterFormat.JSON);
	}
	
	@Override
	public Response deleteDuplicates(String auditWho) {
		return buildResponseOk(business.deleteDuplicates(auditWho));
	}
	
	/**/
	
	static final String CONTENT_DISPOSITION_FORMAT = "%s; "+ConstantString.FILENAME+"=%s";	
}