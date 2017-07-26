package org.cyk.utility.common.helper;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.lang3.ClassUtils;
import org.cyk.utility.common.Action;
import org.cyk.utility.common.annotation.FieldOverride;
import org.reflections.Reflections;

import lombok.Getter;
import lombok.Setter;

@Singleton
public class ClassHelper extends AbstractReflectionHelper<Class<?>> implements Serializable {

	private static final long serialVersionUID = 1L;

	private static ClassHelper INSTANCE;
	
	public static ClassHelper getInstance() {
		if(INSTANCE == null)
			INSTANCE = new ClassHelper();
		return INSTANCE;
	}
	
	@Override
	protected void initialisation() {
		INSTANCE = this;
		super.initialisation();
	}
	
	public Class<?> getWrapper(Class<?> aClass){
		return ClassUtils.primitiveToWrapper(aClass);
	}
	
	public Boolean isNumber(Class<?> aClass){
		return isInstanceOf(Number.class, getWrapper(aClass));
	}
	
	public Boolean isString(Class<?> aClass){
		return isEqual(java.lang.String.class, aClass);
	}
	
	public Boolean isDate(Class<?> aClass){
		return isEqual(java.util.Date.class, aClass);
	}
	
	public Boolean isBoolean(Class<?> aClass){
		return isEqual(Boolean.class, getWrapper(aClass));
	}
	
	public Class<?> get(Class<?> aClass, String fieldName,Class<?> fieldType) {
		FieldOverride fieldOverride = inject(FieldHelper.class).getOverride(aClass,fieldName);
		Class<?> clazz;
		if(fieldOverride==null)
			clazz = fieldType;
		else
			clazz = fieldOverride.type();
		return clazz;
	}
	
	public Class<?> get(Class<?> aClass, String fieldName) {
		Field field = inject(FieldHelper.class).get(aClass, fieldName);
		return get(aClass, field);
	}
	
	public Class<?> get(Class<?> aClass, Field field) {
		return get(aClass, field.getName(), field.getType());
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Class<?>> get(String packageName,Class<?> baseClass){
		Reflections reflections = new Reflections(packageName);
		@SuppressWarnings("rawtypes")
		Collection classes = reflections.getSubTypesOf(baseClass);
		logTrace("sub types of {} in package {} are : {}", baseClass,packageName,classes);
	    return classes;
	}
	
	@SuppressWarnings("unchecked")
	public <TYPE> Class<TYPE> getParameterAt(Class<?> aClass,Integer index,Class<TYPE> typeClass){
		return (Class<TYPE>) ((ParameterizedType) aClass.getGenericSuperclass()).getActualTypeArguments()[index];
	}
	
	public Class<?> getByName(Class<?> aClass){
		return getByName(aClass.getName());
	}
	
	public Class<?> getByName(String name){
		try {
			return Class.forName(name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public <T> T instanciate(Class<T> aClass,Object[] constructorParameters){
		Class<?>[] classes = new Class[constructorParameters.length / 2];
		Object[] arguments = new Object[constructorParameters.length / 2];
		int j = 0;
		for(int i = 0 ; i < constructorParameters.length ; i = i + 2){
			classes[j] = (Class<?>) constructorParameters[i];
			arguments[j++] = constructorParameters[i+1];
		}
		try {
			return aClass.getConstructor(classes).newInstance(arguments);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public <T> T instanciateOne(Class<T> aClass){
		try {
			return aClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Collection<?> instanciateMany(@SuppressWarnings("rawtypes") Collection classes){
		Collection<Object> collection = new ArrayList<>();
		if(classes!=null)
			for(Object aClass : classes)
				collection.add(instanciateOne((Class<?>) aClass));
		return collection;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Collection<T> instanciateMany(Class<T> aClass,@SuppressWarnings("rawtypes") Collection classes){
		return (Collection<T>) instanciateMany(classes);
	}
	
	public Boolean isInstanceOf(Class<?> parentClass,Class<?> childClass){
		if(parentClass==null || childClass==null)
			return Boolean.FALSE;
		return parentClass.isAssignableFrom(childClass);
	}
	
	public Boolean isEqual(Class<?> aClass1,Class<?> aClass2){
		if(aClass1==null || aClass2==null)
			return Boolean.FALSE;
		return aClass1.equals(aClass2);
	}
	
	/**/
	
	public static interface Get extends AbstractReflectionHelper.Get<Package, Class<?>> {
		
		Class<?> getBaseClass();
		Get setBaseClass(Class<?> aClass);
		
		@Getter @Setter 
		public static class Adapter extends AbstractReflectionHelper.Get.Adapter.Default<Package, Class<?>> implements Get,Serializable {
			private static final long serialVersionUID = 1L;

			protected Class<?> baseClass;
			
			@SuppressWarnings("unchecked")
			public Adapter(Package input) {
				super(input);
				setInputClass((Class<Package>) ClassHelper.getInstance().getByName(Class.class.getName())); 
				setOutputClass((Class<Collection<Class<?>>>) ClassHelper.getInstance().getByName(Class.class.getName())); 
			}
			
			public Get setBaseClass(Class<?> baseClass){
				this.baseClass = baseClass;
				return this;
			}
			
			/**/
			
			public static class Default extends Get.Adapter implements Serializable {
				private static final long serialVersionUID = 1L;

				public Default(Package input) {
					super(input);
				}
				
				@Override
				public Integer getModifiers(Class<?> clazz) {
					return clazz.getModifiers();
				}
				
				@Override
				public String getName(Class<?> clazz) {
					return clazz.getName();
				}
				
				@Override
				protected Package getParent(Package aPackage) {
					return null;
				}
				
				@Override
				protected Collection<Class<?>> getTypes(Package aPackage) {
					return ClassHelper.getInstance().get(aPackage.getName(), getBaseClass());
				}
				
				@Override
				public Set<Class<?>> getAnnotationClasses(Class<?> aClass) {
					Set<Class<?>> classes = new HashSet<>();
					for(Annotation annotation : aClass.getAnnotations()){
						classes.add(annotation.annotationType());
					}
					return classes;
				}
				
			}
		}
		
	}

	public static interface Instanciation<INSTANCE> extends org.cyk.utility.common.Builder<INSTANCE,INSTANCE> {
		
		ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>> getGetExecutor();
		Instanciation<INSTANCE> setGetExecutor(ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>> getExecutor);
		
		@Getter
		public static class Adapter<INSTANCE> extends org.cyk.utility.common.Builder.Adapter.Default<INSTANCE,INSTANCE> implements Instanciation<INSTANCE>,Serializable {
			private static final long serialVersionUID = 1L;
			
			protected ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>> getExecutor;
			
			@SuppressWarnings("unchecked")
			public Adapter(Class<INSTANCE> outputClass) {
				super(outputClass, (INSTANCE) outputClass, outputClass);
			}
			
			@Override
			public Instanciation<INSTANCE> setGetExecutor(ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>> getExecutor) {
				return null;
			}
			
			public static class Default<INSTANCE> extends Instanciation.Adapter<INSTANCE> implements Serializable {
				private static final long serialVersionUID = 1L;
				
				public Default(Class<INSTANCE> outputClass) {
					super(outputClass);
				}
				
				@Override
				public Instanciation<INSTANCE> setGetExecutor(ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>> getExecutor) {
					this.getExecutor = getExecutor;
					return this;
				}
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override
				protected INSTANCE __execute__() {
					ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>> getExecutor = getGetExecutor();
					if(getExecutor==null){
						getExecutor = new ListenerHelper.Executor.Function.Adapter.Default.Object<Get<?>>();
						getExecutor.setResultMethod(ClassHelper.getInstance().instanciateOne(Get.Adapter.Default.RESULT_METHOD_CLASS));
						getExecutor.setInput((Collection) ClassHelper.getInstance().instanciateMany(Get.class
								,CollectionHelper.getInstance().isEmpty(Get.CLASSES) ? Arrays.asList(Get.Adapter.Default.class) : Get.CLASSES));
					}
					getExecutor.getResultMethod().setInputClass((Class<Object>) getOutputClass());
					getExecutor.getResultMethod().setInput(getOutputClass());
					getExecutor.getResultMethod().setOutputClass((Class<Object>) getOutputClass());
					INSTANCE instance = (INSTANCE) getExecutor.execute();
					return instance;
				}
			}
		}
		
		/**/
		
		public static interface Get<INSTANCE> extends Action<INSTANCE, INSTANCE> {
			
			Collection<Class<? extends Get<?>>> CLASSES = new ArrayList<>();
			
			public static class Adapter<INSTANCE> extends Action.Adapter.Default<INSTANCE, INSTANCE> implements Get<INSTANCE>,Serializable {
				private static final long serialVersionUID = 1L;
				
				@SuppressWarnings("unchecked")
				public Adapter(Class<INSTANCE> outputClass) {
					super("get", outputClass, (INSTANCE) outputClass, outputClass);
				}
				
				public static class Default<INSTANCE> extends Get.Adapter<INSTANCE> implements Serializable {
					private static final long serialVersionUID = 1L;
					
					@SuppressWarnings("unchecked")
					public static Class<ListenerHelper.Executor.ResultMethod<Object, Get<?>>> RESULT_METHOD_CLASS = (Class<org.cyk.utility.common.helper.ListenerHelper.Executor.ResultMethod<Object, Get<?>>>) ClassHelper.getInstance().getByName(ResultMethod.class);
					
					public Default(Class<INSTANCE> outputClass) {
						super(outputClass);
					}
					
					public Default() {
						this(null);
					}
				}
			}
			
			public static class ResultMethod extends ListenerHelper.Executor.ResultMethod.Adapter.Default.Object<Get<?>> {
				private static final long serialVersionUID = 1L;

				@Override
				protected java.lang.Object __execute__() {
					return ClassHelper.getInstance().instanciateOne((Class<?>)getInput());
				}
			}
		}
	}
	
}