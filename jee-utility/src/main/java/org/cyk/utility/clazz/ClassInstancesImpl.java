package org.cyk.utility.clazz;

import java.io.Serializable;

import org.cyk.utility.collection.AbstractCollectionInstanceImpl;
import org.cyk.utility.collection.CollectionHelper;

public class ClassInstancesImpl extends AbstractCollectionInstanceImpl<ClassInstance> implements ClassInstances,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public ClassInstance get(Class<?> aClass) {
		if(aClass!=null && __inject__(CollectionHelper.class).isNotEmpty(collection))
			for(ClassInstance index : collection)
				if(aClass.equals(index.getClazz()))
					return index;
		return null;
	}

}
