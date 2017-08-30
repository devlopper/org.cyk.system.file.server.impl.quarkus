package org.cyk.utility.common.helper;

import java.io.Serializable;

import javax.inject.Singleton;

@Singleton
public class UniformResourceLocatorHelper extends AbstractHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private static UniformResourceLocatorHelper INSTANCE;
	
	public static UniformResourceLocatorHelper getInstance() {
		if(INSTANCE == null)
			INSTANCE = new UniformResourceLocatorHelper();
		return INSTANCE;
	}
	
	@Override
	protected void initialisation() {
		INSTANCE = this;
		super.initialisation();
	}
	
	public static interface Builder<OUTPUT> extends org.cyk.utility.common.Builder.NullableInput<OUTPUT> {
		
		public static class Adapter<OUTPUT> extends org.cyk.utility.common.Builder.NullableInput.Adapter<OUTPUT> implements Serializable {

			private static final long serialVersionUID = 1L;

			public Adapter(Class<OUTPUT> outputClass) {
				super(outputClass);
			}
			
			/**/
			
			public static class Default<OUTPUT> extends Builder.Adapter<OUTPUT> implements Serializable {

				private static final long serialVersionUID = 1L;

				public Default(Class<OUTPUT> outputClass) {
					super(outputClass);
				}	
			}	
		}
	}
	
}
