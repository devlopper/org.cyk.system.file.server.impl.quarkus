package org.cyk.quarkus.extension.test.deployment;

import java.util.ArrayList;
import java.util.Collection;

import io.quarkus.arc.deployment.UnremovableBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class TestProcessor {

    private static final String FEATURE = "cyk test";

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
    			
    			);
    }
    
    private static void addUnremovable(Class<?> interfaceClass,Collection<String> unremovablesNames) {
    	unremovablesNames.add(interfaceClass.getName());
    	unremovablesNames.add(interfaceClass.getName()+"Impl");
    }
    
    private static void addUnremovable(Collection<String> unremovablesNames,Class<?>...interfaceClass) {
    	if(interfaceClass == null || interfaceClass.length == 0)
    		return;
    	for(Class<?> klass : interfaceClass)
    		addUnremovable(klass, unremovablesNames);
    }
}