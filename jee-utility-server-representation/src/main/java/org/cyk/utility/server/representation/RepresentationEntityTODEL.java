package org.cyk.utility.server.representation;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.persistence.query.filter.Filter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * @author Christian
 *
 */
@Api
public interface RepresentationEntityTODEL<PERSISTENCE_ENTITY,ENTITY,ENTITY_COLLECTION> extends RepresentationServiceProvider {

	/* Create */
	
	@POST
	@Path(PATH_CREATE_ONE)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@ApiOperation(value = "create one",tags = {"create"})
	Response createOne(ENTITY entity);
	
	@POST
	@Path(PATH_CREATE_MANY)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@ApiOperation(value = "create many using collection",tags = {"create"})
	Response createMany(Collection<ENTITY> entities,@QueryParam(PARAMETER_PROPERTIES) String properties);
	
	@POST
	@Path(PATH_CREATE_MANY_COLLECTION)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@ApiOperation(value = "create many using custom collection",tags = {"create"})
	Response createMany(ENTITY_COLLECTION entityCollection,@QueryParam(PARAMETER_PROPERTIES) String properties);
	
	/* Read */ 
	
	@GET
	@Path(PATH_GET_MANY)
	@Produces({ MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML })
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	Response getMany(@QueryParam(PARAMETER_IS_PAGEABLE) Boolean isPageable,@QueryParam(PARAMETER_FROM) Long from,@QueryParam(PARAMETER_COUNT) Long count,@QueryParam(PARAMETER_FIELDS) String fields
			,@QueryParam(PARAMETER_FILTER) Filter.Dto filter);
	
	@GET
	@Path(PATH_GET_ONE)
	@Produces({ MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML })
	Response getOne(@PathParam(PARAMETER_IDENTIFIER) String identifier,@QueryParam(PARAMETER_TYPE) String type,@QueryParam(PARAMETER_FIELDS) String fields);
	
	/* Update */
	
	/* Using partial */
	//@PATCH FIXME Not working so we will use PUT for the moment
	@PUT
	@Path(PATH_UPDATE_ONE)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	Response updateOne(ENTITY entity,@QueryParam(PARAMETER_FIELDS) String fields);
	
	//@PATCH FIXME Not working so we will use PUT for the moment
	@PUT
	@Path(PATH_UPDATE_MANY)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	Response updateMany(Collection<ENTITY> entities,@QueryParam(PARAMETER_FIELDS) String fields);
	
	/* Delete */
	
	@DELETE
	@Path(PATH_DELETE_ONE)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	//Response deleteOne(@PathParam(PARAMETER_IDENTIFIER) String identifier,@QueryParam(PARAMETER_TYPE) String type);
	Response deleteOne(ENTITY entity);
	
	@DELETE
	@Path(PATH_DELETE_MANY)
	Response deleteMany();
	
	@DELETE
	@Path(PATH_DELETE_IDENTIFIERS)
	Response deleteByIdentifiers(@QueryParam(PARAMETER_IDENTIFIER) List<String> identifiers,@QueryParam(PARAMETER_TYPE) String type);
	
	@DELETE
	@Path(PATH_DELETE_ALL)
	Response deleteAll();
	
	/* Save */
	
	@POST
	@Path(PATH_SAVE_FROM_FILE_EXCEL_SHEET)
	Response saveFromFileExcelSheet(@QueryParam(PARAMETER_WORKBOOK_NAME) String workbookName,@QueryParam(PARAMETER_SHEET_NAME) String sheetName
			,@QueryParam(PARAMETER_COLUMN_INDEX_FIELD_NAME) List<String> columnIndexFieldNames);
	
	/* Count */
	
	@GET
	@Path(PATH_GET_COUNT)
	Response count(@QueryParam(PARAMETER_FILTER) Filter.Dto filter);
	
	/* Import */

	@POST
	@Path(PATH_IMPORT)
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Produces({ MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML })
	Response import_(@QueryParam(PARAMETER_UNIFORM_RESOURCE_IDENTIFIER) List<String> uniformResourceIdentifiers,Boolean ignoreKnownUniformResourceIdentifiers);
}