package org.cyk.system.file.server.impl;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.system.file.server.impl.service.FileDtoImpl;
import org.cyk.system.file.server.impl.service.FileDtoImplMapper;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.computation.SortOrder;
import org.cyk.utility.__kernel__.mapping.MapperClassGetter;
import org.cyk.utility.business.Initializer;
import org.cyk.utility.business.Validator;
import org.cyk.utility.persistence.query.CountQueryIdentifierGetter;
import org.cyk.utility.persistence.query.EntityCounter;
import org.cyk.utility.persistence.query.EntityReader;
import org.cyk.utility.persistence.server.DefaultProjectionsGetterImpl;
import org.cyk.utility.persistence.server.DefaultSortOrdersGetterImpl;
import org.cyk.utility.persistence.server.TransientFieldsProcessor;
import org.cyk.utility.persistence.server.query.string.RuntimeQueryStringBuilder;
import org.cyk.utility.service.server.PersistenceEntityClassGetterImpl;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@javax.enterprise.context.ApplicationScoped
public class ApplicationLifeCycleListener {

	@Inject CountQueryIdentifierGetter countQueryIdentifierGetter;
	
    void onStart(@Observes StartupEvent startupEvent) {
    	org.cyk.quarkus.extension.hibernate.orm.ApplicationLifeCycleListener.QUALIFIER = org.cyk.system.file.server.api.System.class;
    	DependencyInjection.setQualifierClassTo(org.cyk.system.file.server.api.System.class, EntityReader.class,EntityCounter.class
    			, RuntimeQueryStringBuilder.class,TransientFieldsProcessor.class, Initializer.class,Validator.class);
    	
    	MapperClassGetter.MAP.put(FileDtoImpl.class, FileDtoImplMapper.class);
    	PersistenceEntityClassGetterImpl.MAP.put(FileDtoImpl.class,FileImpl.class);
    	DefaultProjectionsGetterImpl.MAP.put(File.class, List.of(FileImpl.FIELD_IDENTIFIER,FileImpl.FIELD_NAME_AND_EXTENSION));
    	DefaultSortOrdersGetterImpl.MAP.put(File.class, Map.of(FileImpl.FIELD_NAME,SortOrder.ASCENDING));
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {               
        
    }
}