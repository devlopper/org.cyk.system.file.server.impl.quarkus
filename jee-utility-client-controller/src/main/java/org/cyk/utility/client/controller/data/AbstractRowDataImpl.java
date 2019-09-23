package org.cyk.utility.client.controller.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import org.cyk.utility.clazz.ClassHelperImpl;
import org.cyk.utility.identifier.resource.UniformResourceIdentifierHelperImpl;
import org.cyk.utility.system.action.SystemAction;

public abstract class AbstractRowDataImpl<DATA extends Data> extends AbstractRowImpl implements RowData<DATA>,Serializable {
	private static final long serialVersionUID = 1L;

	private Class<DATA> dataClass;
	private DATA data;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		dataClass = (Class<DATA>) ClassHelperImpl.__getParameterAt__(getClass(), 0, Data.class);
	}
	
	@Override
	public DATA getData() {
		return data;
	}
	
	@Override
	public RowData<DATA> setData(DATA data) {
		this.data = data;
		return this;
	}
	
	@Override
	public String getUrlBySystemActionClass(Object request,Class<? extends SystemAction> aClass) {
		String url = null;		
		Data data = getData();
		/*Class<DATA> dataClass = getDataClass();
		if(dataClass == null)
			if(data!=null)
				dataClass = (Class<DATA>) data.getClass();
		*/
		/*SystemAction systemAction = __inject__(aClass).setEntityClass(dataClass);
		systemAction.getEntities(Boolean.TRUE).add(data);
		*/
		//url = UniformResourceIdentifierHelperImpl.__build__(request,systemAction);
		url = UniformResourceIdentifierHelperImpl.__build__(request, aClass, null, (Class<DATA>)data.getClass(), (Collection<DATA>)Arrays.asList(data), null, null, null);
		return url;
	}
	
	@Override
	public Class<DATA> getDataClass() {
		return dataClass;
	}
	
	@Override
	public RowData<DATA> setDataClass(Class<DATA> dataClass) {
		this.dataClass = dataClass;
		return this;
	}

}