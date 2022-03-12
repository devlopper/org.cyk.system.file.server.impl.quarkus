package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.collection.CollectionProcessor;
import org.cyk.utility.__kernel__.computation.ComparisonOperator;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.protocol.http.HttpHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Initializer;
import org.cyk.utility.business.Result;
import org.cyk.utility.business.server.AbstractSpecificBusinessImpl;
import org.cyk.utility.business.server.EntityCreator;
import org.cyk.utility.file.FileHelper;
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
	public Result download(String identifier) {
		Result result = new Result().open();
		FileValidator.validateDownloadInputs(identifier);
		FileImpl file = (FileImpl) persistence.readOne(identifier, List.of(FileImpl.FIELDS_UNIFORM_RESOURCE_LOCATOR_NAME_AND_EXTENSION_MIME_TYPE_SIZE_BYTES));
		result.setValue(file);	
		if(file == null) {
			LogHelper.logWarning(String.format("File <<%s>> to download not found", identifier), getClass());
			return null;
		}
		if(file.getBytes() == null) {
			file.setBytes(getBytes(file.getUniformResourceLocator(),result));
			if(file.getBytes() == null) {
				LogHelper.logWarning(String.format("Bytes of file <<%s>> to download not found", identifier), getClass());
				return null;
			}
		}
		// Return of message
		result.close().setName(String.format("Downloading of %s",identifier)).log(getClass());
		return result;
	}
	
	private byte[] getBytes(String uniformResourceLocator,Result result) {
		if(uniformResourceLocator.toLowerCase().startsWith("http"))
			return HttpHelper.get(uniformResourceLocator,byte[].class,null).body();
		try {
			return IOUtils.toByteArray(URI.create(uniformResourceLocator).toURL());
		} catch (Exception exception) {
			result.addMessages(exception.toString());
			return null;
			//throw new RuntimeException(exception);
		}
	}
	
	@Override @Transactional
	public Result computeSha1(String auditWho) {
		Result result = new Result().open();
		FileValidator.validateComputeSha1Inputs(auditWho);
		Collection<FileImpl> files = CollectionHelper.cast(FileImpl.class,persistence.readWhereSha1IsNull());
		
		String auditIdentifier = generateAuditIdentifier();
		LocalDateTime auditWhen = LocalDateTime.now();
		for(FileImpl file : files) {
			byte[] bytes = getBytes(file.getUniformResourceLocator(),result);
			if(bytes == null)
				continue;
			file.setSha1(FileHelper.computeSha1(bytes));
			audit(file, auditIdentifier, COMPUTE_SHA1_AUDIT_IDENTIFIER, auditWho, auditWhen);
			entityManager.merge(file);
		}

		// Return of message
		result.close().setName(String.format("Computation of sha1 of %s file(s)",files.size())).log(getClass());
		return result;
	}
	
	@Override
	public Result readDuplicatedSha1() {
		Result result = new Result().open();
		Collection<String> sha1s = persistence.readSha1HavingCountSha1GreaterThanOne();
		result.setValue(sha1s);
		// Return of message
		result.close().setName(String.format("Read duplicated sha1 : %s",CollectionHelper.getSize(sha1s))).log(getClass());
		return result;
	}
	
	@Override
	public Result countDuplicatedSha1() {
		Result result = new Result().open();
		Long count = NumberHelper.getLong(CollectionHelper.getSize(persistence.readSha1HavingCountSha1GreaterThanOne()));
		result.setValue(count);
		// Return of message
		result.close().setName(String.format("Count duplicated sha1 : %s",count)).log(getClass());
		return result;
	}
	
	@Override
	public Result countDuplicated() {
		Result result = new Result().open();
		
		return result;
	}
	
	@Override @Transactional
	public Result deleteDuplicated(String auditWho) {
		Result result = new Result().open();
		FileValidator.validateDeleteDuplicatedInputs(auditWho);
		Collection<File> files = persistence.readMany(new QueryExecutorArguments().addProjectionsFromStrings(FileImpl.FIELD_IDENTIFIER,FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR).addFilterFieldsValues(Parameters.DUPLICATED,Boolean.TRUE));
		if(CollectionHelper.isEmpty(files))
			return null;
		Collection<String> identifiers = files.stream().map(file -> file.getIdentifier()).collect(Collectors.toList());
		if(CollectionHelper.isEmpty(identifiers))
			return null;
		String auditIdentifier = generateAuditIdentifier();
		LocalDateTime auditWhen = LocalDateTime.now();
		
		persistence.updateAuditsByIdentifiers(identifiers, auditIdentifier, DELETE_DUPLICATED_AUDIT_IDENTIFIER, auditWho, auditWhen);
		Integer count = persistence.deleteByIdentifiers(identifiers);
		// Return of message
		result.close().setName(String.format("Deletion of duplicated : %s",count)).log(getClass());
		result.count(FileImpl.class, Action.DELETE, count);
		result.addMessages(String.format("%s duplicated file(s) deleted",count));
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
		if(minimalSize == null || minimalSize < 0)
			return this.minimalSize;
		return minimalSize;
	}
	
	Long normalizeMaximalSize(Long maximalSize,Long minimalSize) {
		if(NumberHelper.compare(minimalSize, maximalSize, ComparisonOperator.GTE))
			return this.maximalSize;
		return maximalSize;
	}
}