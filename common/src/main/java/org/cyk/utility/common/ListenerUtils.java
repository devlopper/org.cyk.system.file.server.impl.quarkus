package org.cyk.utility.common;

import java.math.BigDecimal;
import java.util.Collection;

public class ListenerUtils {

	private static final ListenerUtils INSTANCE = new ListenerUtils();
	public static ListenerUtils getInstance() {
		return INSTANCE;
	}
	
	public <RESULT,LISTENER> RESULT getValue(Class<RESULT> valueClass,Collection<LISTENER> listeners,ResultMethod<LISTENER,RESULT> method){
		RESULT result = null;
		for(LISTENER listener : listeners){
			RESULT value = method.execute(listener);
			if(value!=null)
				result = value;
		}
		return result;
	}
	
	public <LISTENER> String getString(Collection<LISTENER> listeners,ResultMethod<LISTENER,String> method){
		return getValue(String.class, listeners, method);
	}
	
	public <LISTENER> BigDecimal getBigDecimal(Collection<LISTENER> listeners,ResultMethod<LISTENER,BigDecimal> method){
		return getValue(BigDecimal.class, listeners, method);
	}
	
	public <LISTENER> Boolean getBoolean(Collection<LISTENER> listeners,ResultMethod<LISTENER,Boolean> method){
		return getValue(Boolean.class, listeners, method);
	}
	
	public <LISTENER> Long getLong(Collection<LISTENER> listeners,ResultMethod<LISTENER,Long> method){
		return getValue(Long.class, listeners, method);
	}
	
	public <LISTENER> Integer getInteger(Collection<LISTENER> listeners,ResultMethod<LISTENER,Integer> method){
		return getValue(Integer.class, listeners, method);
	}
	
	public <LISTENER> Double getDouble(Collection<LISTENER> listeners,ResultMethod<LISTENER,Double> method){
		return getValue(Double.class, listeners, method);
	}
	
	public <LISTENER,ELEMENT_TYPE> Collection<ELEMENT_TYPE> getCollection(Collection<LISTENER> listeners,ResultMethod<LISTENER,Collection<ELEMENT_TYPE>> method){
		Collection<ELEMENT_TYPE> collection = null;
		for(LISTENER listener : listeners){
			Collection<ELEMENT_TYPE> value = method.execute(listener);
			if(value!=null)
				collection = value;
		}
		return collection;
	}
	
	public <RESULT,LISTENER> void execute(Collection<LISTENER> listeners,VoidMethod<LISTENER> method){
		for(LISTENER listener : listeners)
			method.execute(listener);
	}
	
	/**/
	
	public static interface ResultMethod<LISTENER,RESULT>{
		RESULT execute(LISTENER listener);
	}
	
	public static interface StringMethod<LISTENER> extends ResultMethod<LISTENER,String>{}
	public static interface BigDecimalMethod<LISTENER> extends ResultMethod<LISTENER,BigDecimal>{}
	public static interface BooleanMethod<LISTENER> extends ResultMethod<LISTENER,Boolean>{}
	public static interface LongMethod<LISTENER> extends ResultMethod<LISTENER,Long>{}
	public static interface IntegerMethod<LISTENER> extends ResultMethod<LISTENER,Integer>{}
	public static interface DoubleMethod<LISTENER> extends ResultMethod<LISTENER,Double>{}
	public static interface CollectionMethod<LISTENER,ELEMENT_TYPE> extends ResultMethod<LISTENER,Collection<ELEMENT_TYPE>>{
		//Class<COLLECTION_RUNTIME_TYPE> getCollectionRuntimeType();
	}
	
	public static interface VoidMethod<LISTENER>{
		void execute(LISTENER listener);
	}
}