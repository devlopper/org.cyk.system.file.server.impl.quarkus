package org.cyk.utility.common.generator;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

public abstract class AbstractGeneratable<T> implements Serializable {

	private static final long serialVersionUID = 6717138845030531852L;

	protected RandomDataProvider provider = RandomDataProvider.getInstance();
	
	protected ByteArrayInputStream inputStream(byte[] bytes){
		return new ByteArrayInputStream(bytes);
	}
	
	public abstract void generate();
	
}
