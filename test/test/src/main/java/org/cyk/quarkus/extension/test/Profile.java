package org.cyk.quarkus.extension.test;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public interface Profile {

	public static Set<String> buildTags(Set<Class<?>> classes) {
		Set<String> tags = classes.stream().map(klass -> klass.getSimpleName().toLowerCase()).collect(Collectors.toSet());
		tags.add(StringUtils.join(tags, "."));
		return tags;
	}
	
	public static Set<String> buildTags(Class<?>...classes) {
		return buildTags(classes == null || classes.length == 0 ? null : Set.of(classes));
	}
	
	public static Map<String,String> buildConfig(Set<Class<?>> classes) {
		Map<String, String> config = new HashMap<>();
		config.put("quarkus.hibernate-orm.sql-load-script", String.format("sql/%s.sql", classes.stream().map(klass -> klass.getSimpleName().toLowerCase()).collect(Collectors.joining("-"))));
		config.put("quarkus.http.test-port", "0");
		return config;
	}
	
	public static Map<String,String> buildConfig(Class<?>...classes) {
		return buildConfig(classes == null || classes.length == 0 ? null : new LinkedHashSet<Class<?>>(List.of(classes)));
	}
}