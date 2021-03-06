package ci.gouv.dgbf.system.collectif.server.impl;

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
}