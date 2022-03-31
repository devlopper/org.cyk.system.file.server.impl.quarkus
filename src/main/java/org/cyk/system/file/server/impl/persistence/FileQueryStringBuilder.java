package org.cyk.system.file.server.impl.persistence;

import static org.cyk.utility.persistence.query.Language.parenthesis;
import static org.cyk.utility.persistence.query.Language.Where.or;

import java.util.ArrayList;
import java.util.Collection;

import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.persistence.query.Language;
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
		static String __IS_NOT_DUPLICATE_CHECKER_FIELD__ = FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR;
		static String __IS_NOT_DUPLICATE_CHECKER_FUNCTION__ = "MIN";
		
		static void populate(QueryExecutorArguments queryExecutorArguments, Arguments arguments, WhereStringBuilder.Predicate predicate,Filter filter) {
			RuntimeQueryStringBuilderImpl.addEqualsIfFilterHasFieldWithPath(queryExecutorArguments, arguments, predicate, filter, Parameters.UNIFORM_RESOURCE_LOCATOR,"t",FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR);
			RuntimeQueryStringBuilderImpl.addEqualsIfFilterHasFieldWithPath(queryExecutorArguments, arguments, predicate, filter, Parameters.SHA1,"t",FileImpl.FIELD_SHA1);
			@SuppressWarnings("unchecked")
			Collection<String> sha1s = (Collection<String>) queryExecutorArguments.getFilterFieldValue(Parameters.SHA1S);
			if(CollectionHelper.isNotEmpty(sha1s)) {
				predicate.add(String.format("t.%s IN :%s", FileImpl.FIELD_SHA1,Parameters.SHA1));
				filter.addField(Parameters.SHA1, sha1s);
			}
			
			Boolean isADuplicate = queryExecutorArguments.getFilterFieldValueAsBoolean(null,Parameters.IS_A_DUPLICATE);
			if(isADuplicate != null) {
				if(isADuplicate)
					predicate.add(String.format("sha1 IN (%s) AND %s NOT IN (%s)", SHA1_COUNT_IS_GREATER_THAN_ONE,__IS_NOT_DUPLICATE_CHECKER_FIELD__,HAS_DUPLICATE));
				else
					predicate.add(String.format("sha1 NOT IN (%s)", SHA1_COUNT_IS_GREATER_THAN_ONE));
			}
			
			Boolean isNameNormalized = queryExecutorArguments.getFilterFieldValueAsBoolean(null,Parameters.IS_NAME_NORMALIZED);
			if(isNameNormalized != null)
				predicate.add(nameIsNormalized(isNameNormalized));
			
			if(queryExecutorArguments.getFilterField(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString()) != null) {
				predicate.add(search());
				String search = ValueHelper.defaultToIfBlank((String) queryExecutorArguments.getFilterFieldValue(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString()),"");
				filter.addField(DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString(), LikeStringValueBuilder.getInstance().build(search, null, null));
			}
		}
		
		static String nameIsNormalized(Boolean yes) {
			Configuration configuration = DependencyInjection.inject(Configuration.class);
			Collection<String> strings = new ArrayList<>();
			configuration.file().name().character().forbidden().forEach(character -> {
				strings.add(String.format("name LIKE %s", "%"+character+"%"));
			});
			String string = StringHelper.concatenate(strings," OR ");
			return Boolean.TRUE.equals(yes) ? Language.Where.not(string) : string;
		}
		
		static String nameIsNormalized() {
			return nameIsNormalized(Boolean.TRUE);
		}
		
		static String search() {
			return parenthesis(or(
					LikeStringBuilder.getInstance().build("t", FileImpl.FIELD_NAME,DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString())
					,String.format("EXISTS(SELECT ft.identifier FROM FileTextImpl ft WHERE ft.identifier = t.identifier AND (%s))",LikeStringBuilder.getInstance().build("ft", FileTextImpl.FIELD_TEXT,DependencyInjection.inject(FilePersistence.class).getParameterNameFilterAsString()))
			));
		}
		
		/**
		 * Query giving list of sha1 where count is greater than one
		 * @return
		 */
		static String SHA1_COUNT_IS_GREATER_THAN_ONE = "SELECT sha1 FROM FileImpl GROUP BY sha1 HAVING COUNT(sha1) > 1";
		
		/**
		 * Query giving list of file having duplicate
		 * @return
		 */
		static String HAS_DUPLICATE = String.format("SELECT %s(%s) FROM FileImpl WHERE sha1 IN (%s) GROUP BY sha1",__IS_NOT_DUPLICATE_CHECKER_FUNCTION__,__IS_NOT_DUPLICATE_CHECKER_FIELD__,SHA1_COUNT_IS_GREATER_THAN_ONE);
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