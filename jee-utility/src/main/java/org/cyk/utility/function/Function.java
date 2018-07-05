package org.cyk.utility.function;

import org.cyk.utility.__kernel__.object.dynamic.Objectable;
import org.cyk.utility.__kernel__.properties.Properties;

public interface Function<INPUT,OUTPUT> extends Objectable {

	Function<INPUT,OUTPUT> setInput(INPUT input);
	Function<INPUT,OUTPUT> execute();
	OUTPUT getOutput();
	
	Function<INPUT,OUTPUT> setProperties(Properties properties);
	
	Function<INPUT,OUTPUT> setLoggable(Boolean loggable);
	Boolean getLoggable();
	
	Function<INPUT,OUTPUT> setMonitorable(Boolean monitorable);
	Boolean getMonitorable();
}
