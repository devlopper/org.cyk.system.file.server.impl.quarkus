package org.cyk.system.file.server.impl.persistence;

import static org.cyk.utility.persistence.query.Language.parenthesis;
import static org.cyk.utility.persistence.query.Language.Where.or;

import java.util.Collection;

import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.cyk.utility.persistence.server.query.string.LikeStringBuilder;
import org.cyk.utility.persistence.server.query.string.LikeStringValueBuilder;
import org.cyk.utility.persistence.server.query.string.QueryStringBuilder.Arguments;
import org.cyk.utility.persistence.server.query.string.WhereStringBuilder;

public interface FileQueryStringBuilder {

	public static interface Projection {
		static void project(QueryExecutorArguments queryExecutorArguments, Arguments arguments) {
			
		}
	}
	
	public static interface Predicate {
		static void populate(QueryExecutorArguments queryExecutorArguments, Arguments arguments, WhereStringBuilder.Predicate predicate,Filter filter) {
			RuntimeQueryStringBuilderImpl.addEqualsIfFilterHasFieldWithPath(queryExecutorArguments, arguments, predicate, filter, Parameters.UNIFORM_RESOURCE_LOCATOR,"t",FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR);
			RuntimeQueryStringBuilderImpl.addEqualsIfFilterHasFieldWithPath(queryExecutorArguments, arguments, predicate, filter, Parameters.SHA1,"t",FileImpl.FIELD_SHA1);
			@SuppressWarnings("unchecked")
			Collection<String> sha1s = (Collection<String>) queryExecutorArguments.getFilterFieldValue(Parameters.SHA1S);
			if(CollectionHelper.isNotEmpty(sha1s)) {
				predicate.add(String.format("t.%s IN :%s", FileImpl.FIELD_SHA1,Parameters.SHA1));
				filter.addField(Parameters.SHA1, sha1s);
			}
			
			Boolean dupliated = queryExecutorArguments.getFilterFieldValueAsBoolean(null,Parameters.DUPLICATED);
			if(dupliated != null) {
				if(dupliated)
					predicate.add(String.format("sha1 IN (%s) AND identifier NOT IN (%s)", sha1CountIsGreaterThanOne(),hasDuplicate()));
				else
					predicate.add(String.format("sha1 NOT IN (%s)", sha1CountIsGreaterThanOne()));
			}
			
			if(queryExecutorArguments.getFilterField(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString()) != null) {
				predicate.add(search());
				String search = ValueHelper.defaultToIfBlank((String) queryExecutorArguments.getFilterFieldValue(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString()),"");
				filter.addField(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString(), LikeStringValueBuilder.getInstance().build(search, null, null));
			}
		}
		
		static String search() {
			return parenthesis(or(
					LikeStringBuilder.getInstance().build("t", FileImpl.FIELD_NAME,DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString())
			));
		}
		
		/**
		 * Query giving list of sha1 where count is greater than one
		 * @return
		 */
		static String sha1CountIsGreaterThanOne() {
			return "SELECT sha1 FROM FileImpl GROUP BY sha1 HAVING COUNT(sha1) > 1";
		}
		
		/**
		 * Query giving list of file having duplicate
		 * @return
		 */
		static String hasDuplicate() {
			return "SELECT MAX(identifier)"
			+ " FROM FileImpl"
			+ " WHERE sha1 IN ("+sha1CountIsGreaterThanOne()+")"
			+ "	GROUP BY sha1";
		}
	}
	
	public static interface Group {
		static void populate() {
			
		}
	}
	
	public static interface Order {
		static void populate(QueryExecutorArguments queryExecutorArguments, Arguments arguments) {
			if(arguments.getOrder() == null || CollectionHelper.isEmpty(arguments.getOrder(Boolean.TRUE).getStrings())) {
				if(queryExecutorArguments.getFilterField(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString()) != null) {
					arguments.getOrder(Boolean.TRUE).asc("t",FileImpl.FIELD_NAME);
				}
			}
		}
	}
	
	public static interface Having {
		
	}
	
}