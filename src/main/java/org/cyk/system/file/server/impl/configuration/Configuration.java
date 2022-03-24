package org.cyk.system.file.server.impl.configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RegExUtils;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "cyk")
public interface Configuration {

	File file();
	
	interface File {
	/*
		@WithName("extension.default.accepted.path.name.regular.expression")
		@WithDefault(".pdf|.txt|.docx|.doc|.rtf|.jpeg|.jpg|.png|.bmp|.gif")
		String acceptedPathNameRegularExpression();
		*/
		Duplicate duplicate();
		
		Size size();
		
		Sha1 sha1();
		
		Path acceptedPath();
		
		Importation importation();
		
		OpticalCharacterRecognition opticalCharacterRecognition();
		
		Directories directories();
		
		Text text();
		
		/**/
		
		interface Importation {
			
			Executor executor();
			
			Batch batch();
			
			interface Executor {
				@WithDefault("4")
				Integer threadCount();
				
				Timeout timeout();
				
				interface Timeout {
					@WithDefault("5")
					Long duration();
					
					@WithDefault("MINUTES")
					TimeUnit unit();
				}
			}
			
			interface Batch {
				@WithDefault("25")
				Integer size();
			}
		}
		
		interface Path {
			Name name();
			interface Name {
				@WithDefault(".pdf|.txt|.docx|.doc|.rtf|.jpeg|.jpg|.png|.bmp|.gif")
				String regularExpression();
			}
		}
		
		interface Sha1 {
			Computation computation();
			
			interface Computation {
				@WithDefault("false")
				Boolean parallelized();
			}
		}
		
		
		interface Size {
			@WithDefault("1")
			Long minimal();
			
			@WithDefault("5242880")//5M
			Long maximal();
		}
		
		
		interface Duplicate {
			@WithDefault("false")
			Boolean allowed();
		}
		
		interface Directories {
			@WithName("default")
			@WithDefault("data/files")
			List<String> default_();
		}		
		
		interface Text {
			List<Replacer> replacers();

			interface Replacer {
				@WithName("regular.expression")
				@WithConverter(StringConverter.class)
				String regularExpression();
				
				@WithName("replacement")
				@WithConverter(StringConverter.class)
				String replacement();
			}
			
			Extraction extraction();
			
			interface Extraction {
				@WithDefault("true")
				Boolean runnableAfterCreation();
				
				@WithDefault("0 */30 * ? * *")
				String cron();
			}
			
			Normalizer normalizer();
			
			interface Normalizer {
				@WithDefault("NFD")
				java.text.Normalizer.Form form();
			}
			
			Line line();
			
			interface Line {
				@WithDefault("\r\n")
				String separator();
				
				@WithDefault("3")
				Integer minimalLenght();
				
				Word word();
				
				interface Word {
					@WithDefault(" ")
					String separator();
					
					@WithDefault("3")
					Integer minimalLenght();
					
					@WithDefault("0.5")
					Float minimalRate();
				}
				
				//List<Replacer> replacers();
			}
		}
	}
	
	interface OpticalCharacterRecognition {
		
		Tesseract tesseract();
		
		interface Tesseract {
			Data data();
			
			interface Data {
				@WithDefault("/usr/local/share/tessdata")
				String path();	
			}
			
			@WithDefault("fra")
			String language();
		}
	}

	static class StringConverter implements  org.eclipse.microprofile.config.spi.Converter<String> {
		public static final String SPACE_MARKER = "__CHAR__SPACE__";
	    @Override
	    public String convert(String value) {
	    	if(value == null)
	        	return value;
	    	return RegExUtils.replaceAll(value, SPACE_MARKER, " ");
	    }
	}
}