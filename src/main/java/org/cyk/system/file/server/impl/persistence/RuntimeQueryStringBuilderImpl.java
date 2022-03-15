package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.cyk.utility.persistence.server.query.string.QueryStringBuilder.Arguments;
import org.cyk.utility.persistence.server.query.string.RuntimeQueryStringBuilder;
import org.cyk.utility.persistence.server.query.string.WhereStringBuilder.Predicate;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @org.cyk.system.file.server.api.System @Unremovable
public class RuntimeQueryStringBuilderImpl extends RuntimeQueryStringBuilder.AbstractImpl implements Serializable{

	@Inject FilePersistence filePersistence;
	
	@Override
	protected void populatePredicate(QueryExecutorArguments arguments, Arguments builderArguments, Predicate predicate,Filter filter) {
		super.populatePredicate(arguments, builderArguments, predicate, filter);
		if(Boolean.TRUE.equals(filePersistence.isProcessable(arguments)))
			FileQueryStringBuilder.Predicate.populate(arguments, builderArguments, predicate, filter);
	}
	
	@Override
	protected void setOrder(QueryExecutorArguments arguments, Arguments builderArguments) {
		super.setOrder(arguments, builderArguments);
		FileQueryStringBuilder.Order.populate(arguments, builderArguments);
	}
}