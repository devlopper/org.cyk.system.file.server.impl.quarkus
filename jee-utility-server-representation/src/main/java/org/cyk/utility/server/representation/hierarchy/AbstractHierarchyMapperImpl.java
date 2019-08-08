package org.cyk.utility.server.representation.hierarchy;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.clazz.ClassHelper;
import org.cyk.utility.mapping.AbstractMapperSourceDestinationImpl;
import org.cyk.utility.mapping.MapperSourceDestination;

public abstract class AbstractHierarchyMapperImpl<SOURCE,DESTINATION,NODE_SOURCE,NODE_DESTINATION,NODE_MAPPER extends MapperSourceDestination<NODE_SOURCE, NODE_DESTINATION>> extends AbstractMapperSourceDestinationImpl<SOURCE, DESTINATION> {
	private static final long serialVersionUID = 1L;
    
	private Class<NODE_MAPPER> nodeMapperClass;
	
	public NODE_SOURCE getNodeSource(NODE_DESTINATION destination) {
    	return destination == null ? null : __getNodeMapper__().getSource(destination);
    }
    
    public NODE_DESTINATION getNodeDestination(NODE_SOURCE source) {
    	return source == null ? null : __getNodeMapper__().getDestination(source);
    }

    @SuppressWarnings("unchecked")
	protected Class<NODE_MAPPER> __getNodeMapperClass__() {
		if(nodeMapperClass == null)
			nodeMapperClass = (Class<NODE_MAPPER>) DependencyInjection.inject(ClassHelper.class).getByName(DependencyInjection.inject(ClassHelper.class).getParameterAt(getClass(), 4, Object.class).getName());
    	return nodeMapperClass;
    }
    
    protected NODE_MAPPER __getNodeMapper__() {
    	return DependencyInjection.inject(__getNodeMapperClass__());
    }
}