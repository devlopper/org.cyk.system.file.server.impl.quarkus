package org.cyk.utility.__kernel__.controller;

import java.io.Serializable;
import java.util.Collection;

import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.Helper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.persistence.query.QueryExecutorArguments;
import org.cyk.utility.__kernel__.throwable.RuntimeException;
import org.cyk.utility.__kernel__.value.Value;

public interface EntityReader {

	<ENTITY> Collection<ENTITY> readMany(Class<ENTITY> controllerEntityClass,Arguments<ENTITY> arguments);
	
	default <ENTITY> Collection<ENTITY> readMany(Class<ENTITY> controllerEntityClass) {
		Arguments<ENTITY> arguments = new Arguments<ENTITY>();
		arguments.setControllerEntityClass(controllerEntityClass);
		return readMany(controllerEntityClass, arguments);
	}
	
	default <ENTITY> Collection<ENTITY> readMany(Class<ENTITY> controllerEntityClass,String queryIdentifier,Object...filterFieldsValues) {
		if(controllerEntityClass == null)
			throw new RuntimeException("controller entity class is required");
		return readMany(controllerEntityClass, new Arguments<ENTITY>().setControllerEntityClass(controllerEntityClass)
				.setRepresentationArguments(new org.cyk.utility.__kernel__.representation.Arguments()
				.setQueryExecutorArguments(new QueryExecutorArguments.Dto().setQueryIdentifier(queryIdentifier).addFilterFieldsValues(filterFieldsValues))));
	}
	
	<ENTITY> ENTITY readOne(Class<ENTITY> controllerEntityClass,Arguments<ENTITY> arguments);
	
	default <ENTITY> ENTITY readOne(Class<ENTITY> controllerEntityClass,String queryIdentifier,Object...filterFieldsValues) {
		if(controllerEntityClass == null)
			throw new RuntimeException("controller entity class is required");
		return readOne(controllerEntityClass, new Arguments<ENTITY>().setControllerEntityClass(controllerEntityClass)
				.setRepresentationArguments(new org.cyk.utility.__kernel__.representation.Arguments()
				.setQueryExecutorArguments(new QueryExecutorArguments.Dto().setQueryIdentifier(queryIdentifier).addFilterFieldsValues(filterFieldsValues))));
	}
	
	/**/
	
	public abstract static class AbstractImpl extends AbstractObject implements EntityReader,Serializable {
		
		@Override
		public <ENTITY> Collection<ENTITY> readMany(Class<ENTITY> controllerEntityClass,Arguments<ENTITY> arguments) {
			if(controllerEntityClass == null)
				throw new RuntimeException("controller entity class is required");
			if(arguments == null)
				throw new RuntimeException("arguments are required");
			if(arguments.getResponseEntityClass() == null)
				arguments.setResponseEntityClass(Collection.class);
			arguments.prepare(controllerEntityClass, org.cyk.utility.__kernel__.representation.EntityReader.class);
			Response response = ((org.cyk.utility.__kernel__.representation.EntityReader)arguments.__representation__).read(arguments.__representationArguments__);
			arguments.finalise(response);
			return (Collection<ENTITY>) arguments.__responseEntity__;			
		}
		
		@Override
		public <ENTITY> ENTITY readOne(Class<ENTITY> controllerEntityClass, Arguments<ENTITY> arguments) {
			if(controllerEntityClass == null)
				throw new RuntimeException("controller entity class is required");
			if(arguments == null)
				throw new RuntimeException("arguments are required");
			if(arguments.getResponseEntityClass() == null)
				arguments.setResponseEntityClass(controllerEntityClass);
			arguments.prepare(controllerEntityClass, org.cyk.utility.__kernel__.representation.EntityReader.class);
			arguments.__representationArguments__.getQueryExecutorArguments(Boolean.TRUE).setCollectionable(Boolean.FALSE);
			Response response = ((org.cyk.utility.__kernel__.representation.EntityReader)arguments.__representation__).read(arguments.__representationArguments__);
			arguments.finalise(response);
			return (ENTITY) arguments.__responseEntity__;	
		}
	}
	
	/**/
	
	static EntityReader getInstance() {
		return Helper.getInstance(EntityReader.class, INSTANCE);
	}
	
	Value INSTANCE = new Value();
}