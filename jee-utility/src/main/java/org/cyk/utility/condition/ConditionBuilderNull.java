package org.cyk.utility.condition;

import org.cyk.utility.field.FieldValueGetter;

public interface ConditionBuilderNull extends ConditionBuilder {

	FieldValueGetter getFieldValueGetter();
	ConditionBuilderNull setFieldValueGetter(FieldValueGetter fieldValueGetter);
	
	String getValueName();
	ConditionBuilderNull setValueName(String name);
	
}
