package org.cyk.system.file.server.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.quarkus.test.junit.QuarkusTestProfile;

public interface Profile extends org.cyk.quarkus.extension.test.Profile {

	public class File implements QuarkusTestProfile{
		@Override
		public Map<String, String> getConfigOverrides() {
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(File.class);
			return map;
		}
		
		@Override
		public Set<String> tags() {
			return org.cyk.quarkus.extension.test.Profile.buildTags(File.class);
		}
	}
	
	public class FileText implements QuarkusTestProfile{
		@Override
		public Map<String, String> getConfigOverrides() {
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(FileText.class);
			return map;
		}
		
		@Override
		public Set<String> tags() {
			return org.cyk.quarkus.extension.test.Profile.buildTags(FileText.class);
		}
	}
	
	public class TextNormalizer implements QuarkusTestProfile{
		@Override
		public Map<String, String> getConfigOverrides() {
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(List.of(TextNormalizer.class),null,null);
			return map;
		}
		
		@Override
		public Set<String> tags() {
			return org.cyk.quarkus.extension.test.Profile.buildTags(TextNormalizer.class);
		}
	}
	
	public class TextExtractorTika implements QuarkusTestProfile{
		@Override
		public Map<String, String> getConfigOverrides() {
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(List.of(TextExtractorTika.class),null,null);
			return map;
		}
		
		@Override
		public Set<String> tags() {
			return org.cyk.quarkus.extension.test.Profile.buildTags(TextExtractorTika.class);
		}
	}
	
	public class Tika implements QuarkusTestProfile{
		@Override
		public Map<String, String> getConfigOverrides() {
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(List.of(Tika.class),null,null);
			return map;
		}
		
		@Override
		public Set<String> tags() {
			return org.cyk.quarkus.extension.test.Profile.buildTags(Tika.class);
		}
	}
}