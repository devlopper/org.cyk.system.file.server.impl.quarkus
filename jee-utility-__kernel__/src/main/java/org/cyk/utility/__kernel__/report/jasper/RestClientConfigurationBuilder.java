package org.cyk.utility.__kernel__.report.jasper;

import java.io.Serializable;

import org.cyk.utility.__kernel__.Helper;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.value.Value;
import org.cyk.utility.__kernel__.value.ValueHelper;

import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public interface RestClientConfigurationBuilder {

	RestClientConfiguration build(Arguments arguments);
	
	default RestClientConfiguration build() {
		return build(null);
	}
	
	public static abstract class AbstractImpl extends AbstractObject implements RestClientConfigurationBuilder,Serializable {
		
		@Override
		public RestClientConfiguration build(Arguments arguments) {
			if(arguments == null)
				arguments = new Arguments();
			RestClientConfiguration configuration = RestClientConfiguration.loadConfiguration(ValueHelper.defaultToIfBlank(arguments.getConfigurationFilePath(), "jasperreportserver.properties"));
			LogHelper.logInfo("Jasper server rest client configuration has been instantiated", getClass());
			return configuration;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class Arguments implements Serializable {
		private String configurationFilePath;
	}
	
	/**/
	
	static RestClientConfigurationBuilder getInstance() {
		return Helper.getInstance(RestClientConfigurationBuilder.class, INSTANCE);
	}
	
	Value INSTANCE = new Value();
}