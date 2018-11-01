package org.cyk.utility.client.controller.component.menu;

import java.io.Serializable;

import org.cyk.utility.client.controller.component.AbstractVisibleComponentImpl;
import org.cyk.utility.client.controller.navigation.Navigation;

public class MenuItemImpl extends AbstractVisibleComponentImpl implements MenuItem,Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Navigation navigation;
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public MenuItem setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public Navigation getNavigation() {
		return navigation;
	}
	
	@Override
	public MenuItem setNavigation(Navigation navigation) {
		this.navigation = navigation;
		return this;
	}
	
	@Override
	public MenuItem getParent() {
		return (MenuItem) super.getParent();
	}

}
