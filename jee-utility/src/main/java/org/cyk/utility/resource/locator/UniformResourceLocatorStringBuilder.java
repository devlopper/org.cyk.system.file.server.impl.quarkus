package org.cyk.utility.resource.locator;

import org.cyk.utility.identifier.resource.UniformResourceIdentifierStringBuilder;
import org.cyk.utility.string.StringFunction;

public interface UniformResourceLocatorStringBuilder extends StringFunction {

	UniformResourceIdentifierStringBuilder getUniformResourceIdentifierString();
	UniformResourceIdentifierStringBuilder getUniformResourceIdentifierString(Boolean injectIfNull);
	UniformResourceLocatorStringBuilder setUniformResourceIdentifierString(UniformResourceIdentifierStringBuilder uniformResourceIdentifierString);
	
}
