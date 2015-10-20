package org.cyk.utility.common.generator;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractGeneratable<T> implements Serializable {

	private static final long serialVersionUID = 6717138845030531852L;

	protected RandomDataProvider provider = RandomDataProvider.getInstance();
	
	@Getter @Setter protected Object source;
	
	protected ByteArrayInputStream inputStream(byte[] bytes){
		return new ByteArrayInputStream(bytes);
	}
	
	protected String positiveFloatNumber(Integer left,Integer minRight,Integer maxRight){
		return provider.randomPositiveInt(left)+"."+provider.randomInt(minRight,maxRight);
	}
	
	public abstract void generate();
	
	
}