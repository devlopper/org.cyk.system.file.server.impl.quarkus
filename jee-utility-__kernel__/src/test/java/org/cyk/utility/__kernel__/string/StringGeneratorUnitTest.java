package org.cyk.utility.__kernel__.string;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.cyk.utility.__kernel__.string.freemarker.FreeMarkerHelper;
import org.cyk.utility.__kernel__.test.weld.AbstractWeldUnitTest;
import org.junit.jupiter.api.Test;

public class StringGeneratorUnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void generate(){	
		assertThat(StringGenerator.getInstance().generate(FreeMarkerHelper.createTemplateFromString("test", "Hello ${user}"), Map.of("user","komenan"))).isEqualTo("Hello komenan");
	}
	
	@Test
	public void generate_project_server_persistence_entity_namable(){
		String string = StringGenerator.getInstance().generate(StringTemplateGetter.getInstance().get("project/system/server/persistence/entities/namable.ftl")
				, Map.of("system_package","org.cyk.system","system_identifier","contact","entity_class_name","Person","entity_table_name","person"));
		assertThat(string).contains("class Person","TABLE_NAME = \"person\";");
	}
	
}
