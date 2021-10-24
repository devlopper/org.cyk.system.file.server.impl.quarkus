package org.cyk.quarkus.extension.business.core_.deployment;

import java.util.ArrayList;
import java.util.Collection;

import org.cyk.utility.__kernel__.array.ArrayHelper;

import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class BusinessCoreProcessor {

    private static final String FEATURE = "cyk-business-core";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }
    
    @BuildStep
	UnremovableBeanBuildItem unremovablesNames() {
		return UnremovableBeanBuildItem.beanClassNames(UNREMOVALES_NAMES.toArray(new String[] {}));
	}
    
    private static final Collection<String> UNREMOVALES_NAMES = new ArrayList<>();
    static {
    	addUnremovable(UNREMOVALES_NAMES
    			,org.cyk.utility.business.Validator.class
    			,org.cyk.utility.business.Initializer.class
    			,org.cyk.utility.business.server.EntityCreator.class
    			,org.cyk.utility.business.server.EntityReader.class
    			,org.cyk.utility.business.server.EntityUpdater.class
    			,org.cyk.utility.business.server.EntityDeletor.class
    			);
    }
    
    private static void addUnremovable(Class<?> interfaceClass,Collection<String> unremovablesNames) {
    	unremovablesNames.add(interfaceClass.getName());
    	unremovablesNames.add(interfaceClass.getName()+"Impl");
    }
    
    private static void addUnremovable(Collection<String> unremovablesNames,Class<?>...interfaceClass) {
    	if(ArrayHelper.isEmpty(interfaceClass))
    		return;
    	for(Class<?> klass : interfaceClass)
    		addUnremovable(klass, unremovablesNames);
    }
}