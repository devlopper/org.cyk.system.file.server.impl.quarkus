package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.collection.CollectionProcessor;
import org.cyk.utility.__kernel__.computation.ComparisonOperator;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.protocol.http.HttpHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Initializer;
import org.cyk.utility.business.Result;
import org.cyk.utility.business.server.AbstractSpecificBusinessImpl;
import org.cyk.utility.business.server.EntityCreator;
import org.cyk.utility.file.PathsProcessor;
import org.cyk.utility.file.PathsScanner;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FileBusinessImpl extends AbstractSpecificBusinessImpl<File> implements FileBusiness,Serializable{

	@Inject EntityManager entityManager;
	@Inject FilePersistence persistence;

	public static final String DEFAULT_DIRECTORIES = "data/files";
	@ConfigProperty(name = "default.directories",defaultValue = DEFAULT_DIRECTORIES)
	List<String> directories;
	
	public static final String DEFAULT_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION = ".pdf|.txt";
	@ConfigProperty(name = "accepted.path.name.regular.expression",defaultValue = DEFAULT_ACCEPTED_PATH_NAME_REGULAR_EXPRESSION)
	String acceptedPathNameRegularExpression;
	
	public static final String DEFAULT_MINIMAL_FILE_SIZE_AS_STRING = "1";
	public static final Long DEFAULT_MINIMAL_FILE_SIZE_AS_LONG = Long.valueOf(DEFAULT_MINIMAL_FILE_SIZE_AS_STRING);
	@ConfigProperty(name = "minimal.file.size",defaultValue = DEFAULT_MINIMAL_FILE_SIZE_AS_STRING)
	Long minimalSize;
	
	public static final String DEFAULT_MAXIMAL_FILE_SIZE_AS_STRING = "1048576";
	public static final Long DEFAULT_MAXIMAL_FILE_SIZE_AS_LONG = Long.valueOf(DEFAULT_MAXIMAL_FILE_SIZE_AS_STRING);
	@ConfigProperty(name = "maximal.file.size",defaultValue = DEFAULT_MAXIMAL_FILE_SIZE_AS_STRING)
	Long maximalSize;
	
	List<String> emptyPathsNames;
	
	@Override
	public Result import_(Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,String auditWho) {
		Result result = new Result().open();
		FileValidator.validateImportInputs(directories,pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize, auditWho);
		
		pathsNames = normalizePathsNames(pathsNames);
		acceptedPathNameRegularExpression = normalizeAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression);
		minimalSize = normalizeMinimalSize(minimalSize);
		maximalSize = normalizeMaximalSize(maximalSize, minimalSize);
		
		Collection<Path> paths = PathsScanner.getInstance().scan(new PathsScanner.Arguments().addPathsFromNames(pathsNames).setAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression)
				.setMinimalSize(minimalSize).setMaximalSize(maximalSize));
		
		Collection<String> existingsURLs = new HashSet<>();
		CollectionHelper.add(existingsURLs, Boolean.TRUE, persistence.readUniformResourceLocators());
		
		Collection<FileImpl> files = new ArrayList<>();
		PathsProcessor.getInstance().process(paths,new CollectionProcessor.Arguments.Processing.AbstractImpl<Path>() {
			@Override
			protected void __process__(Path path) {
				String url = path.toFile().toURI().toString();
				if(existingsURLs.contains(url))
					return;
				//Instantiate
				FileImpl file = new FileImpl();
				file.setNameAndExtension(path.toFile().getName());
				file.setSize(path.toFile().length());
				file.setUniformResourceLocator(path.toFile().toURI().toString());
				Initializer.getInstance().initialize(FileImpl.class, file,IMPORT_AUDIT_IDENTIFIER);
				if(StringHelper.isBlank(file.getMimeType())) {
					result.addMessages(String.format("%s has no mime type", url));
					return;
				}
				if(StringHelper.isNotBlank(file.getUniformResourceLocator()))
					existingsURLs.add(url);
				files.add(file);
			}
		});
		
		String auditIdentifier = generateAuditIdentifier();
		LocalDateTime auditWhen = LocalDateTime.now();
		files.forEach(file -> {
			audit(file, auditIdentifier, IMPORT_AUDIT_IDENTIFIER, auditWho, auditWhen);
		});
		
		EntityCreator.getInstance().create(new QueryExecutorArguments().setObjects(CollectionHelper.cast(Object.class, files)));
		
		// Return of message
		result.close().setName(String.format("Importation of %s file(s)",files.size())).log(getClass());
		result.addMessages(String.format("Number of files imported : %s",files.size()));
		return result;
	}
	
	@Override
	public Result countInDirectories(Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize, Long maximalSize) {
		Result result = new Result().open();
		FileValidator.validateCountInDirectoriesInputs(directories,pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize);
		
		pathsNames = normalizePathsNames(pathsNames);
		acceptedPathNameRegularExpression = normalizeAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression);
		minimalSize = normalizeMinimalSize(minimalSize);
		maximalSize = normalizeMaximalSize(maximalSize, minimalSize);
		
		result.setValue(NumberHelper.getLong(PathsScanner.getInstance().count(new PathsScanner.Arguments().addPathsFromNames(pathsNames)
				.setAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression).setMinimalSize(minimalSize).setMaximalSize(maximalSize))));
		
		// Return of message
		result.close().setName("Count in directories").log(getClass());
		result.addMessages(String.format("Number of files in directories : %s", result.getValue()));
		return result;
	}
	
	@Override
	public Result download(String identifier) {
		Result result = new Result().open();
		FileValidator.validateGetBytesInputs(identifier);
		FileImpl file = (FileImpl) persistence.readOne(identifier, List.of(FileImpl.FIELDS_UNIFORM_RESOURCE_LOCATOR_NAME_AND_EXTENSION_MIME_TYPE_SIZE_BYTES));
		if(file != null) {
			result.setValue(file);
			if(file.getBytes() == null && StringHelper.isNotBlank(file.getUniformResourceLocator())) {
				if(file.getUniformResourceLocator().toLowerCase().startsWith("http"))
					file.setBytes(HttpHelper.get(file.getUniformResourceLocator(),byte[].class,null).body());
				else
					try {
						file.setBytes(IOUtils.toByteArray(URI.create(file.getUniformResourceLocator()).toURL()));
					} catch (Exception exception) {
						throw new RuntimeException(exception);
					}
			}
			if(file.getBytes() == null)
				LogHelper.logWarning(String.format("File bytes of <<%s>> to download not found", identifier), getClass());
		}else
			LogHelper.logWarning(String.format("File <<%s>> to download not found", identifier), getClass());
		if(file == null || file.getBytes() == null)
			return null;
		// Return of message
		result.close().setName(String.format("Downloading of %s",identifier)).log(getClass());
		return result;
	}
	
	/**/
	
	Collection<String> normalizePathsNames(Collection<String> pathsNames) {
		if(CollectionHelper.isEmpty(pathsNames))
			return directories;	
		return pathsNames;
	}
	
	String normalizeAcceptedPathNameRegularExpression(String acceptedPathNameRegularExpression) {
		if(StringHelper.isBlank(acceptedPathNameRegularExpression))
			return this.acceptedPathNameRegularExpression;
		return acceptedPathNameRegularExpression;
	}
	
	Long normalizeMinimalSize(Long minimalSize) {
		if(NumberHelper.isLessThanOrEqualZero(minimalSize))
			return this.minimalSize;
		return minimalSize;
	}
	
	Long normalizeMaximalSize(Long maximalSize,Long minimalSize) {
		if(NumberHelper.compare(minimalSize, maximalSize, ComparisonOperator.GTE))
			return this.maximalSize;
		return maximalSize;
	}
}