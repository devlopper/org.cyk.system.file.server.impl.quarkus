package org.cyk.quarkus.extension.hibernate.orm;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.cyk.utility.persistence.SpecificPersistence;

public abstract class AbstractSpecificPersistenceImpl<ENTITY> extends org.cyk.utility.persistence.server.AbstractSpecificPersistenceImpl<ENTITY>  implements SpecificPersistence<ENTITY>,Serializable {

	@Inject EntityManager entityManager;
	
}