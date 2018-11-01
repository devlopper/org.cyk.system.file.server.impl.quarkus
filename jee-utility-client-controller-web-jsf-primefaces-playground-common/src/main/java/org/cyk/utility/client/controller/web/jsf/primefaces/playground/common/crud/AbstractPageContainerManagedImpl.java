package org.cyk.utility.client.controller.web.jsf.primefaces.playground.common.crud;

import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuBuilderMap;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;
import org.cyk.utility.client.controller.component.menu.MenuRenderTypeRowBar;
import org.cyk.utility.scope.ScopeSession;

public abstract class AbstractPageContainerManagedImpl extends org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl {
	private static final long serialVersionUID = 1L;

	@Override
	protected MenuBuilderMap __getMenuBuilderMap__() {
		MenuBuilder menuBuilder = __inject__(MenuBuilder.class).setRenderType(__inject__(MenuRenderTypeRowBar.class));
		menuBuilder.addItems(
				__inject__(MenuItemBuilder.class).setName("MyEntity")
					.addChild(__inject__(MenuItemBuilder.class).setName("Lister").setNavigationIdentifier("myEntityListWindow")
							,__inject__(MenuItemBuilder.class).setName("Créer").setNavigationIdentifier("myEntityEditWindow")
					)
				
					);
		return __inject__(MenuBuilderMap.class).set(ScopeSession.class,menuBuilder);
	}
	
}
