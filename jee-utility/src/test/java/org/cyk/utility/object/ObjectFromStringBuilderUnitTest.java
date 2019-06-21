package org.cyk.utility.object;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.cyk.utility.__kernel__.annotation.JavaScriptObjectNotation;
import org.cyk.utility.network.message.Receivers;
import org.cyk.utility.string.Strings;
import org.cyk.utility.test.weld.AbstractWeldUnitTest;
import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class ObjectFromStringBuilderUnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void objectFieldAreNull_json_stringOnlyClosures(){
		Class object = buildObjectFromStringUsingJson("{}", Class.class,"field01");
		assertionHelper.assertNotNull(object);
		assertionHelper.assertNull(object.getField01());
		assertionHelper.assertNull(object.getField02());
		assertionHelper.assertNull(object.getField03());
	}
	
	@Test
	public void objectField01IsNotNull_json_stringField01Is1(){
		Class object = buildObjectFromStringUsingJson("{\"field01\":\"1\"}", Class.class,"field01");
		assertionHelper.assertNotNull(object);
		assertionHelper.assertEquals("1",object.getField01());
	}
	
	@Test
	public void objectField01IsNotNull_json_stringField04Is1_abc_4(){
		Class object = buildObjectFromStringUsingJson("{\"field04\":\"1,abc,4\"}", Class.class,"field04");
		assertionHelper.assertNotNull(object);
		assertThat(object.getField04().get()).containsExactly("1","abc","4");
	}
	
	@Test
	public void objectField01IsNotNull_json_receiverField05Is1_abc_4(){
		Class object = buildObjectFromStringUsingJson("{\"field05\":\"1,abc,4\"}", Class.class,"field05");
		assertionHelper.assertNotNull(object);
		assertThat(object.getField05().getAt(0).getIdentifier()).isEqualTo("1");
		assertThat(object.getField05().getAt(1).getIdentifier()).isEqualTo("abc");
		assertThat(object.getField05().getAt(2).getIdentifier()).isEqualTo("4");
	}
	
	@Test
	public void objectField01IsNotNull_json_stringField06Isk01_v01(){
		Class object = buildObjectFromStringUsingJson("{\"field06\":\"{\\\"k01\\\":\\\"v01\\\"}\"}", Class.class,"field06");
		assertionHelper.assertNotNull(object);
		assertThat(object.getField06()).containsKeys("k01").containsValues("v01");
	}
	
	/*
	@Test
	public void stringify_json_field01IsNotNull(){
		assertionHelper.assertEquals("{\"field01\":\"abc\"}",buildObjectFromStringUsingJson(new Class().setField01("abc"), "field01"));
	}
	
	@Test
	public void stringify_json_field01IsNotNull_field02IsNotNull(){
		assertionHelper.assertEquals("{\"field01\":\"abc\",\"field02\":\"159\"}",buildObjectFromStringUsingJson(new Class().setField01("abc").setField02("159")
				, "field01","field02"));
	}
	*/
	/**/
	
	private static <T> T buildObjectFromStringUsingJson(String string,java.lang.Class<T> klass,String...fieldNamesStrings) {
		ObjectFromStringBuilder objectFromStringBuilder = __injectByQualifiersClasses__(ObjectFromStringBuilder.class,JavaScriptObjectNotation.Class.class);
		objectFromStringBuilder.setString(string);
		objectFromStringBuilder.setKlass(klass);
		objectFromStringBuilder.addFieldNamesStrings(fieldNamesStrings);
		return (T) objectFromStringBuilder.execute().getOutput();
	}
	
	/**/
	
	@Getter @Setter @Accessors(chain=true)
	public static class Class {
		private String field01;
		private String field02;
		private String field03;
		private Strings field04;
		private Receivers field05;
		private Map<String,String> field06;
	}
}