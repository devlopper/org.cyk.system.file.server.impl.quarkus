package org.cyk.utility.persistence.server.query;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cyk.utility.__kernel__.Helper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.computation.SortOrder;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.throwable.ThrowableHelper;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.persistence.query.Query;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.cyk.utility.persistence.query.QueryIdentifierGetter;
import org.cyk.utility.persistence.query.QueryName;
import org.cyk.utility.persistence.query.QueryType;
import org.cyk.utility.persistence.server.query.string.RuntimeQueryStringBuilder;

public interface RuntimeQueryBuilder {
	
	Query build(QueryExecutorArguments arguments);	
	
	/**/
	
	public abstract class AbstractImpl extends AbstractObject implements RuntimeQueryBuilder,Serializable {
		private static final long serialVersionUID = 1L;		
		
		@Override
		public Query build(QueryExecutorArguments arguments) {
			ThrowableHelper.throwIllegalArgumentExceptionIfNull("arguments", arguments);
			ThrowableHelper.throwIllegalArgumentExceptionIfNull("query", arguments.getQuery());
			if(!Boolean.TRUE.equals(isBuildable(arguments)))
				return arguments.getQuery();
			return __build__(arguments);
		}
		
		protected Query __build__(QueryExecutorArguments arguments) {
			Query query = instantiateQuery(arguments);
			query.setIdentifier(null);//Disable named query mapping			
			arguments.setRuntimeQuery(query);
			return query;
		}
		
		protected Query instantiateQuery(QueryExecutorArguments arguments) {
			//Filter might change. Hence we must clone the filter to keep track of the original one
			arguments.setFilterBackup(Filter.clone(arguments.getFilter()));
			processQueryExecutorArguments(arguments);
			
			Query query = new Query();
			query.setIdentifier(arguments.getQuery().getIdentifier());
			query.setIntermediateResultClass(arguments.getQuery().getIntermediateResultClass());
			query.setQueryDerivedFromQuery(arguments.getQuery().getQueryDerivedFromQuery());
			query.setResultClass(arguments.getQuery().getResultClass());
			query.setTupleClass(arguments.getQuery().getTupleClass());
			query.setTupleFieldsNamesIndexes(arguments.getQuery().getTupleFieldsNamesIndexes());
			query.setType(arguments.getQuery().getType());
			query.setValue(RuntimeQueryStringBuilder.getInstance().build(arguments));
			setTupleFieldsNamesIndexesFromFieldsNames(arguments, query);
			
			setSortOrders(arguments);
			/*
			if(QueryType.COUNT.equals(arguments.getQuery().getType()) || QueryType.READ_ONE.equals(arguments.getQuery().getType())) {
				arguments.setSortOrders(null);
			}
			*/
			return query;
		}
		
		protected void processQueryExecutorArguments(QueryExecutorArguments arguments) {
			
		}
		
		protected void setTupleFieldsNamesIndexesFromFieldsNames(QueryExecutorArguments arguments,Query query) {
			if(QueryType.READ_MANY.equals(query.getType()) || QueryType.READ_ONE.equals(query.getType())) {
				if(CollectionHelper.isEmpty(arguments.getProjections()))
					;
				else {
					query.setIntermediateResultClass(Object[].class);
					Collection<String> resultsFieldsNames = arguments.getResultsFieldsNames();
					if(CollectionHelper.isEmpty(resultsFieldsNames))
						resultsFieldsNames = arguments.getProjections().stream().map(x -> x.getFieldName()).collect(Collectors.toList());
					query.setTupleFieldsNamesIndexesFromFieldsNames(resultsFieldsNames);
				}
			}
		}
		
		protected Boolean isBuildable(QueryExecutorArguments arguments) {
			if(StringHelper.isBlank(arguments.getQuery().getIdentifier()))
				return Boolean.FALSE;
			if(BUILDABLES.contains(arguments.getQuery().getIdentifier()))
				return Boolean.TRUE;			
			if(arguments.getQuery().getIdentifier().equals(QueryIdentifierGetter.getInstance().get(arguments.getQuery().getTupleClass(), QueryName.READ_DYNAMIC)))
				return Boolean.TRUE;
			if(arguments.getQuery().getIdentifier().equals(QueryIdentifierGetter.getInstance().get(arguments.getQuery().getTupleClass(), QueryName.READ_DYNAMIC_ONE)))
				return Boolean.TRUE;
			if(arguments.getQuery().getIdentifier().equals(QueryIdentifierGetter.getInstance().get(arguments.getQuery().getTupleClass(), QueryName.COUNT_DYNAMIC)))
				return Boolean.TRUE;
			return Boolean.FALSE;
		}
		
		protected void setSortOrders(QueryExecutorArguments arguments) {
			if(QueryType.COUNT.equals(arguments.getQuery().getType()) || QueryType.READ_ONE.equals(arguments.getQuery().getType())) {
				arguments.setSortOrders(null);
				return;
			}
			Map<String,SortOrder> sortOrders = arguments.getSortOrders();
			if(MapHelper.isEmpty(sortOrders))
				return;
			sortOrders = computeSortOrders(arguments,sortOrders);
			if(MapHelper.isEmpty(sortOrders))
				return;
			arguments.setSortOrders(sortOrders);
		}
		
		protected Map<String,SortOrder> computeSortOrders(QueryExecutorArguments arguments,Map<String,SortOrder> sortOrders) {
			Map<String,SortOrder> map = new LinkedHashMap<>();
			sortOrders.forEach((fieldName,sortOrder) -> {
				Collection<String> fieldsNames = computeSortOrdersFieldsNames(arguments,fieldName);
				if(CollectionHelper.isEmpty(fieldsNames))
					fieldsNames = List.of(fieldName);
				fieldsNames.forEach(x -> {
					map.put(x, sortOrder);
				});
			});			
			return map;
		}
		
		protected Collection<String> computeSortOrdersFieldsNames(QueryExecutorArguments arguments,String fieldName) {
			return List.of(fieldName);
		}
		
		protected static Boolean hasFilterField(QueryExecutorArguments arguments,String fieldName) {
			return arguments == null ? Boolean.FALSE : arguments.getFilterField(fieldName) != null; 			
		}
	}
	
	/**/
	
	/**/
	
	static RuntimeQueryBuilder getInstance() {
		return Helper.getInstance(RuntimeQueryBuilder.class, INSTANCE);
	}
	
	Value INSTANCE = new Value();	
	
	Collection<String> BUILDABLES = new HashSet<>();
}