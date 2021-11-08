package org.cyk.quarkus.extension.hibernate.orm;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.persistence.SpecificPersistence;
import org.cyk.utility.persistence.server.SpecificPersistenceGetter;

public abstract class AbstractSpecificPersistenceGetterImpl extends SpecificPersistenceGetter.AbstractImpl implements Serializable {

	@Override
	public <ENTITY> SpecificPersistence<ENTITY> __get__(Class<ENTITY> klass) {
		return readField(klass);
	}

	@SuppressWarnings("unchecked")
	protected <ENTITY> SpecificPersistence<ENTITY> readField(Class<ENTITY> klass) {
		Field field = FIELDS.get(klass);
		if(field == null) {
			String fieldName = StringUtils.substringBeforeLast(StringHelper.getVariableNameFrom(klass.getSimpleName()),"Impl")+"Persistence";
			field = FieldHelper.getByName(getClass(), fieldName);
			if(field == null)
				throw new RuntimeException(String.format("Persistence field to get %s need to be declare on %s", klass,getClass()));
			FIELDS.put(klass, field);
		}
		return (SpecificPersistence<ENTITY>) FieldHelper.read(this, field);
	}
	
	private static final Map<Class<?>,Field> FIELDS = new HashMap<>();
}