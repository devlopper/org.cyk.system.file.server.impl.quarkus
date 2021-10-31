package org.cyk.utility.primefaces.collection;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.rest.ResponseHelper;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public abstract class LazyDataModel<ENTITY> extends org.primefaces.model.LazyDataModel<ENTITY> implements Serializable {

	private Map<String,ENTITY> map;
	private Class<ENTITY> entityClass;
	private Listener<ENTITY> listener;
	
	private Boolean readerUsable;
	private Boolean isCountEqualsListSize;
	
	//private Boolean loggableAsInfo = ValueConverter.getInstance().convertToBoolean(WebController.getInstance().getRequestParameter(ParameterName.LOGGABLE_AS_INFO));
	
	private int __first__,__pageSize__;
	private List<ENTITY> __list__;
	private Integer __count__;
	private String __sortField__,__entityFieldsNamesAsString__;
	private SortOrder __sortOrder__;
	private List<SortMeta> __multiSortMeta__;
	private Map<String, Object> __filters__;
	private Response __response__;
	private Listener<ENTITY> __listener__;
	
	public LazyDataModel(Class<ENTITY> entityClass) {
		this.entityClass = entityClass;
	}
	
	protected abstract Response serve(Map<String, Object> filters,LinkedHashMap<String,SortOrder> sortOrders,int first, int pageSize);
	
	protected void load(Map<String, Object> filters,LinkedHashMap<String,SortOrder> sortOrders,int first, int pageSize) {
		__response__ = serve(filters, null, first, pageSize);
		__list__ = getList(__response__);
		__count__ = NumberHelper.getInteger(ResponseHelper.getHeaderXTotalCount(__response__),0);
		setRowCount(__count__);
	}
	
	protected abstract List<ENTITY> getList(Response response);
	/*
	@SuppressWarnings("unchecked")
	protected List<ENTITY> __load__(int first, int pageSize,Map<String, Object> filters) {
		long timestamp = System.currentTimeMillis();
		list = null;
		__first__ = first;
		__pageSize__ = pageSize;
		__filters__ = filters;
		__listener__ = listener;
		if(__listener__ == null) {
			__listener__ = listener;
			if(__listener__ == null)
				__listener__ = (Listener<ENTITY>) Listener.AbstractImpl.DefaultImpl.INSTANCE;
		}
		list = __listener__.read(this);
		__response__ = __listener__.getResponse(this);
		if(CollectionHelper.isEmpty(list))
			__count__ = 0;
		else {
			if(Boolean.TRUE.equals(isCountEqualsListSize))
				__count__ = list.size();
			else
				__count__ = __listener__.getCount(this);
		}
		setRowCount(__count__);
		long duration = System.currentTimeMillis() - timestamp;
		if(Boolean.TRUE.equals(LOGGABLE)) {
			LogHelper.log(String.format("Page(%s,%s) , duration=%s", first,pageSize,duration), LOG_LEVEL,getClass());
		}
		map = null;
		return list;
	}
	*/
	@Override
	public List<ENTITY> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		long timestamp = System.currentTimeMillis();
		__multiSortMeta__ = multiSortMeta;
		load(filters, null, first, pageSize);
		long duration = System.currentTimeMillis() - timestamp;
		if(Boolean.TRUE.equals(LOGGABLE))
			LogHelper.log(String.format("Page(%s,%s) , duration=%s", first,pageSize,duration), LOG_LEVEL,getClass());
		map = null;
		return __list__;
	}
	
	@Override
	public List<ENTITY> load(int first, int pageSize, String sortField, SortOrder sortOrder,Map<String, Object> filters) {
		long timestamp = System.currentTimeMillis();
		__sortField__ = sortField;
		__sortOrder__ = sortOrder;		
		load(filters, null, first, pageSize);		
		long duration = System.currentTimeMillis() - timestamp;
		if(Boolean.TRUE.equals(LOGGABLE))
			LogHelper.log(String.format("Page(%s,%s) , duration=%s", first,pageSize,duration), LOG_LEVEL,getClass());
		map = null;
		return __list__;
	}
	
	/**/
	
	@Override
	public Object getRowKey(ENTITY entity) {
		if(entity == null)
			return null;
		Object identifier = FieldHelper.readSystemIdentifier(entity);
		if(identifier == null)
			return null;
		if(map == null)
			map = new HashMap<>();
		if(!map.containsKey(identifier.toString()))
			map.put(identifier.toString(), entity);
		return identifier;
	}
	
	@Override
	public ENTITY getRowData(String identifier) {
		if(map == null)
			return null;
		return map.get(identifier);
	}
	
	/**/
	
	public static interface Listener<T> {
		//Response load(LazyDataModel<T> lazyDataModel);
		
		public static abstract class AbstractImpl<T> extends AbstractObject implements Listener<T>,Serializable{	
			/*
			@Override
			public Integer getCount(LazyDataModel<T> lazyDataModel) {
				if(lazyDataModel.__response__ == null)
					return 0;
				return NumberHelper.getInteger(ResponseHelper.getHeaderXTotalCount(lazyDataModel.__response__),0);
			}
			*/
			
			/**/
			
			public static class DefaultImpl extends AbstractImpl<Object> implements Serializable {
				public static final Listener<Object> INSTANCE = new DefaultImpl();
			}
		}
	}
	
	public static Boolean LOGGABLE = Boolean.TRUE;
	public static Level LOG_LEVEL = Level.FINE;
}