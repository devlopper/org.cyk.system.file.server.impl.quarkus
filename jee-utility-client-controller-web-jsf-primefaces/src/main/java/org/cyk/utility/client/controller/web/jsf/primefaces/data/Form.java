package org.cyk.utility.client.controller.web.jsf.primefaces.data;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.identifier.resource.ParameterName;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceIdentifierHelper;
import org.cyk.utility.__kernel__.internationalization.InternationalizationHelper;
import org.cyk.utility.__kernel__.internationalization.InternationalizationPhrase;
import org.cyk.utility.__kernel__.klass.ClassHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.object.Builder;
import org.cyk.utility.__kernel__.object.Configurator;
import org.cyk.utility.__kernel__.object.__static__.controller.AbstractDataIdentifiableSystemStringIdentifiableBusinessStringImpl;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.Input;
import org.cyk.utility.__kernel__.properties.Properties;
import org.cyk.utility.__kernel__.string.Case;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.system.action.SystemActionList;
import org.cyk.utility.__kernel__.throwable.RuntimeException;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.client.controller.ControllerEntity;
import org.cyk.utility.client.controller.ControllerLayer;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.InputBuilder;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.InputClassGetter;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.panel.Dialog;
import org.omnifaces.util.Faces;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class Form extends AbstractObject implements Serializable {

	private String title;
	private Action action;
	private Class<?> entityClass;
	private Object entity;
	private ControllerEntity<Object> controllerEntity;
	private Object request;
	private Layout layout;
	private Collection<String> entityFieldsNames;
	private Collection<String> updatableEntityFieldsNames;
	private CommandButton submitCommandButton;
	private Object container;
	private Form.Listener listener;
	private Map<String,AbstractInput<?>> inputs;
	
	public void execute() {
		Listener listener = this.listener == null ? Listener.AbstractImpl.DefaultImpl.INSTANCE : this.listener;
		listener.listenBeforeExecute(this);
		listener.act(this);		
		if(container instanceof Dialog) {
			((Dialog)container).hideOnComplete();
			//PrimefacesHelper.updateOnComplete(":form:"+dataTable.getIdentifier());
		}else if(!WebController.getInstance().isPageRenderedAsDialog())
			listener.redirect(this, request);		
		listener.listenAfterExecute(this);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInput(Class<T> klass,String fieldName) {
		if(klass == null || StringHelper.isBlank(fieldName) || MapHelper.isEmpty(inputs))
			return null;
		return (T) inputs.get(fieldName);
	}
	
	/**/
	
	public static final String FIELD_SUBMIT_COMMAND_BUTTON = "submitCommandButton";
	public static final String FIELD_CONTAINER = "container";
	public static final String FIELD_REQUEST = "request";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_ACTION = "action";
	public static final String FIELD_ENTITY_CLASS = "entityClass";
	public static final String FIELD_ENTITY = "entity";
	public static final String FIELD_CONTROLLER_ENTITY = "controllerEntity";
	public static final String FIELD_LAYOUT = "layout";
	public static final String FIELD_ENTITY_FIELDS_NAMES = "entityFieldsNames";
	public static final String FIELD_LISTENER = "listener";
	public static final String METHOD_EXECUTE = "execute";
	
	/**/
	
	public static class ConfiguratorImpl extends Configurator.AbstractImpl<Form> implements Serializable {

		@SuppressWarnings("unchecked")
		@Override
		public void configure(Form form, Map<Object, Object> arguments) {
			super.configure(form, arguments);
			Listener listener = (Listener) MapHelper.readByKey(arguments, FIELD_LISTENER);
			if(form.action == null) {
				form.action = Action.getByNameCaseInsensitive(Faces.getRequestParameter(ParameterName.ACTION_IDENTIFIER.getValue()));
			}
			
			if(form.controllerEntity == null && form.entityClass != null) {
				form.controllerEntity = (ControllerEntity<Object>) __inject__(ControllerLayer.class).injectInterfaceClassFromEntityClass(form.entityClass);
			}
						
			if(form.entity == null) {
				if(Action.CREATE.equals(form.action))
					form.entity = ClassHelper.instanciate(form.entityClass);
				else {
					String entityIdentifier = Faces.getRequestParameter(ParameterName.ENTITY_IDENTIFIER.getValue());
					if(StringHelper.isNotBlank(entityIdentifier))
						form.entity = form.controllerEntity.readBySystemIdentifier(entityIdentifier);
				}
			}
			
			if(form.entityFieldsNames == null && form.entityClass != null) {
				form.entityFieldsNames = listener == null ? Listener.AbstractImpl.getFieldsNames_static(form.entity) : listener.getFieldsNames(form);
			}
			
			if(form.title == null && form.entityClass != null && form.action != null) {
				InternationalizationPhrase internationalizationPhrase = new InternationalizationPhrase().setKase(Case.FIRST_CHARACTER_UPPER_REMAINDER_LOWER);
				internationalizationPhrase.addNoun(form.action.name()).addString("of").addString(form.entityClass);
				InternationalizationHelper.processPhrases(internationalizationPhrase);
				form.title = internationalizationPhrase.getValue();
			}
			
			if(form.layout == null && CollectionHelper.isNotEmpty(form.entityFieldsNames)) {
				Collection<String> inputsFieldsNames = (Collection<String>) MapHelper.readByKey(arguments, FIELD_INPUTS_FIELDS_NAMES);
				if(CollectionHelper.isEmpty(inputsFieldsNames)) {
					inputsFieldsNames = form.entityFieldsNames;
				}
				
				Collection<AbstractInput<?>> inputs = null;
				if(MapHelper.isEmpty(form.inputs)) {
					for(String fieldName : inputsFieldsNames) {
						AbstractInput<?> input = listener == null 
								? Listener.AbstractImpl.buildInput_static(form.entity, fieldName, Listener.AbstractImpl.getInputArguments_static(form.entity, fieldName)) 
										: listener.buildInput(form, fieldName);
						if(input == null)
							continue;
						if(inputs == null)
							inputs = new ArrayList<>();
						inputs.add(input);
					}
				}else {
					inputs = form.inputs.values();
				}
				if(CollectionHelper.isNotEmpty(inputs)) {
					Collection<Map<Object,Object>> cells = __getLayoutCellsArgumentsMaps__(form,inputs);
					if(CollectionHelper.isNotEmpty(cells)) {
						Object[] layoutArguments = new Object[] {
								Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.UI_G,Layout.FIELD_NUMBER_OF_COLUMNS,2
								,Layout.FIELD_ROW_CELL_MODEL,Map.of(0,new Cell().setWidth(3),1,new Cell().setWidth(9))
								,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cells
						};
						form.layout = Layout.build(layoutArguments);
					}			
				}
			}
			
			if(form.request == null) {
				form.request = __inject__(HttpServletRequest.class);
			}
			
			if(form.updatableEntityFieldsNames == null) {
				if(CollectionHelper.isNotEmpty(form.entityFieldsNames)) {
					form.updatableEntityFieldsNames = new ArrayList<String>(form.entityFieldsNames);
					Field systemIdentifierField = FieldHelper.getSystemIdentifier(form.entityClass);
					if(systemIdentifierField != null)
						form.updatableEntityFieldsNames.add(systemIdentifierField.getName());
				}
			}
		}
		
		protected Collection<Map<Object,Object>> __getLayoutCellsArgumentsMaps__(Form form,Collection<AbstractInput<?>> inputs) {
			if(CollectionHelper.isEmpty(inputs))
				return null;
			form.inputs = new HashMap<>();
			Collection<Map<Object,Object>> cells = new ArrayList<>();
			inputs.forEach(new Consumer<AbstractInput<?>>() {
				@Override
				public void accept(AbstractInput<?> input) {
					cells.add(MapHelper.instantiate(Cell.FIELD_CONTROL,input.getOutputLabel()));
					cells.add(MapHelper.instantiate(Cell.FIELD_CONTROL,input));
					form.inputs.put(input.getField().getName(), input);//TODO nested wont work. use path instead
				}			
			});
			if(Action.CREATE.equals(form.action) || Action.UPDATE.equals(form.action) || Action.DELETE.equals(form.action) || Action.EDIT.equals(form.action)) {
				Map<Object,Object> submitCommandArguments = MapHelper.instantiate(CommandButton.FIELD_ICON,"fa fa-floppy-o",CommandButton.ConfiguratorImpl.FIELD_OBJECT,form
						,CommandButton.ConfiguratorImpl.FIELD_METHOD_NAME,METHOD_EXECUTE,CommandButton.ConfiguratorImpl.FIELD_INPUTS,inputs);
				if(form.container instanceof Dialog) {
					Dialog dialog = (Dialog) form.container;
					form.submitCommandButton = CommandButton.build(submitCommandArguments);
					form.submitCommandButton.getRunnerArguments().getSuccessMessageArguments().setRenderTypes(List.of(RenderType.GROWL,RenderType.INLINE));
					CollectionHelper.setElementAt(dialog.getCommandButtons(), 0, form.submitCommandButton);
					dialog.setExecuteCommandButton(form.submitCommandButton);
				}else {
					cells.add(MapHelper.instantiate(Cell.FIELD_CONTROL,CommandButton.build(submitCommandArguments)));
				}
			}			
			return cells;
		}	
		
		@Override
		protected Class<Form> __getClass__() {
			return Form.class;
		}
		
		/**/
		
		public static final String FIELD_INPUTS_FIELDS_NAMES = "inputsFieldsNames";
		public static final String FIELD_METHOD_NAME = "methodName";
		public static final String FIELD_LISTENER = "configurator.listener";
	
		/**/
		
		public static interface Listener {
			Collection<String> getFieldsNames(Form form);
			AbstractInput<?> buildInput(Form form,String fieldName);		
			Class<?> getInputClass(Form form,String fieldName);
			Map<Object,Object> getInputArguments(Form form,String fieldName);
			
			public static abstract class AbstractImpl extends AbstractObject implements Listener,Serializable {
				@Override
				public Collection<String> getFieldsNames(Form form) {
					return getFieldsNames_static(form.getEntity());
				}
				
				@Override
				public AbstractInput<?> buildInput(Form form,String fieldName) {
					return buildInput_static(form.getEntity(),fieldName,getInputArguments(form,fieldName));
				}
				
				@Override
				public Class<?> getInputClass(Form form,String fieldName) {
					return getInputClass_static(form.getEntity(),fieldName);
				}
				
				@Override
				public Map<Object,Object> getInputArguments(Form form,String fieldName) {
					return getInputArguments_static(form.getEntity(),fieldName);
				}
				
				/**/
				
				public static Collection<String> getFieldsNames_static(Object object) {
					Collection<String> fieldsNames = FieldHelper.getNames(FieldHelper.getByAnnotationClass(object.getClass(), Input.class));
					if(CollectionHelper.getSize(fieldsNames) < 2)
						return fieldsNames;
					if(fieldsNames.contains(AbstractDataIdentifiableSystemStringIdentifiableBusinessStringImpl.FIELD_IDENTIFIER)
							&& fieldsNames.contains(AbstractDataIdentifiableSystemStringIdentifiableBusinessStringImpl.FIELD_CODE))
						fieldsNames.remove(AbstractDataIdentifiableSystemStringIdentifiableBusinessStringImpl.FIELD_IDENTIFIER);
					return fieldsNames;
				}
				
				public static AbstractInput<?> buildInput_static(Object object,String fieldName,Map<Object,Object> arguments) {
					return InputBuilder.getInstance().build(object, fieldName, arguments);
				}
				
				public static Map<Object,Object> getInputArguments_static(Object object,String fieldName) {
					Map<Object,Object> arguments = new HashMap<>();
					arguments.put(AbstractInput.FIELD_OBJECT, object);
					arguments.put(AbstractInput.FIELD_FIELD, FieldHelper.getByName(object.getClass(), fieldName));
					return arguments;
				}
				
				public Class<?> getInputClass_static( Object object,String fieldName) {
					return InputClassGetter.getInstance().get(object.getClass(), fieldName);
				}
			}
		}
	}
	
	public static Form build(Map<Object,Object> arguments) {
		return Builder.build(Form.class,arguments);
	}
	
	public static Form build(Object...objects) {
		return build(MapHelper.instantiate(objects));
	}
	
	static {
		Configurator.set(Form.class, new ConfiguratorImpl());
	}
	
	/**/
	
	public static interface Listener {
		void listenBeforeExecute(Form form);
		void act(Form form);
		void listenAfterExecute(Form form);
		void redirect(Form form,Object request);
		
		/**/
		
		public static abstract class AbstractImpl extends AbstractObject implements Listener,Serializable {
			
			@Override
			public void act(Form form) {
				if(Action.CREATE.equals(form.action))
					form.controllerEntity.create(form.entity);
				else if(Action.UPDATE.equals(form.action)) {			 
					if(CollectionHelper.isEmpty(form.updatableEntityFieldsNames))
						throw new RuntimeException("No fields names have been defined for update");			
					form.controllerEntity.update(form.entity,new Properties().setFields(StringHelper.concatenate(form.updatableEntityFieldsNames, ",")));
				}else if(Action.DELETE.equals(form.action)) {
					form.controllerEntity.delete(form.entity);
				}else
					throw new RuntimeException(String.format("Action %s not yet handled", form.action));
			}
			
			@Override
			public void redirect(Form form,Object request) {
				Faces.redirect(UniformResourceIdentifierHelper.build(request, SystemActionList.class, null, form.entityClass, null, null, null, null));
			}
			
			@Override
			public void listenBeforeExecute(Form form) {}
			
			@Override
			public void listenAfterExecute(Form form) {}
			
			/**/
			
			public static class DefaultImpl extends AbstractImpl implements Serializable {
				public static final Form.Listener INSTANCE = new DefaultImpl();
			}
		}
	}

	/**/
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value={ElementType.TYPE})
	public static @interface Annotation {
		
	}
}