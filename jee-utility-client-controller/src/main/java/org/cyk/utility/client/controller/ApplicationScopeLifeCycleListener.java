package org.cyk.utility.client.controller;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.__kernel__.AbstractApplicationScopeLifeCycleListener;
import org.cyk.utility.__kernel__.function.FunctionRunnableMap;
import org.cyk.utility.client.controller.component.command.CommandFunctionFunctionRunnableImpl;
import org.cyk.utility.client.controller.component.command.CommandFunctionImpl;
import org.cyk.utility.client.controller.component.window.WindowRenderTypeDialog;
import org.cyk.utility.client.controller.component.window.WindowRenderTypeNormal;
import org.cyk.utility.client.controller.navigation.NavigationIdentifierStringBuilderExtensionFunctionRunnableImpl;
import org.cyk.utility.client.controller.navigation.NavigationIdentifierStringBuilderExtensionImpl;
import org.cyk.utility.client.controller.proxy.ProxyGetterImpl;
import org.cyk.utility.client.controller.proxy.ProxyGetterRestEasyFunctionRunnableImpl;
import org.cyk.utility.identifier.resource.UniformResourceIdentifierParameterValueMatrix;
import org.cyk.utility.instance.InstanceBuilderImpl;
import org.cyk.utility.instance.InstanceGetterImpl;
import org.cyk.utility.string.StringHelper;
import org.cyk.utility.system.node.SystemClient;

@ApplicationScoped
public class ApplicationScopeLifeCycleListener extends AbstractApplicationScopeLifeCycleListener implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void __initialize__(Object object) {
		//__inject__(FunctionRunnableMap.class).set(CommandFunctionExecuteListenerThroughImpl.class, CommandFunctionExecuteListenerThroughFunctionRunnableImpl.class,Boolean.FALSE);
		__inject__(FunctionRunnableMap.class).set(InstanceGetterImpl.class, InstanceGetterFunctionRunnableImpl.class);
		__inject__(FunctionRunnableMap.class).set(ProxyGetterImpl.class, ProxyGetterRestEasyFunctionRunnableImpl.class);
		__inject__(FunctionRunnableMap.class).set(CommandFunctionImpl.class, CommandFunctionFunctionRunnableImpl.class);
		__inject__(FunctionRunnableMap.class).set(InstanceBuilderImpl.class, InstanceBuilderFunctionRunnableImpl.class);
		__inject__(FunctionRunnableMap.class).set(NavigationIdentifierStringBuilderExtensionImpl.class, NavigationIdentifierStringBuilderExtensionFunctionRunnableImpl.class);
		
		__inject__(UniformResourceIdentifierParameterValueMatrix.class).setClasses(WindowRenderTypeNormal.class,WindowRenderTypeDialog.class);
		
		__initializeApplication__(object);
	}
	
	protected void __initializeApplication__(Object object) {
		SystemClient systemClient = __inject__(SystemClient.class);
		if(__inject__(StringHelper.class).isBlank(systemClient.getName()))
			systemClient.setName("CLIENT_APP_NAME");
	}
	
	@Override
	public void __destroy__(Object object) {}
	
}
