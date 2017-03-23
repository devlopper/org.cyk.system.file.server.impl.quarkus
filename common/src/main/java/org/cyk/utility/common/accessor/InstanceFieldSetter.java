package org.cyk.utility.common.accessor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.common.Action;

import lombok.Getter;

public interface InstanceFieldSetter<INPUT, OUTPUT> extends Action<INPUT, OUTPUT> {

	OUTPUT getInstance();
	InstanceFieldSetter<INPUT, OUTPUT> setInstance(OUTPUT instance);
	
	Set<String> getFieldNames();
	InstanceFieldSetter<INPUT, OUTPUT> setFieldNames(Set<String> fieldNames);
	
	/**/
	
	@Getter
	public static class Adapter<INPUT, OUTPUT> extends Action.Adapter.Default<INPUT, OUTPUT> implements InstanceFieldSetter<INPUT, OUTPUT>,Serializable {
		private static final long serialVersionUID = 3887225153998606760L;

		protected OUTPUT instance;
		protected Set<String> fieldNames = new LinkedHashSet<>();
		
		public Adapter(Class<INPUT> inputClass, INPUT input, Class<OUTPUT> outputClass) {
			super("Set", inputClass, input, outputClass, null);
		}
		
		@Override
		public InstanceFieldSetter<INPUT, OUTPUT> setInstance(OUTPUT instance) {
			this.instance = instance;
			return this;
		}
		
		@Override
		public InstanceFieldSetter<INPUT, OUTPUT> setFieldNames(Set<String> fieldNames) {
			this.fieldNames = fieldNames;
			return this;
		}
		
		/**/
		
		public static class Default<INPUT, OUTPUT> extends InstanceFieldSetter.Adapter<INPUT, OUTPUT> implements Serializable {
			private static final long serialVersionUID = 3887225153998606760L;

			public Default(Class<INPUT> inputClass, INPUT input, Class<OUTPUT> outputClass) {
				super(inputClass, input, outputClass);
			}
			
		}
		
	}
	
	/**/
	
	public static interface OneDimensionObjectArray<OUTPUT> extends InstanceFieldSetter<Object[], OUTPUT> {
		
		Map<String,Integer> getFieldNamesIndexes();
		OneDimensionObjectArray<OUTPUT> setFieldNamesIndexes(Map<String,Integer> fieldNamesIndexes);
		
		Boolean isArrayValueAtProcessable(Integer index);
		
		Object getValue(Class<?> fieldType,Object value);
		
		OneDimensionObjectArray<OUTPUT> addFieldName(String name,Integer index);
		
		/**/
		
		@Getter
		public static class Adapter<OUTPUT> extends InstanceFieldSetter.Adapter.Default<Object[], OUTPUT> implements OneDimensionObjectArray<OUTPUT>,Serializable {
			private static final long serialVersionUID = 3887225153998606760L;

			protected Map<String,Integer> fieldNamesIndexes = new HashMap<>();
			
			public Adapter(Object[] input, Class<OUTPUT> outputClass) {
				super(Object[].class, input, outputClass);
			}
			
			@Override
			public OneDimensionObjectArray<OUTPUT> setFieldNamesIndexes(Map<String, Integer> fieldNamesIndexes) {
				this.fieldNamesIndexes = fieldNamesIndexes;
				return this;
			}
			
			@Override
			public Boolean isArrayValueAtProcessable(Integer index) {
				return null;
			}
			
			@Override
			public Object getValue(Class<?> fieldType,Object value) {
				return null;
			}
			
			@Override
			public OneDimensionObjectArray<OUTPUT> addFieldName(String name, Integer index) {
				return null;
			}
			
			/**/
			
			public static class Default<OUTPUT> extends OneDimensionObjectArray.Adapter<OUTPUT> implements Serializable {
				private static final long serialVersionUID = 3887225153998606760L;

				public Default(Object[] input, Class<OUTPUT> outputClass) {
					super(input, outputClass);
				}
				
				@Override
				public Boolean isArrayValueAtProcessable(Integer index) {
					return getInput()[index] != null && StringUtils.isNotBlank(getInput()[index].toString());
				}
				
				@Override
				public Object getValue(Class<?> fieldType,Object value) {
					return commonUtils.convertString(value.toString(), fieldType);
				}
				
				@Override
				public OneDimensionObjectArray<OUTPUT> addFieldName(String name, Integer index) {
					getFieldNames().add(name);
					getFieldNamesIndexes().put(name, index);
					return this;
				}
				
				@Override
				protected OUTPUT __execute__() {
					addLogMessageBuilderParameters(getLogMessageBuilder(),"length",getInput().length);
					for(String fieldName : getFieldNames()){
						try{
							Class<?> fieldType = PropertyUtils.getPropertyType(getInstance(), fieldName);
							Integer index = getFieldNamesIndexes().get(fieldName);
							Boolean isArrayValueAtProcessable = isArrayValueAtProcessable(index);
							Object value = getInput()[getFieldNamesIndexes().get(fieldName)];
							addLogMessageBuilderParameters(getLogMessageBuilder(), "field name",fieldName);
							addLogMessageBuilderParameters(getLogMessageBuilder(),"index",index);
							addLogMessageBuilderParameters(getLogMessageBuilder(),"value",value);
							addLogMessageBuilderParameters(getLogMessageBuilder(),"processable",isArrayValueAtProcessable);
							if(Boolean.TRUE.equals(isArrayValueAtProcessable)){
								value = getValue(fieldType, value);
								addLogMessageBuilderParameters(getLogMessageBuilder(),"field value",value);
								PropertyUtils.setProperty(getInstance(), fieldName, value);
							}
						}catch(Exception exception){
							logThrowable(exception);
						}
					}			
					return getInstance();
				}
			}
						
		}
		
	}
	
}
