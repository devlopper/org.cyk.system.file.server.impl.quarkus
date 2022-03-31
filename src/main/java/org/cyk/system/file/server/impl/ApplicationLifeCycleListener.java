package org.cyk.system.file.server.impl;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.system.file.server.impl.service.FileDtoImpl;
import org.cyk.system.file.server.impl.service.FileDtoImplMapper;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.computation.SortOrder;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.mapping.MapperClassGetter;
import org.cyk.utility.business.Initializer;
import org.cyk.utility.business.Validator;
import org.cyk.utility.file.PathsScanner;
import org.cyk.utility.persistence.query.CountQueryIdentifierGetter;
import org.cyk.utility.persistence.query.EntityCounter;
import org.cyk.utility.persistence.query.EntityReader;
import org.cyk.utility.persistence.server.DefaultProjectionsGetterImpl;
import org.cyk.utility.persistence.server.DefaultSortOrdersGetterImpl;
import org.cyk.utility.persistence.server.TransientFieldsProcessor;
import org.cyk.utility.persistence.server.query.string.RuntimeQueryStringBuilder;
import org.cyk.utility.service.server.PersistenceEntityClassGetterImpl;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@javax.enterprise.context.ApplicationScoped
public class ApplicationLifeCycleListener {

	public static java.io.File FILE_DIRECTORY;
	
	@Inject CountQueryIdentifierGetter countQueryIdentifierGetter;
	@Inject Configuration configuration;
	
    void onStart(@Observes StartupEvent startupEvent) {
    	//logConfiguration();
    	org.cyk.quarkus.extension.hibernate.orm.ApplicationLifeCycleListener.QUALIFIER = org.cyk.system.file.server.api.System.class;
    	DependencyInjection.setQualifierClassTo(org.cyk.system.file.server.api.System.class, EntityReader.class,EntityCounter.class
    			, RuntimeQueryStringBuilder.class,TransientFieldsProcessor.class, Initializer.class,Validator.class);
    	
    	MapperClassGetter.MAP.put(FileDtoImpl.class, FileDtoImplMapper.class);
    	PersistenceEntityClassGetterImpl.MAP.put(FileDtoImpl.class,FileImpl.class);
    	DefaultProjectionsGetterImpl.MAP.put(File.class, List.of(FileImpl.FIELD_IDENTIFIER,FileImpl.FIELD_NAME_AND_EXTENSION));
    	DefaultSortOrdersGetterImpl.MAP.put(File.class, Map.of(FileImpl.FIELD_NAME,SortOrder.ASCENDING));
    	
    	createFileDirectory();
    	emptyFileDirectory();
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {               
        
    }
    
    void createFileDirectory() {
    	LogHelper.logInfo(String.format("Files will %sbe copied to directory <<%s>>.",configuration.file().copyToDirectory() ? "" : "not ",configuration.file().directory().name()), getClass());
    	if(!configuration.file().copyToDirectory())
    		return;
    	FILE_DIRECTORY = new java.io.File(configuration.file().directory().name());
    	if(!FILE_DIRECTORY.exists()) {
    		LogHelper.logInfo(String.format("Files directory <<%s>> does not exist. It will %sbe created.", FILE_DIRECTORY.toPath(),configuration.file().directory().creatable() ? "" : "not "), getClass());
    		if(configuration.file().directory().creatable()) {
    			try {
					Boolean result = FILE_DIRECTORY.mkdirs();
					LogHelper.logInfo(String.format("Files directory <<%s>> has %sbeen created.", FILE_DIRECTORY.toPath(),result ? "" : "not "), getClass());
				} catch (Exception exception) {
					LogHelper.logInfo(String.format("Files directory <<%s>> cannot be created.", FILE_DIRECTORY.toPath()), getClass());
					LogHelper.log(exception, getClass());
				}
    		}
    	}
    }
    
    void emptyFileDirectory() {
    	if(FILE_DIRECTORY == null || !FILE_DIRECTORY.exists())
    		return;
    	LogHelper.logInfo(String.format("Files directory <<%s>> content will %sbe deleted.",configuration.file().directory().name(),configuration.file().directory().emptyable() ? "" : "not "), getClass());
    	if(!configuration.file().directory().emptyable())
    		return;
    	List<Path> paths = (List<Path>) PathsScanner.getInstance().scan(new PathsScanner.Arguments().addPathsFromNames(configuration.file().directory().name()));
    	LogHelper.logInfo(String.format("%s files to delete.",CollectionHelper.getSize(paths)), getClass());
    	if(CollectionHelper.isEmpty(paths))
    		return;
    	Collection<Path> unDeletablePaths = new ArrayList<>();
    	paths.forEach(path -> {
    		if(!path.toFile().delete())
    			synchronized(unDeletablePaths) {
    				unDeletablePaths.add(path);
    			}
    	});
    	if(!unDeletablePaths.isEmpty()) {
    		LogHelper.logWarning(String.format("%s files cannot be deleted.",unDeletablePaths.size()), getClass());
    		unDeletablePaths.forEach(path -> {System.out.println("----------> "+path);});
    	}
    }
    
    void logConfiguration() {
    	configuration.tika().server().file().fetchers().forEach(fetcher -> {
    		System.out.println(fetcher.name()+" : "+fetcher.path().regularExpression());
    	});
    }
}