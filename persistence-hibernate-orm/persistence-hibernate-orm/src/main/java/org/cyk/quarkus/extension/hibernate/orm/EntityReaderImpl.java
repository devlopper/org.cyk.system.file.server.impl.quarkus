package org.cyk.quarkus.extension.hibernate.orm;
import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cyk.utility.persistence.SpecificPersistence;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.cyk.utility.persistence.server.SpecificPersistenceGetter;

@ApplicationScoped @Qualifier
public class EntityReaderImpl extends org.cyk.utility.persistence.server.query.EntityReaderImpl implements Serializable {

	@Inject SpecificPersistenceGetter specificPersistenceGetter;
	
	@Override
	public <T> T readOne(Class<T> tupleClass, QueryExecutorArguments arguments) {
		SpecificPersistence<T> persistence = specificPersistenceGetter.get(tupleClass);
		if(persistence != null && Boolean.TRUE.equals(persistence.isProcessable(arguments)))
			return (T) persistence.readOne(arguments);
		return super.readOne(tupleClass, arguments);
	}
	
	@Override
	public <T> Collection<T> readMany(Class<T> resultClass, QueryExecutorArguments arguments) {
		SpecificPersistence<T> persistence = specificPersistenceGetter.get(resultClass);
		if(persistence != null && Boolean.TRUE.equals(persistence.isProcessable(arguments)))
			return (Collection<T>) persistence.readMany(arguments);
		return super.readMany(resultClass, arguments);
	}
}