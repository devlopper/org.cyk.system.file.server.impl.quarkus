package org.cyk.utility.persistence;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManagerFactory;

@ApplicationScoped
public class EntityManagerFactoryGetterImpl extends EntityManagerFactoryGetter.AbstractImpl implements Serializable {

	public static EntityManagerFactory ENTITY_MANAGER_FACTORY; 
	
	@Override
	public EntityManagerFactory get() {
		return ENTITY_MANAGER_FACTORY;
	}

}
