package org.cyk.utility.client.controller.web.jsf;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.Helper;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.value.Value;

public interface OutcomeBuilder {

	String build(Class<?> klass,Action action);
	
	
	/**/
	
	static OutcomeBuilder getInstance() {
		return Helper.getInstance(OutcomeBuilder.class, INSTANCE);
	}
	
	Value INSTANCE = DependencyInjection.inject(Value.class);
	
	String FORMAT = "%s%sView";
}