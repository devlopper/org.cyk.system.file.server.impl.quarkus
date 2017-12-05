package org.cyk.utility.common.userinterface;

import java.io.Serializable;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.common.cdi.AbstractBean;
import org.cyk.utility.common.helper.AbstractHelper;
import org.cyk.utility.common.helper.ClassHelper;
import org.cyk.utility.common.helper.StringHelper;

@Singleton
public class RequestHelper extends AbstractHelper implements Serializable {
	private static final long serialVersionUID = 1L;

	static {
		ClassHelper.getInstance().map(Listener.class, Listener.Adapter.Default.class,Boolean.FALSE);
	}
	
	private static RequestHelper INSTANCE;
	
	public static RequestHelper getInstance() {
		if(INSTANCE == null)
			INSTANCE = new RequestHelper();
		return INSTANCE;
	}
	
	@Override
	protected void initialisation() {
		INSTANCE = this;
		super.initialisation();
	}
	
	public Object get(){
		return ClassHelper.getInstance().instanciateOne(Listener.class).get();
	}
	
	public String getUniformResourceLocator(Object request){
		return ClassHelper.getInstance().instanciateOne(Listener.class).getUniformResourceLocator(request);
	}
	
	public String getUniformResourceLocator(){
		return ClassHelper.getInstance().instanciateOne(Listener.class).getUniformResourceLocator();
	}
	
	public Object getParameter(String name,Object request){
		return ClassHelper.getInstance().instanciateOne(Listener.class).getParameter(name,request);
	}
	
	public Object getParameter(String name){
		return ClassHelper.getInstance().instanciateOne(Listener.class).getParameter(name);
	}
	
	public String getParameterAsString(String name,Object request){
		return (String) getParameter(name,request);
	}
	
	public String getParameterAsString(String name){
		return getParameterAsString(name,get());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<?>> T getParameterAsEnum(Class<T> enumClass,String name,Object request){
		String string = getParameterAsString(name,request);
		if(StringHelper.getInstance().isNotBlank(string))
			for(Enum<?> enumValue : enumClass.getEnumConstants()){
				if(StringUtils.equalsIgnoreCase(enumValue.name(), string))
					return (T) enumValue;
			}
		return null;
	}
	
	public <T extends Enum<?>> T getParameterAsEnum(Class<T> enumClass,String name){
		return getParameterAsEnum(enumClass,name,get());
	}
	
	public Class<?> getParameterAsClass(String name,Object request){
		String string = getParameterAsString(name,request);
		if(StringHelper.getInstance().isNotBlank(string))
			return ClassHelper.getInstance().getClassByIdentifier(string);
		return null;
	}
	
	public Class<?> getParameterAsClass(String name){
		return getParameterAsClass(name,get());
	}
	
	/**/
	
	public static interface Listener {
		
		Object get();
		String getSession(Object request);
		String getSession();
		String getUniformResourceLocator(Object request);
		String getUniformResourceLocator();
		Object getParameter(String name,Object request);
		Object getParameter(String name);
	
		public static class Adapter extends AbstractBean implements Listener,Serializable {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Object get() {
				return null;
			}
			
			@Override
			public String getSession(Object request) {
				return null;
			}
			
			@Override
			public String getSession() {
				return null;
			}
			
			@Override
			public String getUniformResourceLocator(Object request) {
				return null;
			}
			
			@Override
			public String getUniformResourceLocator() {
				return null;
			}
			
			@Override
			public Object getParameter(String name,Object request) {
				return null;
			}
			
			@Override
			public Object getParameter(String name) {
				return null;
			}
			
			public static class Default extends Listener.Adapter implements Serializable {
				private static final long serialVersionUID = 1L;
				
				@Override
				public String getSession() {
					return getSession(get());
				}
				
				@Override
				public Object getParameter(String name) {
					return getParameter(name,get());
				}
				
				@Override
				public String getUniformResourceLocator() {
					return getUniformResourceLocator(get());
				}
				
			}
		}
	}
	
}