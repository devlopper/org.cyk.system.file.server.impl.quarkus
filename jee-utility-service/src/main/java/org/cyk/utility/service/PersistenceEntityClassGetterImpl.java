package org.cyk.utility.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersistenceEntityClassGetterImpl extends PersistenceEntityClassGetter.AbstractImpl implements Serializable {

	public static final Map<Class<?>,Class<?>> MAP = new HashMap<>();
	
	@Override
	public Class<?> get(Class<?> controllerEntityClass) {
		if(MAP.containsKey(controllerEntityClass))
			return MAP.get(controllerEntityClass);
		return super.get(controllerEntityClass);
	}
	
}
