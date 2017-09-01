package org.cyk.utility.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.cyk.utility.common.helper.InstanceHelper;
import org.cyk.utility.common.helper.MapHelper;
import org.cyk.utility.common.helper.MapHelper.Stringifier.Entry.InputStrategy;
import org.cyk.utility.common.helper.MapHelper.Stringifier.Entry.OutputStrategy;
import org.cyk.utility.test.unit.AbstractUnitTest;
import org.junit.Test;

import lombok.Getter;
import lombok.Setter;

public class MapHelperUnitTest extends AbstractUnitTest {

	private static final long serialVersionUID = -6691092648665798471L;

	static {
		MapHelper.Stringifier.Entry.Adapter.Default.GET_VALUE_CLASS = Mapping.class;
		MapHelper.Stringifier.Entry.Adapter.Default.DEFAULT_KEY_VALUES_SEPARATOR = "&";
		MapHelper.Stringifier.Adapter.Default.DEFAULT_SEPARATOR = "&";
	}
	
	@Test
	public void containsKey(){
		Map<String,String> map = new HashMap<>();
		assertFalse(new MapHelper.ContainsKey.String.Adapter.Default("a").setProperty(MapHelper.ContainsKey.PROPERTY_NAME_MAP, map).execute());
		map.put("b", "bval");
		assertFalse(new MapHelper.ContainsKey.String.Adapter.Default("a").setProperty(MapHelper.ContainsKey.PROPERTY_NAME_MAP, map).execute());
		map.put("a", "aval");
		assertTrue(new MapHelper.ContainsKey.String.Adapter.Default("a").setProperty(MapHelper.ContainsKey.PROPERTY_NAME_MAP, map).execute());
		assertFalse(new MapHelper.ContainsKey.String.Adapter.Default("A").setProperty(MapHelper.ContainsKey.PROPERTY_NAME_MAP, map).execute());
		assertTrue(new MapHelper.ContainsKey.String.Adapter.Default("A").setProperty(MapHelper.ContainsKey.PROPERTY_NAME_MAP, map)
				.setProperty(MapHelper.ContainsKey.PROPERTY_NAME_CASE_SENSITIVE, Boolean.FALSE).execute());
	}
	
	@Test
	public void getValue(){
		Map<String,String> map = new HashMap<>();
		assertNull(new MapHelper.GetValue.String.Adapter.Default<java.lang.String>(map,java.lang.String.class).setProperty(MapHelper.GetValue.PROPERTY_NAME_KEY, "a").execute());
		map.put("b", "bval");
		assertNull(new MapHelper.GetValue.String.Adapter.Default<java.lang.String>(map,java.lang.String.class).setProperty(MapHelper.GetValue.PROPERTY_NAME_KEY, "a").execute());
		map.put("a", "aval");
		assertNotNull(new MapHelper.GetValue.String.Adapter.Default<java.lang.String>(map,java.lang.String.class).setProperty(MapHelper.GetValue.PROPERTY_NAME_KEY, "a").execute());
		assertNull(new MapHelper.GetValue.String.Adapter.Default<java.lang.String>(map,java.lang.String.class).setProperty(MapHelper.GetValue.PROPERTY_NAME_KEY, "A").execute());
		assertNotNull(new MapHelper.GetValue.String.Adapter.Default<java.lang.String>(map,java.lang.String.class).setProperty(MapHelper.GetValue.PROPERTY_NAME_KEY, "A")
				.setProperty(MapHelper.ContainsKey.PROPERTY_NAME_CASE_SENSITIVE, Boolean.FALSE).execute());
	}
	
	@Test
	public void getStringValueAs(){
		Map<String,String> map = new HashMap<>();
		assertEquals(83, MapHelper.getInstance().getStringValueAs(Integer.class, map, "a", "83"));
		map.put("a", "12");
		assertEquals(12, MapHelper.getInstance().getStringValueAs(Integer.class, map, "a", "52"));
	}
	
	@Test
	public void stringifyEntryOne(){
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set(null,null).execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set(null,"v1").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set("a",null).execute());
		assertEquals("a=12", new MapHelper.Stringifier.Entry.Adapter.Default().set("a","12").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set("","").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set("","v1").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set("a","").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set(" "," ").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set(" ", "v1").execute());
		assertEquals(null, new MapHelper.Stringifier.Entry.Adapter.Default().set("a", " ").execute());
		
		assertEquals("a=15", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", 15).execute());
		assertEquals("a=15", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", new ClassA(15l)).execute());
		assertEquals("a=15", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", Arrays.asList(new ClassA(15l))).execute());
		assertEquals("a=15", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", new Object[]{new ClassA(15l)}).execute());
		
		assertEquals("a=2_f_0", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", 15).setIsEncoded(Boolean.TRUE).execute());
		assertEquals("a=2_f_0", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", new ClassA(15l)).setIsEncoded(Boolean.TRUE).execute());
		assertEquals("a=2_f_0", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", Arrays.asList(new ClassA(15l))).setIsEncoded(Boolean.TRUE).execute());
		assertEquals("a=2_f_0", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", new Object[]{new ClassA(15l)}).setIsEncoded(Boolean.TRUE).execute());
	}
	
	@Test
	public void stringifyEntryMany(){
		assertEquals("a=v1,v2", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", "v1,v2").execute());
		assertEquals("a=v1&a=v2", new MapHelper.Stringifier.Entry.Adapter.Default().setOutputStrategy(OutputStrategy.KEY_MANY_VALUES)
				.setInputStrategy(InputStrategy.ONE_MANY).set("a","v1,v2").execute());
		
		assertEquals("a=17&5", new MapHelper.Stringifier.Entry.Adapter.Default().setInputStrategy(InputStrategy.MANY).set("a", new Object[]{17,5}).execute());
		assertEquals("a=17&a=5", new MapHelper.Stringifier.Entry.Adapter.Default("a", new Object[]{17,5}).setOutputStrategy(OutputStrategy.KEY_MANY_VALUES).execute());
		
		assertEquals("a=9&4&13", new MapHelper.Stringifier.Entry.Adapter.Default().setInputStrategy(InputStrategy.MANY).set("a", Arrays.asList("9","4","13")).execute());
		assertEquals("a=9&a=4&a=13", new MapHelper.Stringifier.Entry.Adapter.Default("a", Arrays.asList("9","4","13")).setOutputStrategy(OutputStrategy.KEY_MANY_VALUES).execute());
		
		assertEquals(null,new MapHelper.Stringifier.Entry.Adapter.Default().execute());
		assertEquals(null,new MapHelper.Stringifier.Entry.Adapter.Default(Constant.EMPTY_STRING, null).execute());
		assertEquals(null,new MapHelper.Stringifier.Entry.Adapter.Default(" ", null).execute());
		assertEquals(null,new MapHelper.Stringifier.Entry.Adapter.Default("p1", null).execute());
		assertEquals(null,new MapHelper.Stringifier.Entry.Adapter.Default(null, "a").execute());
		assertEquals("p1=a",new MapHelper.Stringifier.Entry.Adapter.Default("p1","a").execute());
		
		assertEquals("p1=a",new MapHelper.Stringifier.Entry.Adapter.Default("p1",new Object[]{"a",null}).execute());
		assertEquals("p1=b",new MapHelper.Stringifier.Entry.Adapter.Default("p1",new Object[]{null,"b"}).execute());
		
		assertEquals("p1=a&p1=b&p1=v3",new MapHelper.Stringifier.Entry.Adapter.Default("p1",new Object[]{"a","b","v3"}).setKeyValuesSeparator("&").setOutputStrategy(OutputStrategy.KEY_MANY_VALUES).execute());
		
		assertEquals("p1=15",new MapHelper.Stringifier.Entry.Adapter.Default("p1","15").execute());
		assertEquals("p1=2_f_0",new MapHelper.Stringifier.Entry.Adapter.Default("p1","15").setIsEncoded(Boolean.TRUE).execute());
		
		assertEquals("p1=15",new MapHelper.Stringifier.Entry.Adapter.Default("p1",new ClassA(15l)).execute());
		assertEquals("p1=2_f_0",new MapHelper.Stringifier.Entry.Adapter.Default("p1",new ClassA(15l)).setIsEncoded(Boolean.TRUE).execute());
		
		assertEquals("a=15&3", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", Arrays.asList(new ClassA(15l),3)).execute());
		assertEquals("a=15&3", new MapHelper.Stringifier.Entry.Adapter.Default().set("a", new Object[]{new ClassA(15l),3}).execute());
		
	}
	
	@Test
	public void stringify(){
		assertEquals("p1=v1&p2=v2&p4=17&5&p3=v3&p5=9&4&13&p6=v6", new MapHelper.Stringifier.Adapter.Default().addKeyValue(
				"p1", "v1","p2", "v2","p4", new Object[]{17,5},"p3", "v3","p5", Arrays.asList("9","4","13"),"p6", "v6").execute());
		
		assertEquals("p1=a&p2=b",new MapHelper.Stringifier.Adapter.Default().addKeyValue("p1","a","p2","b").execute());
		assertEquals("p1=a&p2=b",new MapHelper.Stringifier.Adapter.Default().addKeyValue("p1","a","p2","b","","b").execute());
		assertEquals("p1=a&p2=b",new MapHelper.Stringifier.Adapter.Default().addKeyValue("p1","a","p2","b"," ","b").execute());
		assertEquals("p1=a&p2=b&p3=15",new MapHelper.Stringifier.Adapter.Default().addKeyValue("p1","a","p2","b","p3",15).execute());		
		assertEquals("p1=a&p2=b&p3=2_f_0&encoded=p3",new MapHelper.Stringifier.Adapter.Default().addKeyValue("p1","a","p2","b","p3",15)
				.addEncodedKeys("p3").execute());
		assertEquals("p1=a&p2=b&p3=2_f_0&p4=2_a_0&encoded=p3&encoded=p4",new MapHelper.Stringifier.Adapter.Default(MapHelper.getInstance()
				.getByKeyValue("p1","a","p2","b","p3",15,"p4",10)).addEncodedKeys("p3","p4").execute());
		assertEquals("p1=a&p2=b&p3=2_f_0&p4=2_a_0&encoded=p3&encoded=p4",new MapHelper.Stringifier.Adapter.Default(MapHelper.getInstance()
				.getByKeyValue("p1","a","p2","b","p3",Arrays.asList(new ClassA(15l)),"p4",Arrays.asList(new ClassA(10l)))).addEncodedKeys("p3","p4").execute());
		
	}
		
	@Getter @Setter
	public static class ClassA implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Long identifier;
		
		public ClassA(Long identifier) {
			this.identifier = identifier;
		}
		
	}
	
	public static class Mapping extends InstanceHelper.Mapping.Adapter.Default {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected Object __execute__() {
			if(getInput() instanceof ClassA)
				return ((ClassA)getInput()).getIdentifier();
			return super.__execute__();
		}
		
	}
	
}
