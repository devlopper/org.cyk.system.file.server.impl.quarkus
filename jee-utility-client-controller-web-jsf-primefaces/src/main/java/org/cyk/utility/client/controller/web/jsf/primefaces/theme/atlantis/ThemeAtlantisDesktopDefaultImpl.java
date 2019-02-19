package org.cyk.utility.client.controller.web.jsf.primefaces.theme.atlantis;

import java.io.Serializable;

import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuBuilderMapGetter;
import org.cyk.utility.client.controller.component.menu.MenuItem;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilders;
import org.cyk.utility.client.controller.component.tab.Tab;
import org.cyk.utility.client.controller.component.tab.Tabs;
import org.cyk.utility.client.controller.component.theme.AbstractThemeImpl;
import org.cyk.utility.client.controller.component.theme.Theme;
import org.cyk.utility.client.controller.component.window.Window;
import org.cyk.utility.scope.ScopeSession;

public class ThemeAtlantisDesktopDefaultImpl extends AbstractThemeImpl implements ThemeAtlantisDesktopDefault,Serializable {
	private static final long serialVersionUID = 1L;

	private Tabs menuTabs;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		
		MenuBuilder menuBuilder = __inject__(MenuBuilderMapGetter.class).execute().getOutput().get(ScopeSession.class);
		MenuItemBuilders oldMenuItemBuilders = menuBuilder.getItems();
		for(MenuItemBuilder index : oldMenuItemBuilders.get()) {
			MenuItemBuilders items = null;
			if(index.getChildren()!=null) {
				items = __inject__(MenuItemBuilders.class);
				for(Object indexChild : index.getChildren())
					if(indexChild instanceof MenuItemBuilder)
						items.add((MenuItemBuilder)indexChild);
				index.getChildren().clear();
			}
			MenuItem item = index.execute().getOutput();
			
			Tab tab = __inject__(Tab.class);
			tab.setProperty(Properties.NAME, item.getCommandable().getName());
			tab.setProperty(Properties.ICON, item.getCommandable().getProperties().getIcon());
			if(items != null)
				tab.setProperty(Properties.MENU,__inject__(MenuBuilder.class).setItems(items).execute().getOutput());
			getMenuTabs(Boolean.TRUE).add(tab);
		}
	}
	
	@Override
	protected String __getIdentifier__() {
		return "org.cyk.utility.client.controller.web.jsf.primefaces.atlantis.desktop.default";
	}
	
	@Override
	protected String __getTemplateIdentifier__() {
		return "/template/default.xhtml";
	}
	
	@Override
	public Theme process(Window window) {
		__north__(window);
		__center__(window);
		__south__(window);
		return this;
	}
	
	private void __north__(Window window) {
		
	}
	
	private void __center__(Window window) {
		mapViews("center",window.getView());
	}

	private void __south__(Window window) {
		
	}
	
	@Override
	public Tabs getMenuTabs() {
		return menuTabs;
	}
	
	@Override
	public Tabs getMenuTabs(Boolean injectIfNull) {
		return (Tabs) __getInjectIfNull__(FIELD_MENU_TABS, injectIfNull);
	}
	
	@Override
	public ThemeAtlantisDesktopDefault setMenuTabs(Tabs menuTabs) {
		this.menuTabs = menuTabs;
		return this;
	}

	/**/
	
	public static final String FIELD_MENU_TABS = "menuTabs";
	
}
