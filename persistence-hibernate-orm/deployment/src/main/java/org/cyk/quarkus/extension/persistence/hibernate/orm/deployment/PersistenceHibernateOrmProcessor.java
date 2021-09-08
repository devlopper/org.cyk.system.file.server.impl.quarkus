package org.cyk.quarkus.extension.persistence.hibernate.orm.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class PersistenceHibernateOrmProcessor {

    private static final String FEATURE = "cyk-persistence-hibernate-orm";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
	UnremovableBeanBuildItem unremovables() {
		UnremovableBeanBuildItem item = UnremovableBeanBuildItem.beanClassNames(UNREMOVALES.stream().map(Class::getName).collect(Collectors.toSet()));
		return item;
	}
    
    @BuildStep
	AdditionalBeanBuildItem createServiceTwo() {
		AdditionalBeanBuildItem item = AdditionalBeanBuildItem.unremovableOf(org.cyk.quarkus.extension.hibernate.orm.ApplicationLifeCycleListener.class);
		return item;
	}
    
    private static final Collection<Class<?>> UNREMOVALES = new ArrayList<>();
    static {
    	UNREMOVALES.addAll(List.of(
    			org.cyk.utility.__kernel__.object.__static__.persistence.EntityLifeCycleListener.class
    			,org.cyk.utility.__kernel__.object.__static__.persistence.EntityLifeCycleListenerImpl.class
    			,org.cyk.utility.persistence.server.hibernate.Initializer.class
    			,org.cyk.utility.persistence.server.hibernate.InitializerImpl.class
    			,org.cyk.utility.persistence.query.QueryManager.class
    			,org.cyk.utility.persistence.query.QueryManagerImpl.class
    			,org.cyk.utility.persistence.server.QueryManagerImpl.class
    			
    			,org.cyk.utility.persistence.EntityManagerFactoryGetter.class,org.cyk.utility.persistence.EntityManagerFactoryGetterImpl.class
    			,org.cyk.utility.persistence.EntityManagerGetter.class,org.cyk.utility.persistence.EntityManagerGetterImpl.class
    			
    			,org.cyk.quarkus.extension.hibernate.orm.ApplicationLifeCycleListener.class
    			));
    }
}
