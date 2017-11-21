package org.cyk.utility.common.userinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.cyk.utility.common.Constant;
import org.cyk.utility.common.Properties;
import org.cyk.utility.common.cdi.AbstractBean;
import org.cyk.utility.common.helper.ArrayHelper;
import org.cyk.utility.common.helper.ClassHelper;
import org.cyk.utility.common.helper.CollectionHelper;
import org.cyk.utility.common.helper.ListenerHelper;
import org.cyk.utility.common.helper.MethodHelper;
import org.cyk.utility.common.helper.RandomHelper;
import org.cyk.utility.common.helper.StringHelper;
import org.cyk.utility.common.helper.UniformResourceLocatorHelper;
import org.cyk.utility.common.model.Area;
import org.cyk.utility.common.userinterface.container.window.Window;
import org.cyk.utility.common.userinterface.input.Watermark;
import org.cyk.utility.common.userinterface.output.OutputText;

@Getter @Setter @Accessors(chain=true)
public class Component extends AbstractBean implements Serializable {
	private static final long serialVersionUID = 1L;

	static {
		ClassHelper.getInstance().map(Listener.class, Listener.Adapter.Default.class,Boolean.FALSE);
	}
	
	private static final Set<ComponentClass> CLASSES = new HashSet<>();
	
	protected Object built;
	protected Component parent;
	protected CollectionHelper.Instance<Component> children;
	
	/**/

	public Component() {
		//getPropertiesMap();//trigger settings
	}
	
	@Override
	protected Properties instanciateProperties() {
		return super.instanciateProperties().setIdentifier(RandomHelper.getInstance().getAlphabetic(5)).setRendered(Boolean.TRUE);
	}
	
	public Component addOneChild(Component component){
		if(children == null)
			children = instanciateChildrenCollection();
		children.addOne(component);
		return this;
	}
	
	public Component addManyChild(Collection<Component> components){
		if(CollectionHelper.getInstance().isNotEmpty(components))
			for(Component component : components)
				addOneChild(component);
		return this;
	}
	
	public Component addManyChild(Component...components){
		if(ArrayHelper.getInstance().isNotEmpty(components))
			addManyChild(Arrays.asList(components));
		return this;
	}
	
	public Component prepare(){
		return this;
	}
	
	protected CollectionHelper.Instance<Component> instanciateChildrenCollection(){
		CollectionHelper.Instance<Component> collection = new CollectionHelper.Instance<Component>();
		collection.setElementClass(Component.class);
		return collection;
	}
	
	public Component build(){
		if(children!=null)
			new CollectionHelper.Iterator.Adapter.Default<Component>(children.getElements()){
				private static final long serialVersionUID = 1L;
				@Override
				protected void __executeForEach__(Component component) {
					component.build();
				}
			}.execute();
		built = ListenerHelper.getInstance()
				.listenObject(Listener.COLLECTION, Listener.METHOD_NAME_BUILD, MethodHelper.Method.Parameter.buildArray(Component.class,this));
		return this;
	}
	
	public Component _setPropertyUrl(String pathIdentifier,Object...queryKeyValue){
		getPropertiesMap().setUrl(UniformResourceLocatorHelper.getInstance().stringify(pathIdentifier,queryKeyValue));
		return this;
	}
	
	public Component _setPropertyUrl(Constant.Action action,Object object,Object...queryKeyValue){
		getPropertiesMap().setUrl(UniformResourceLocatorHelper.getInstance().stringify(action, object, queryKeyValue));
		return this;
	}
	
	/**/
	
	public static interface BuilderBase<OUTPUT extends Component> extends org.cyk.utility.common.Builder.NullableInput<OUTPUT> {

		BuilderBase<OUTPUT> setComponentParent(Component componentParent);
		
		public static class Adapter<OUTPUT extends Component> extends org.cyk.utility.common.Builder.NullableInput.Adapter.Default<OUTPUT> implements BuilderBase<OUTPUT>, Serializable {
			private static final long serialVersionUID = 1L;

			protected Component componentParent;
			
			public Adapter(Class<OUTPUT> outputClass) {
				super(outputClass);
			}
			
			@Override
			public BuilderBase<OUTPUT> setComponentParent(Component componentParent) {
				return null;
			}

			/**/

			public static class Default<OUTPUT extends Component> extends BuilderBase.Adapter<OUTPUT> implements Serializable {
				private static final long serialVersionUID = 1L;

				public Default(Class<OUTPUT> outputClass) {
					super(outputClass);
				}
				
				@Override
				public BuilderBase<OUTPUT> setComponentParent(Component componentParent) {
					this.componentParent = componentParent;
					return this;
				}
			}
		}
		
		/**/
		
		public static interface Target<COMPONENT,OUTPUT> extends org.cyk.utility.common.Builder<COMPONENT, OUTPUT> {
			
			public static class Adapter<COMPONENT,OUTPUT> extends org.cyk.utility.common.Builder.Adapter.Default<COMPONENT, OUTPUT> implements Target<COMPONENT,OUTPUT>,Serializable {
				private static final long serialVersionUID = 1L;
				
				public Adapter(Class<COMPONENT> componentClass,COMPONENT input, Class<OUTPUT> outputClass) {
					super(componentClass, input, outputClass);
				}
				
				public static class Default<COMPONENT,OUTPUT> extends Target.Adapter<COMPONENT,OUTPUT> implements Serializable {
					private static final long serialVersionUID = 1L;
					
					public Default(Class<COMPONENT> componentClass,COMPONENT input, Class<OUTPUT> outputClass) {
						super(componentClass,input, outputClass);
					}
					
					@SuppressWarnings("unchecked")
					public Default() {
						this(null,null, null);
						setOutputClass((Class<OUTPUT>) ClassHelper.getInstance().getParameterAt(getClass(), 0, Object.class));
					}
					
					@Override
					protected OUTPUT __execute__() {
						OUTPUT instance = ClassHelper.getInstance().instanciateOne(getOutputClass());
						
						return instance;
					}		
				}
			}
		}
	}
	
	public static interface Builder extends BuilderBase<Component> {

		public static class Adapter extends BuilderBase.Adapter.Default<Component> implements Builder, Serializable {
			private static final long serialVersionUID = 1L;

			public Adapter() {
				super(Component.class);
			}

			/**/

			public static class Default extends Builder.Adapter implements Serializable {
				private static final long serialVersionUID = 1L;

			}
		}
	}
	
	/**/
	
	@Getter @Setter @Accessors(chain=true)
	public static class Visible extends Component implements Serializable {
		private static final long serialVersionUID = 1L;

		protected Area area = new Area();
		protected OutputText label;
		
		/**/
		
		public OutputText getLabel(){
			if(label == null)
				label = new OutputText();
			return label;
		}
		
		public Visible setLabel(OutputText label){
			this.label = label;
			if(label!=null){
				if(getPropertiesMap().getTitle()==null)
					getPropertiesMap().setTitle(label.getPropertiesMap().getValue());
			}
			__setWatermarkFromLabel__();
			return this;
		}
		
		public Visible __setWatermarkFromLabel__(){
			Watermark watermark = (Watermark)getPropertiesMap().getWatermark();
			if(watermark==null){
				getPropertiesMap().setWatermark(watermark = new Watermark());
			}
			watermark.getPropertiesMap().setValue(label.getPropertiesMap().getValue());
			return this;
		}

		public Visible setLabelFromIdentifier(String identifier){
			if(StringHelper.getInstance().isBlank(identifier))
				getLabel().getPropertiesMap().setValue(null);
			else
				getLabel().getPropertiesMap().setValue(StringHelper.getInstance().get(identifier, new Object[]{}));
			__setWatermarkFromLabel__();
			if(getPropertiesMap().getTitle()==null)
				getPropertiesMap().setTitle(getLabel().getPropertiesMap().getValue());
			return this;
		}
				
		public Visible setLength(Number length){
			getArea().getLength().setDistance(length);
			return this;
		}
		
		public Visible setWidth(Number width){
			getArea().getWidth().setDistance(width);
			return this;
		}
		
		/**/
		
		public static interface BuilderBase<OUTPUT extends Visible> extends Component.BuilderBase<OUTPUT> {

			public static class Adapter<OUTPUT extends Visible> extends Component.BuilderBase.Adapter.Default<OUTPUT> implements BuilderBase<OUTPUT>, Serializable {
				private static final long serialVersionUID = 1L;

				public Adapter(Class<OUTPUT> outputClass) {
					super(outputClass);
				}

				/**/

				public static class Default<OUTPUT extends Visible> extends BuilderBase.Adapter<OUTPUT> implements Serializable {
					private static final long serialVersionUID = 1L;

					public Default(Class<OUTPUT> outputClass) {
						super(outputClass);
					}
				}
			}
		}
		
		public static interface Builder extends BuilderBase<Visible> {

			public static class Adapter extends BuilderBase.Adapter.Default<Visible> implements Builder, Serializable {
				private static final long serialVersionUID = 1L;

				public Adapter() {
					super(Visible.class);
				}

				/**/

				public static class Default extends Builder.Adapter implements Serializable {
					private static final long serialVersionUID = 1L;

				}
			}
		}
	
		/**/

	}
	
	public static class Invisible extends Component implements Serializable {
		private static final long serialVersionUID = 1L;

		/**/

		public static interface BuilderBase<OUTPUT extends Invisible> extends Component.BuilderBase<OUTPUT> {

			public static class Adapter<OUTPUT extends Invisible> extends Component.BuilderBase.Adapter.Default<OUTPUT> implements BuilderBase<OUTPUT>, Serializable {
				private static final long serialVersionUID = 1L;

				public Adapter(Class<OUTPUT> outputClass) {
					super(outputClass);
				}

				/**/

				public static class Default<OUTPUT extends Invisible> extends BuilderBase.Adapter<OUTPUT> implements Serializable {
					private static final long serialVersionUID = 1L;

					public Default(Class<OUTPUT> outputClass) {
						super(outputClass);
					}
				}
			}
		}
		
		public static interface Builder extends BuilderBase<Invisible> {

			public static class Adapter extends BuilderBase.Adapter.Default<Invisible> implements Builder, Serializable {
				private static final long serialVersionUID = 1L;

				public Adapter() {
					super(Invisible.class);
				}

				/**/

				public static class Default extends Builder.Adapter implements Serializable {
					private static final long serialVersionUID = 1L;

				}
			}
		}
	}

	/**/
	
	public static interface Listener {
		
		Collection<Listener> COLLECTION = new ArrayList<Listener>();
		
		String METHOD_NAME_BUILD = "build";
		Object build(Component component);
		
		String METHOD_NAME_LISTEN_INSTANCIATE_ONE = "listenInstanciateOne";
		void listenInstanciateOne(Component component);
		
		String METHOD_NAME_LISTEN_INSTANCIATE_ONE_NULL_SPECIFIC_CLASS = "listenInstanciateOneNullSpecificClass";
		void listenInstanciateOneNullSpecificClass(Component component);
		
		String METHOD_NAME_LISTEN_INSTANCIATE_ONE_NON_NULL_SPECIFIC_CLASS = "listenInstanciateOneNonNullSpecificClass";
		void listenInstanciateOneNonNullSpecificClass(Component component);

		public static class Adapter extends AbstractBean implements Listener , Serializable {
			private static final long serialVersionUID = 1L;

			@Override
			public Object build(Component component) {
				return null;
			}

			@Override
			public void listenInstanciateOne(Component component) {}

			@Override
			public void listenInstanciateOneNullSpecificClass(Component component) {}

			@Override
			public void listenInstanciateOneNonNullSpecificClass(Component component) {}
			
			public static class Default extends Listener.Adapter implements Serializable {
				private static final long serialVersionUID = 1L;
				
				@Override
				public void listenInstanciateOneNonNullSpecificClass(Component component) {
					component.prepare();
				}
			}
		}
	}

	/**/
	
	@SuppressWarnings("unchecked")
	public static void setClass(Class<? extends Component> parentClass,Constant.Action[] actions,Class<?> actionOnClass,Object key,Class<?> clazz){
		if(ArrayHelper.getInstance().isNotEmpty(actions))
			for(Constant.Action action : actions)
				CLASSES.add(new ComponentClass(parentClass,action, actionOnClass, key, (Class<? extends Component>) clazz));
	}
	
	public static void setClass(Class<? extends Component> parentClass,Constant.Action action,Class<?> actionOnClass,Object key,Class<?> clazz){
		setClass(parentClass, new Constant.Action[]{action}, actionOnClass, key, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Component> getClass(Component parent,Constant.Action action,Class<?> actionOnClass,Object key,Class<?> nullValue){
		for(ComponentClass componentClass : CLASSES)
			if(
					( componentClass.getParentClass()==null || parent==null || componentClass.getParentClass().equals(parent.getClass()) )
					&& ( componentClass.getAction().equals(action) )
					&& ( componentClass.getActionOnClass().equals(actionOnClass) )
					&& ( componentClass.getKey()==null || componentClass.getKey().equals(key) ) 
				)
				return componentClass.getClazz();
		return (Class<? extends Component>) nullValue;
	}
	
	public static Class<?> getClass(Component parent,Constant.Action action,Class<?> actionOnClass,Class<?> nullValue){
		return getClass(parent,action, actionOnClass, null, nullValue);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Component> T get(Component parent,Class<T> componentClass,Constant.Action action,Class<?> actionOnClass,Object key,Class<? extends Component> nullClassValue){
		Class<T> aClass = (Class<T>) getClass(parent,action, actionOnClass, key, nullClassValue);
		T component = (T) ClassHelper.getInstance().instanciateOne(aClass);
		component.setParent(parent);
		ListenerHelper.getInstance().listen(Listener.COLLECTION, Listener.METHOD_NAME_LISTEN_INSTANCIATE_ONE, MethodHelper.Method.Parameter
				.buildArray(Component.class,component));
		if(aClass.equals(nullClassValue)){
			ListenerHelper.getInstance().listen(Listener.COLLECTION, Listener.METHOD_NAME_LISTEN_INSTANCIATE_ONE_NULL_SPECIFIC_CLASS, MethodHelper.Method.Parameter
					.buildArray(Component.class,component));
		}else{
			ListenerHelper.getInstance().listen(Listener.COLLECTION, Listener.METHOD_NAME_LISTEN_INSTANCIATE_ONE_NON_NULL_SPECIFIC_CLASS, MethodHelper.Method.Parameter
					.buildArray(Component.class,component));
		}
		return component;
	}
	
	public static <T extends Component> T get(Component parent,Class<T> componentClass,Constant.Action action,Class<?> actionOnClass,Object key){
		return get(parent, componentClass, action, actionOnClass, key, componentClass);
	}
	
	public static <T extends Component> T get(Window window,Class<T> componentClass){
		return get(window,componentClass, window.getAction(), window.getActionOnClass(), window.getActionKey());
	}
	
	public static void clearClasses(){
		CLASSES.clear();
	}
	
	@Getter @Setter @Accessors(chain=true) @AllArgsConstructor @EqualsAndHashCode(of={"parentClass","action","actionOnClass","key"})
	public static class ComponentClass implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Class<? extends Component> parentClass;
		private Constant.Action action;
		private Class<?> actionOnClass;
		private Object key;
		
		private Class<? extends Component> clazz;
		
	}
}