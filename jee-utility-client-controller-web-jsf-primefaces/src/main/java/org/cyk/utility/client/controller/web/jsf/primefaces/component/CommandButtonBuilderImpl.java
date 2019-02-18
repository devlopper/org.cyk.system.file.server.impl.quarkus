package org.cyk.utility.client.controller.web.jsf.primefaces.component;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.client.controller.component.ComponentRole;
import org.cyk.utility.client.controller.component.VisibleComponent;
import org.cyk.utility.client.controller.component.command.Commandable;
import org.cyk.utility.client.controller.event.Event;
import org.cyk.utility.client.controller.event.Events;
import org.cyk.utility.client.controller.web.ComponentHelper;
import org.cyk.utility.client.controller.web.ValueExpressionMap;
import org.cyk.utility.client.controller.web.jsf.JavaServerFacesHelper;
import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.object.Objects;
import org.cyk.utility.string.Case;
import org.cyk.utility.string.StringHelper;
import org.cyk.utility.system.action.SystemAction;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.commandbutton.CommandButton;

public class CommandButtonBuilderImpl extends AbstractComponentBuilderImpl<CommandButton,Commandable> implements CommandButtonBuilder,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected CommandButton __execute__(Commandable commandable, ValueExpressionMap valueExpressionMap) throws Exception {
		CommandButton commandButton = new CommandButton();
		commandButton.setValue(commandable.getName());
		Object icon = commandable.getProperties().getIcon();
		if(icon!=null)
			commandButton.setIcon(icon.toString());
		
		if(commandable.getNavigation()!=null) {
			commandButton.setType("button");
			String url = null;
			if(__injectCollectionHelper__().contains(commandable.getRoles(), ComponentRole.COLLECTION_PROCESSOR)) {
				url = commandable.getNavigation().getUniformResourceLocator().toString();
			}else if(__injectCollectionHelper__().contains(commandable.getRoles(), ComponentRole.COLLECTION_ITEM_PROCESSOR)) {
				SystemAction action = commandable.getNavigation().getSystemAction();	
				String methodName = __injectStringHelper__().applyCase(StringUtils.substringBefore(action.getClass().getSimpleName(),"Impl")+"Class",Case.FIRST_CHARACTER_LOWER);
				if(__injectStringHelper__().isNotBlank(methodName))
					url = "#{indexRow.getUrlBySystemActionClass(componentHelper."+methodName+")}";
			}
			if(__inject__(StringHelper.class).isNotBlank(url))
				valueExpressionMap.set("onclick",__buildValueExpressionString__("window.open('"+url+"','_self');return false;"));
		}else if(commandable.getCommand()!=null) {
			commandButton.setType("submit");
			/*String update = __inject__(ComponentHelper.class).getGlobalMessagesTargetInlineComponentIdentifier()
					+","+__inject__(ComponentHelper.class).getGlobalMessagesTargetGrowlComponentIdentifier()
					+","+__inject__(ComponentHelper.class).getGlobalMessagesTargetDialogComponentIdentifier();
			*/
			String update = __inject__(ComponentHelper.class).getGlobalMessagesTargetsIdentifiers();
			
			Objects updatables = commandable.getUpdatables();
			
			if(__inject__(CollectionHelper.class).isNotEmpty(updatables))
				for(Object index : updatables.get()) {
					String token = null;
					if(index instanceof VisibleComponent) {
						token = (String)((VisibleComponent)index).getProperties().getIdentifierAsStyleClass();
						if(__inject__(StringHelper.class).isNotBlank(token))
							update = update + " , @(."+token+")";		
					}else
						update = update + " , "+index;
					
				}
			String commandableIdentifier = commandable.getIdentifier().toString();
			if(__inject__(StringHelper.class).isNotBlank(commandable.getCommand().getContainerContextDependencyInjectionBeanName())) {
				String actionExpressionLanguage = commandable.getCommand().getContainerContextDependencyInjectionBeanName()+".getCommandableByIdentifier('"+commandableIdentifier+"').command.function.executeToReturnVoid";
				commandButton.setActionExpression(__inject__(JavaServerFacesHelper.class).buildMethodExpression(actionExpressionLanguage, Void.class,new Class<?>[] {}));	
			}
			
			//update = StringUtils.replace(update, "glo", ":form:glo");
			
			commandButton.setUpdate(update);
			
			//commandButton.setImmediate(Boolean.TRUE);
			//System.out.println("CommandButtonBuilder.build() UPDATE : "+update);
		}else {
			commandButton.setType("button");
			if(commandable.getProperties().getOnClick()!=null)
				valueExpressionMap.set("onclick",__buildValueExpressionString__(commandable.getProperties().getOnClick().toString())) ;
		}
		
		
		
		Events events = commandable.getEvents();
		if(__inject__(CollectionHelper.class).isNotEmpty(events)) {
			String commandableIdentifier = commandable.getIdentifier().toString();
			for(Event index : events.get()) {
				if(index.getScript()==null) {
					//String actionExpressionLanguage = commandable.getCommand().getWindowContainerManaged().getContextDependencyInjectionBeanName()+".getCommandableByIdentifier('"+commandableIdentifier+"').events.getAt(0).properties.function.executeWithOneParameterToReturnVoid";
					String actionExpressionLanguage = commandable.getCommand().getContainerContextDependencyInjectionBeanName()+".getCommandableByIdentifier('"+commandableIdentifier+"').events.getAt(0).properties.function.executeWithOneParameterToReturnVoid";
					AjaxBehavior behavior = (AjaxBehavior) FacesContext.getCurrentInstance().getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
					behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(__inject__(JavaServerFacesHelper.class).buildMethodExpression(actionExpressionLanguage, Void.class,new Class<?>[] {})
							, __inject__(JavaServerFacesHelper.class).buildMethodExpression(actionExpressionLanguage, Void.class,new Class<?>[] {Object.class})));
					
					if(index.getProperties().getUpdate()!=null)
						behavior.setUpdate(index.getProperties().getUpdate().toString());
					//behavior.setListener(methodExpression);
					
					commandButton.addClientBehavior(index.getProperties().getEvent().toString(), behavior);	
				}
			}
		}
		
		return commandButton;
	}
	
}