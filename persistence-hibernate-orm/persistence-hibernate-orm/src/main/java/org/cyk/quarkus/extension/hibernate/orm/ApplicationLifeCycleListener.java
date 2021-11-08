package org.cyk.quarkus.extension.hibernate.orm;

import javax.enterprise.event.Observes;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.persistence.query.EntityCounter;
import org.cyk.utility.persistence.query.EntityReader;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;

@Startup(value = ApplicationLifeCycleListener.ORDER)
@javax.enterprise.context.ApplicationScoped
public class ApplicationLifeCycleListener {
	public static final int ORDER = 2500;
	
	public static Class<?> QUALIFIER = Qualifier.class;
	
    void onStart(@Observes StartupEvent startupEvent) {
    	DependencyInjection.setQualifierClassTo(QUALIFIER, EntityReader.class);
    	DependencyInjection.setQualifierClassTo(QUALIFIER, EntityCounter.class);
    	org.cyk.utility.persistence.server.hibernate.Initializer.initialize();
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {               
        
    }
}