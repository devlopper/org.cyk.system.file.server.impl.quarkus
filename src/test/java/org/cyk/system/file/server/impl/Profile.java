package org.cyk.system.file.server.impl;

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
	
	public class TextExtractorTika implements QuarkusTestProfile{
		@Override
		public Map<String, String> getConfigOverrides() {
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(TextExtractorTika.class);
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
			Map<String, String> map = org.cyk.quarkus.extension.test.Profile.buildConfig(Tika.class);
			return map;
		}
		
		@Override
		public Set<String> tags() {
			return org.cyk.quarkus.extension.test.Profile.buildTags(Tika.class);
		}
	}
}