package org.cyk.quarkus.extension.hibernate.orm;

import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@javax.enterprise.context.ApplicationScoped
public class ApplicationLifeCycleListener {

    void onStart(@Observes StartupEvent startupEvent) {               
    	org.cyk.utility.persistence.server.hibernate.Initializer.initialize();
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {               
        
    }
}