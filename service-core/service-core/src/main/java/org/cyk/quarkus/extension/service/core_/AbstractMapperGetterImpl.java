package org.cyk.quarkus.extension.service.core_;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.mapping.Mapper;
import org.cyk.utility.service.server.MapperGetter;

public abstract class AbstractMapperGetterImpl extends MapperGetter.AbstractImpl implements Serializable{
	
	@Override
	protected <PERSISTENCE_ENTITY, SERVICE_ENTITY> Mapper<PERSISTENCE_ENTITY, SERVICE_ENTITY> __get__(Class<PERSISTENCE_ENTITY> persistenceEntityClass, Class<SERVICE_ENTITY> serviceEntityClass) {
		return readField(persistenceEntityClass, serviceEntityClass);
	}

	@SuppressWarnings("unchecked")
	protected <PERSISTENCE_ENTITY, SERVICE_ENTITY> Mapper<PERSISTENCE_ENTITY, SERVICE_ENTITY> readField(Class<PERSISTENCE_ENTITY> persistenceEntityClass, Class<SERVICE_ENTITY> serviceEntityClass) {
		Map<Class<?>,Field> map = FIELDS.get(persistenceEntityClass);
		if(map == null)
			FIELDS.put(persistenceEntityClass, map = new HashMap<>());
		Field field = map.get(serviceEntityClass);
		if(field == null) {
			String fieldName = StringUtils.substringBeforeLast(StringHelper.getVariableNameFrom(persistenceEntityClass.getSimpleName()),"Impl")+"DtoImplMapper";
			field = FieldHelper.getByName(getClass(), fieldName);
			if(field == null)
				throw new RuntimeException(String.format("Mapper field to map %s and %s need to be declare on %s", persistenceEntityClass,serviceEntityClass,getClass()));
			map.put(serviceEntityClass, field);
		}
		return (Mapper<PERSISTENCE_ENTITY, SERVICE_ENTITY>) FieldHelper.read(this, field);
	}
	
	private static final Map<Class<?>,Map<Class<?>,Field>> FIELDS = new HashMap<>();
}