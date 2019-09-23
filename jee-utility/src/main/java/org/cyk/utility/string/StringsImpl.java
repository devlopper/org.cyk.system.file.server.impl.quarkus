package org.cyk.utility.string;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.cyk.utility.__kernel__.constant.ConstantEmpty;
import org.cyk.utility.__kernel__.collection.AbstractCollectionInstanceImpl;
import org.cyk.utility.__kernel__.collection.CollectionHelper;

@Dependent
public class StringsImpl extends AbstractCollectionInstanceImpl<String> implements Strings,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Strings add(Collection<String> collection) {
		return (Strings) super.add(collection);
	}
	
	@Override
	public Strings add(String... elements) {
		return (Strings) super.add(elements);
	}

	@Override
	public Strings addWithPrefix(String prefix, Collection<String> elements) {
		if(__inject__(StringHelper.class).isNotEmpty(prefix) && elements instanceof List && CollectionHelper.isNotEmpty(elements)) {
			for(Integer index = 0 ; index < elements.size() ; index = index + 1)
				((List<String>)elements).set(index, prefix+((List<String>)elements).get(index));
		}
		return add(elements);
	}

	@Override
	public Strings addWithPrefix(String prefix, String... elements) {
		return addWithPrefix(prefix,List.of(elements));
	}
	
	@Override
	public String concatenate(Object separator) {
		return __inject__(StringHelper.class).concatenate(get(), separator == null ? ConstantEmpty.STRING : separator.toString());
	}
	
	@Override
	public String concatenate() {
		return concatenate(ConstantEmpty.STRING);
	}
}