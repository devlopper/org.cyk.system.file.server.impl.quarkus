package org.cyk.quarkus.extension.hibernate.orm;
import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.utility.persistence.SpecificPersistence;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.cyk.utility.persistence.server.SpecificPersistenceGetter;

@ApplicationScoped @Qualifier
public class EntityCounterImpl extends org.cyk.utility.persistence.server.query.EntityCounterImpl implements Serializable {

	@Inject SpecificPersistenceGetter specificPersistenceGetter;
	
	@Override
	public Long count(Class<?> tupleClass, QueryExecutorArguments arguments) {
		SpecificPersistence<?> persistence = specificPersistenceGetter.get(tupleClass);
		if(persistence != null && Boolean.TRUE.equals(persistence.isProcessable(arguments)))
			return persistence.count(arguments);
		return super.count(tupleClass, arguments);
	}
}