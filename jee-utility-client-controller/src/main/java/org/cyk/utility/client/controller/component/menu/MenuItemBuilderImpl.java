package org.cyk.utility.client.controller.component.menu;

import java.io.Serializable;
import java.util.Collection;

import org.cyk.utility.client.controller.component.AbstractVisibleComponentBuilderImpl;
import org.cyk.utility.client.controller.component.command.CommandableBuilder;

public class MenuItemBuilderImpl extends AbstractVisibleComponentBuilderImpl<MenuItem> implements MenuItemBuilder,Serializable {
	private static final long serialVersionUID = 1L;

	private CommandableBuilder commandable;
	
	@Override
	protected void __execute__(MenuItem menuItem) {
		super.__execute__(menuItem);
		CommandableBuilder commandable = getCommandable();
		if(commandable!=null)
			menuItem.setCommandable(commandable.execute().getOutput());
		
		Collection<Object> children = getChildren();
		if(__injectCollectionHelper__().isNotEmpty(children)) {
			for(Object index : children) {
				if(index instanceof MenuItemBuilder)
					menuItem.addChild( ((MenuItemBuilder)index).execute().getOutput() );
			}
		}
	}
	
	@Override
	public MenuItemBuilder setCommandable(CommandableBuilder commandable) {
		this.commandable = commandable;
		return this;
	}
	
	@Override
	public CommandableBuilder getCommandable() {
		return commandable;
	}
	
	@Override
	public CommandableBuilder getCommandable(Boolean injectIfNull) {
		return (CommandableBuilder) __getInjectIfNull__(FIELD_COMMANDABLE, injectIfNull);
	}
	
	@Override
	public MenuItemBuilder setCommandableNavigationIdentifier(Object identifier) {
		getCommandable(Boolean.TRUE).setNavigationIdentifier(identifier);
		return this;
	}
	
	@Override
	public MenuItemBuilder setCommandableNavigationIdentifierAndParameters(Object identifier,Object[] parameters) {
		getCommandable(Boolean.TRUE).setNavigationIdentifierAndParameters(identifier,parameters);
		return this;
	}
	
	@Override
	public MenuItemBuilder setCommandableName(String name) {
		getCommandable(Boolean.TRUE).setName(name);
		return this;
	}
	
	@Override
	public MenuItemBuilder addChild(Object... child) {
		return (MenuItemBuilder) super.addChild(child);
	}
	
	@Override
	public MenuItemBuilder setParent(Object parent) {
		return (MenuItemBuilder) super.setParent(parent);
	}
	
	@Override
	public MenuItemBuilder getParent() {
		return (MenuItemBuilder) super.getParent();
	}
	
	/**/
	
	public static final String FIELD_COMMANDABLE = "commandable";
	
}
