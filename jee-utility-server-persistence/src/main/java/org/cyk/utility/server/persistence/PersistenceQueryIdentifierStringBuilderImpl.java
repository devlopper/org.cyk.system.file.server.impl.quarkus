package org.cyk.utility.server.persistence;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;

import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.function.AbstractFunctionWithPropertiesAsInputAndStringAsOutputImpl;

public class PersistenceQueryIdentifierStringBuilderImpl extends AbstractFunctionWithPropertiesAsInputAndStringAsOutputImpl implements PersistenceQueryIdentifierStringBuilder, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		getProperties().setFromPath(new Object[]{Properties.IS,Properties.FORMAT,Properties.REQUIRED}, Boolean.TRUE);
		setFormat("%s.%s");//classSimpleName.fieldName
	}
	
	@Override
	protected Collection<Object> __getFormatArguments__(Boolean isFormatRequired, Collection<Object> formatArguments) {
		if(__inject__(CollectionHelper.class).isEmpty(formatArguments)){
			formatArguments = __inject__(CollectionHelper.class).instanciate(getClassSimpleName(),getFieldName());
		}
		return super.__getFormatArguments__(isFormatRequired, formatArguments);
	}
	
	@Override
	protected String __execute__(String format, Collection<Object> formatArguments) throws Exception {
		// TODO Auto-generated method stub
		return super.__execute__(format, formatArguments);
	}
	
	@Override
	public PersistenceQueryIdentifierStringBuilder setClassSimpleName(String name) {
		getProperties().setFromPath(new Object[]{Properties.CLASS,Properties.NAME}, name);
		return this;
	}
	
	@Override
	public String getClassSimpleName() {
		return (String) getProperties().getFromPath(Properties.CLASS,Properties.NAME);
	}
	
	@Override
	public PersistenceQueryIdentifierStringBuilder setClassSimpleName(Class<?> aClass) {
		return setClassSimpleName(aClass.getSimpleName());
	}
	
	@Override
	public PersistenceQueryIdentifierStringBuilder setFieldName(String name) {
		getProperties().setFromPath(new Object[]{Properties.FIELD,Properties.NAME}, name);
		return this;
	}
	
	@Override
	public String getFieldName() {
		return (String) getProperties().getFromPath(Properties.FIELD,Properties.NAME);
	}
	
	@Override
	public PersistenceQueryIdentifierStringBuilder setFieldName(Field field) {
		return setFieldName(field.getName());
	}
}