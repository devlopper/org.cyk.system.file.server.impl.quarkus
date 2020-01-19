package org.cyk.utility.__kernel__.persistence.query.filter;
import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cyk.utility.__kernel__.computation.ArithmeticOperator;
import org.cyk.utility.__kernel__.field.FieldInstance;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.value.ValueUsageType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class Field extends AbstractObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private FieldInstance instance;
	private Object value;
	private ValueUsageType valueUsageType;
	private ArithmeticOperator arithmeticOperator;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
