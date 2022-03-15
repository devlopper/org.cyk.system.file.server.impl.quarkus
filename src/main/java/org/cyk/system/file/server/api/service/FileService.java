package org.cyk.system.file.server.api.service;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path(FileService.PATH)
@Tag(name = "Files",description = "Manage files")
public interface FileService extends org.cyk.utility.service.SpecificService<FileDto>{
	String PATH = "files";
	
	@GET
	@Path("number-in-directories")
	@Produces({MediaType.TEXT_PLAIN})
	@Operation(description = "Get number of files in directories")
	@APIResponses(value = {
			@APIResponse(description = "Number of files into directories got",responseCode = "200", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while getting number of files into directories",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response countInDirectories(@Parameter(name = PARAMETER_PATHS_NAMES) @QueryParam(PARAMETER_PATHS_NAMES) List<String> pathsNames
			,@Parameter(name = PARAMETER_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION) @QueryParam(PARAMETER_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION) String acceptedPathNameRegularExpression
			,@Parameter(name = PARAMETER_MINIMAL_SIZE) @QueryParam(PARAMETER_MINIMAL_SIZE) Long minimalSize
			,@Parameter(name = PARAMETER_MAXIMAL_SIZE) @QueryParam(PARAMETER_MAXIMAL_SIZE) Long maximalSize);
	
	@POST
	@Path("importation")
	@Produces({MediaType.TEXT_PLAIN})
	@Operation(description = "Import files into database from sources")
	@APIResponses(value = {
			@APIResponse(description = "Files imported",responseCode = "201", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while importing files",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response import_(@Parameter(name = PARAMETER_PATHS_NAMES) @QueryParam(PARAMETER_PATHS_NAMES) List<String> pathsNames
			,@Parameter(name = PARAMETER_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION) @QueryParam(PARAMETER_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION) String acceptedPathNameRegularExpression
			,@Parameter(name = PARAMETER_MINIMAL_SIZE) @QueryParam(PARAMETER_MINIMAL_SIZE) Long minimalSize
			,@Parameter(name = PARAMETER_MAXIMAL_SIZE) @QueryParam(PARAMETER_MAXIMAL_SIZE) Long maximalSize
			,@Parameter(name = PARAMETER_IS_DUPLICATE_ALLOWED) @QueryParam(PARAMETER_IS_DUPLICATE_ALLOWED) Boolean isDuplicateAllowed
			,@Parameter(name = PARAMETER_USERNAME,required = true) @QueryParam(PARAMETER_USERNAME) String auditWho);
	
	String DOWNLOAD = "download";
	String DOWNLOAD_PATH_FORMAT = "%s/"+DOWNLOAD;
	String DOWNLOAD_PATH = "{"+PARAMETER_IDENTIFIER+"}/"+DOWNLOAD;
	@GET
	@Path(DOWNLOAD_PATH)
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	@Operation(description = "Download file")
	@APIResponses(value = {
			@APIResponse(description = "Download of file got",responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM))
			,@APIResponse(description = "Download of file not found",responseCode = "400", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while downloading file",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response download(@PathParam(PARAMETER_IDENTIFIER) String identifier,@QueryParam(PARAMETER_IS_INLINE) Boolean isInline);
	
	@GET
	@Path("duplicates-identifiers")
	@Produces({MediaType.APPLICATION_JSON})
	@Operation(description = "Get duplicates identifiers")
	@APIResponses(value = {
			@APIResponse(description = "Duplicates identifiers got",responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON))
			,@APIResponse(description = "Error while getting duplicates identifiers",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response getDuplicatesIdentifiers();
	
	@GET
	@Path("duplicates-number")
	@Produces({MediaType.TEXT_PLAIN})
	@Operation(description = "Get number of duplicates")
	@APIResponses(value = {
			@APIResponse(description = "Number of duplicates got",responseCode = "200", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while getting number of duplicates",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response countDuplicates();
	
	@DELETE
	@Path("duplicates")
	@Produces({MediaType.TEXT_PLAIN})
	@Operation(description = "Delete duplicates")
	@APIResponses(value = {
			@APIResponse(description = "Duplicates deleted",responseCode = "200", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while deleting duplicates",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response deleteDuplicates(@Parameter(name = PARAMETER_USERNAME,required = true) @QueryParam(PARAMETER_USERNAME) String auditWho);
	
	/*
	@POST
	@Path("extraction-of-all-bytes")
	@Produces({MediaType.TEXT_PLAIN})
	@Operation(description = "Extract all bytes of all files")
	@APIResponses(value = {
			@APIResponse(description = "Bytes of all files extracted",responseCode = "200", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while extracting bytes of all files",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response extractBytesOfAll();
	
	@POST
	@Path("extraction-of-bytes")
	@Produces({ MediaType.TEXT_PLAIN})
	@Operation(description = "Extract bytes of files")
	@APIResponses(value = {
			@APIResponse(description = "Bytes of files extracted",responseCode = "200", content = @Content(mediaType = MediaType.TEXT_PLAIN))
			,@APIResponse(description = "Error while extracting bytes of files",responseCode = "500", content = @Content(mediaType = MediaType.APPLICATION_JSON))
	})
	Response extractBytes(@Parameter(name = PARAMETER_IDENTIFIERS) @QueryParam(PARAMETER_IDENTIFIERS) List<String> identifiers);
	*/
	/**/
	
	String PARAMETER_PATHS_NAMES = "paths";
	String PARAMETER_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION = "accepted";
	String PARAMETER_MINIMAL_SIZE = "minimal_size";
	String PARAMETER_MAXIMAL_SIZE = "maximal_size";
	String PARAMETER_IS_SHA1_COMPUTATION_BLOCKING = "is_sha1_computation_blocking";
	String PARAMETER_IS_DUPLICATES_DELETION_BLOCKING = "is_duplicates_deletion_blocking";
	String PARAMETER_IS_DUPLICATE_ALLOWED = "is_duplicate_allowed";
	String PARAMETER_USERNAME = "username";
}