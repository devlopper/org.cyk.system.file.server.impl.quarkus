package org.cyk.system.file.server.impl.business;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.cyk.system.file.server.api.business.FileBusiness;
import org.cyk.system.file.server.api.business.FileTextBusiness;
import org.cyk.system.file.server.api.business.TextExtractor;
import org.cyk.system.file.server.api.persistence.File;
import org.cyk.system.file.server.api.persistence.FilePersistence;
import org.cyk.system.file.server.api.persistence.FileTextPersistence;
import org.cyk.system.file.server.api.persistence.Parameters;
import org.cyk.system.file.server.impl.configuration.Configuration;
import org.cyk.system.file.server.impl.persistence.FileBytesImpl;
import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.system.file.server.impl.persistence.FileImplUniformResourceLocatorExtensionMimeTypeReader;
import org.cyk.system.file.server.impl.persistence.FileImplUniformResourceLocatorMimeTypeBytesReader;
import org.cyk.system.file.server.impl.persistence.FileTextImpl;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.computation.ComparisonOperator;
import org.cyk.utility.__kernel__.computation.SortOrder;
import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.identifier.resource.UniformResourceLocatorHelper;
import org.cyk.utility.__kernel__.log.LogHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.cyk.utility.__kernel__.object.marker.AuditableWhoDoneWhatWhen;
import org.cyk.utility.__kernel__.object.marker.IdentifiableSystem;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.business.Initializer;
import org.cyk.utility.business.Result;
import org.cyk.utility.business.server.AbstractSpecificBusinessImpl;
import org.cyk.utility.file.FileHelper;
import org.cyk.utility.file.PathsScanner;
import org.cyk.utility.persistence.EntityManagerGetter;
import org.cyk.utility.persistence.entity.EntityLifeCycleListenerImpl;
import org.cyk.utility.persistence.query.QueryExecutorArguments;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.eventbus.EventBus;

@ApplicationScoped
public class FileBusinessImpl extends AbstractSpecificBusinessImpl<File> implements FileBusiness,Serializable{

	@Inject EntityManager entityManager;
	@Inject EventBus eventBus;
	@Inject @RestClient TikaClient tikaClient;
	@Inject @org.cyk.system.file.server.impl.Tika TextExtractor textExtractor;
	
	@Inject FilePersistence persistence;
	@Inject FileTextPersistence fileTextPersistence;
	@Inject FileTextBusiness fileTextBusiness;

	@Inject Configuration configuration;
	
	@Override
	public Result countInDirectories(Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize, Long maximalSize) {
		Result result = new Result().open();
		FileValidator.validateCountInDirectoriesInputs(configuration.file().directories().default_(),pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize);
		
		pathsNames = normalizePathsNames(pathsNames);
		acceptedPathNameRegularExpression = normalizeAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression);
		minimalSize = normalizeMinimalSize(minimalSize);
		maximalSize = normalizeMaximalSize(maximalSize, minimalSize);
		
		result.setValue(NumberHelper.getLong(PathsScanner.getInstance().count(new PathsScanner.Arguments().addPathsFromNames(pathsNames)
				.setAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression).setMinimalSize(minimalSize).setMaximalSize(maximalSize))));
		
		// Return of message
		return result.close();
	}
	
	@Override
	public Result import_(Collection<String> pathsNames, String acceptedPathNameRegularExpression,Long minimalSize,Long maximalSize,Boolean isDuplicateAllowed,String auditWho) {
		Result result = new Result().open();
		FileValidator.validateImportInputs(configuration.file().directories().default_(),pathsNames, acceptedPathNameRegularExpression, minimalSize, maximalSize,isDuplicateAllowed, auditWho);
		
		pathsNames = normalizePathsNames(pathsNames);
		acceptedPathNameRegularExpression = normalizeAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression);
		minimalSize = normalizeMinimalSize(minimalSize);
		maximalSize = normalizeMaximalSize(maximalSize, minimalSize);
		isDuplicateAllowed = normalizeIsDuplicateAllowed(isDuplicateAllowed);
		
		List<Path> paths = (List<Path>) PathsScanner.getInstance().scan(new PathsScanner.Arguments().addPathsFromNames(pathsNames).setAcceptedPathNameRegularExpression(acceptedPathNameRegularExpression)
				.setMinimalSize(minimalSize).setMaximalSize(maximalSize));
		
		Collection<File> files = new ArrayList<>();
		if(CollectionHelper.isNotEmpty(paths)) {
			Collection<String> existingsSha1 = new HashSet<>();
			if(Boolean.FALSE.equals(isDuplicateAllowed)) {
				CollectionHelper.add(existingsSha1,Boolean.TRUE,persistence.readSha1s());
			}
			
			Collection<String> existingsURLs = new HashSet<>();
			CollectionHelper.add(existingsURLs, Boolean.TRUE, persistence.readUniformResourceLocators());
			
			String auditIdentifier = generateAuditIdentifier();
			LocalDateTime auditWhen = LocalDateTime.now();
			
			Collection<String> directories = pathsNames;
			Boolean isDuplicateAllowedFinal = isDuplicateAllowed;
			
			// Filter paths to get only new one : uniform resource locator must be unique
			List<Path> lPaths = new ArrayList<>();
			for(Path path : paths) {
				String url = path.toFile().toURI().toString();
				if(existingsURLs.contains(url))
					continue;
				lPaths.add(path);
				existingsURLs.add(url);
			}
			
			new BatchProcessor.AbstractImpl<Path>() {
				@Override
				protected String getName() {
					return String.format("Importation from directories : %s - Paths count : %s|%s - Is sha1 computation parallel : %s", directories,lPaths.size(),paths.size(),configuration.file().sha1().computation().parallelized());
				}
				@Override
				protected void __process__(List<Path> paths, Integer batchsCount, Integer batchIndex, EntityManager entityManager) {
					import_(paths, existingsURLs, existingsSha1, isDuplicateAllowedFinal,files, result, auditIdentifier, auditWho, auditWhen, entityManager);
				}
				@Override
				protected Integer getSize() {
					return configuration.file().importation().batch().size();
				}
				@Override
				protected Integer getExecutorThreadCount() {
					return configuration.file().importation().executor().threadCount();
				}
				@Override
				protected Long getExecutorTimeoutDuration() {
					return configuration.file().importation().executor().timeout().duration();
				}
				@Override
				protected TimeUnit getExecutorTimeoutUnit() {
					return configuration.file().importation().executor().timeout().unit();
				}
			}.process(lPaths);
		}

		// Return of message
		result.close().setName(String.format("Importation of %s file(s)",files.size())).log(getClass());
		result.addMessages(String.format("Number of files imported : %s",files.size()));
		
		if(Boolean.TRUE.equals(configuration.file().text().extraction().runnableAfterCreation()))
			eventBus.request(EVENT_CHANNEL_EXTRACT_TEXT_OF_ALL, null);
		
		return result;
	}
	
	@Transactional
	void import_(Collection<Path> paths,Collection<String> existingsURLs,Collection<String> existingsSha1,Boolean isDuplicateAllowed,Collection<File> files,Result result,String auditIdentifier,String auditWho,LocalDateTime auditWhen,EntityManager entityManager) {
		//Filter paths to get only non duplicate : sha1 must be unique
		Map<Path,String> pathsSha1s = new TreeMap<>();
		final Map<Path,String> map = new LinkedHashMap<>();
		if(Boolean.FALSE.equals(isDuplicateAllowed)) {
			//Compute all sha1
			(configuration.file().sha1().computation().parallelized() ? paths.parallelStream() : paths.stream()).forEach(path -> {
				pathsSha1s.put(path, FileHelper.computeSha1(getBytes(path.toFile().toURI().toString(), result)));
			});

			pathsSha1s.forEach((path,sha1) -> {
				if(StringHelper.isBlank(sha1)) {
					result.addMessages(String.format("Unable to compte sha1 of %s", path));
					return;
				}
				
				if(existingsSha1.contains(sha1) || map.containsValue(sha1))
					return;

				map.put(path, sha1);
				synchronized(result) {	
					existingsSha1.add(sha1);
				}
			});
		}else
			paths.forEach(path -> map.put(path, null));

		//Instantiate
		map.forEach((path,sha1) -> {
			FileImpl file = new FileImpl();
			file.setIdentifier(IdentifiableSystem.generateRandomly());
			file.setNameAndExtension(path.toFile().getName());
			file.setSize(path.toFile().length());
			file.setUniformResourceLocator(path.toFile().toURI().toString());
			file.setSha1(sha1);
			Initializer.getInstance().initialize(FileImpl.class, file,IMPORT_AUDIT_IDENTIFIER);
			
			if(StringHelper.isBlank(file.getMimeType()))
				file.setMimeType(tikaClient.getMimeType(null, path.toFile().getName()));
			
			if(StringHelper.isBlank(file.getMimeType())) {
				result.addMessages(String.format("%s has no mime type", file.getUniformResourceLocator()));
				return;
			}
			audit(file, auditIdentifier, IMPORT_AUDIT_IDENTIFIER, auditWho, auditWhen);
			synchronized(result) {
				files.add(file);
			}
			entityManager.persist(file);	
		});
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
		result.close().setName(String.format("Downloading of %s",identifier)).setLogLevel(Level.FINE).log(getClass());
		return result;
	}
	
	static byte[] getBytes(String uniformResourceLocator,Result result) {
		try {
			return UniformResourceLocatorHelper.getBytes(uniformResourceLocator);
		} catch (Exception exception) {
			result.addMessages(exception.toString());
			return null;
		}
	}
	
	void computeSha1(Collection<File> files,String auditIdentifier,String auditFunctionality,String auditWho,LocalDateTime auditWhen,EntityManager entityManager,Result result) {
		if(CollectionHelper.isEmpty(files))
			return;
		for(File file : files) {
			byte[] bytes = getBytes(((FileImpl)file).getUniformResourceLocator(),result);
			if(bytes == null)
				continue;
			((FileImpl)file).setSha1(FileHelper.computeSha1(bytes));
			audit((AuditableWhoDoneWhatWhen) file, auditIdentifier, auditFunctionality, auditWho, auditWhen);
		}
		updateBatch(files, entityManager, null, null);
	}
	
	@Override @Transactional
	public Result deleteDuplicates(String auditWho) {
		Result result = new Result().open();
		FileValidator.validateDeleteDuplicatedInputs(auditWho);
		
		String auditIdentifier = generateAuditIdentifier();
		LocalDateTime auditWhen = LocalDateTime.now();
		
		computeSha1(persistence.readWhereSha1IsNull(), auditIdentifier, auditWho, auditWho, auditWhen, entityManager, result);
		
		Collection<File> files = persistence.readMany(new QueryExecutorArguments().setEntityManager(entityManager).addProjectionsFromStrings(FileImpl.FIELD_IDENTIFIER,FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR)
				.addFilterFieldsValues(Parameters.IS_A_DUPLICATE,Boolean.TRUE).setSortOrders(Map.of(FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR,SortOrder.ASCENDING)));

		Integer count = 0;
		if(CollectionHelper.isNotEmpty(files)) {
			Collection<String> identifiers = files.stream().map(file -> file.getIdentifier()).collect(Collectors.toList());
			
			persistence.updateAuditsByIdentifiers(identifiers, auditIdentifier, DELETE_DUPLICATES_AUDIT_IDENTIFIER, auditWho, auditWhen,entityManager);
			count = persistence.deleteByIdentifiers(identifiers,entityManager);
		}
		// Return of message
		result.close().setName(String.format("Deletion of duplicated : %s",count)).log(getClass());
		result.count(FileImpl.class, Action.DELETE, count);
		result.addMessages(String.format("%s duplicated file(s) deleted",count));
		return result;
	}
	
	/* Extract Bytes */
	
	@Override
	public Result extractBytes(Collection<String> identifiers,String auditWho) {
		return new BytesExtractorImpl().extract(identifiers, auditWho);
	}
	
	@Override
	public Result extractBytes(String auditWho, String... identifiers) {
		return extractBytes(ArrayHelper.isEmpty(identifiers) ? null : CollectionHelper.listOf(identifiers),auditWho);
	}
	
	@Override
	public Result extractBytesOfAll(String auditWho) {
		return new BytesExtractorImpl().extractOfAll(auditWho);
	}
	
	/* Extract Text */
	
	void extractText(Collection<String> identifiers,String auditIdentifier,String auditFunctionality, String auditWho,LocalDateTime auditWhen,Result result) {
		
	}
	
	@Override
	public Result extractText(Collection<String> identifiers, String auditWho) {
		Result result = new Result().open();
		FileValidator.validateExtractTextInputs(identifiers, auditWho);
		identifiers = entityManager.createNamedQuery(FileImpl.QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS_BY_IDENTIFIERS, String.class).setParameter("identifiers", identifiers).getResultList();
		Long count = fileTextPersistence.count();
		if(CollectionHelper.isNotEmpty(identifiers))
			extractText(identifiers, result, generateAuditIdentifier(), EXTRACT_TEXT_AUDIT_IDENTIFIER, auditWho, LocalDateTime.now());
		count = fileTextPersistence.count() - count;
		// Return of message
		result.close().setName(String.format("Extraction of file's text(%s) by %s",count,auditWho)).log(getClass());
		result.addMessages(String.format("file's text extracted : %s",count));
		return result;		
	}
	
	void extractText(Collection<String> identifiers,Result result,String auditIdentifier,String auditFunctionality, String auditWho,LocalDateTime auditWhen) {
		new BatchProcessor.AbstractImpl<String>() {
			@Override
			protected String getName() {
				return String.format("Extraction of file's text. %s", identifiers.size());
			}
			@Override
			protected void __process__(List<String> identifiers, Integer batchsCount, Integer batchIndex, EntityManager entityManager) {
				Collection<Object[]> arrays = new FileImplUniformResourceLocatorMimeTypeBytesReader().readByIdentifiers(identifiers, null);
				if(CollectionHelper.isNotEmpty(arrays))
					extractText(arrays, result, auditIdentifier, auditFunctionality, auditWho, auditWhen, entityManager);
			}
			@Override
			protected Integer getSize() {
				return 5;
			}
			@Override
			protected Integer getExecutorThreadCount() {
				return 4;
			}
			@Override
			protected Long getExecutorTimeoutDuration() {
				return 15l;
			}
			@Override
			protected TimeUnit getExecutorTimeoutUnit() {
				return TimeUnit.MINUTES;
			}
		}.process((List<String>)identifiers);
	}
	
	@Transactional
	void extractText(Collection<Object[]> arrays,Result result,String auditIdentifier,String auditFunctionality, String auditWho,LocalDateTime auditWhen,EntityManager entityManager) {
		arrays.forEach(array -> {
			String text = textExtractor.extract((byte[])array[3], (String)array[1], (String)array[2],result);
			if(StringHelper.isBlank(text))
				return;
			if(text.length() > configuration.file().text().maximalLength())
				text = text.substring(0, configuration.file().text().maximalLength().intValue());
			FileTextImpl fileText = new FileTextImpl().setIdentifier((String)array[0]).setText(text);
			audit(fileText, auditIdentifier, auditFunctionality, auditWho, auditWhen);
			entityManager.persist(fileText);
		});
	}
	
	
	@Override
	public Result extractText(String auditWho, String... identifiers) {
		return extractText(ArrayHelper.isEmpty(identifiers) ? null : CollectionHelper.listOf(identifiers),auditWho);
	}
	
	@Override
	public Result extractTextOfAll(String auditWho) {
		Result result = new Result().open();
		FileValidator.validateExtractTextAllInputs(auditWho);
		List<String> identifiers = entityManager.createNamedQuery(FileImpl.QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS, String.class).getResultList();
		Long count = fileTextPersistence.count();
		if(CollectionHelper.isNotEmpty(identifiers))
			extractText(identifiers, result, generateAuditIdentifier(), EXTRACT_TEXT_OF_ALL_AUDIT_IDENTIFIER, auditWho, LocalDateTime.now());
		count = fileTextPersistence.count() - count;
		// Return of message
		result.close().setName(String.format("Extraction of all file's text(%s) by %s",count,auditWho)).log(getClass());
		result.addMessages(String.format("All file's text extracted : %s",count));
		return result;		
	}
	
	void extractTextOfAllBySystem() {
		extractTextOfAll(EntityLifeCycleListenerImpl.SYSTEM_USER_NAME);
	}
	
	public static final String EVENT_CHANNEL_EXTRACT_TEXT_OF_ALL = "EXTRACT_TEXT_OF_ALL";
	@ConsumeEvent(EVENT_CHANNEL_EXTRACT_TEXT_OF_ALL)
    public void listenExtractTextOfAll(String message) {
		extractTextOfAllBySystem();
	}
	
	@Scheduled(cron = "{cyk.file.text.extraction.cron}")
	void extractTextOfAllAutomatically() {
		extractTextOfAllBySystem();
	}
	
	/**/
	
	Collection<String> normalizePathsNames(Collection<String> pathsNames) {
		if(CollectionHelper.isEmpty(pathsNames))
			return configuration.file().directories().default_();	
		return pathsNames;
	}
	
	String normalizeAcceptedPathNameRegularExpression(String acceptedPathNameRegularExpression) {
		if(StringHelper.isBlank(acceptedPathNameRegularExpression))
			return configuration.file().acceptedPath().name().regularExpression();
		return acceptedPathNameRegularExpression;
	}
	
	Long normalizeMinimalSize(Long minimalSize) {
		if(minimalSize == null || minimalSize < 0)
			return configuration.file().size().minimal();
		return minimalSize;
	}
	
	
	Long normalizeMaximalSize(Long maximalSize,Long minimalSize) {
		if(NumberHelper.compare(minimalSize, maximalSize, ComparisonOperator.GTE))
			return configuration.file().size().maximal();
		return maximalSize;
	}
	
	Boolean normalizeIsDuplicateAllowed(Boolean isDuplicateAllowed) {
		if(isDuplicateAllowed == null)
			return configuration.file().duplicate().allowed();
		return isDuplicateAllowed;
	}
	
	/**/
	
	public static interface BytesExtractor<T extends AuditableWhoDoneWhatWhen> {
		
		Result extract(Collection<String> identifiers,String auditWho);
		Result extract(String auditWho,String...identifiers);
		Result extractOfAll(String auditWho);
		
		/**/
		
		public static abstract class AbstractImpl<T extends AuditableWhoDoneWhatWhen> extends AbstractObject implements BytesExtractor<T>,Serializable {
			
			@Override
			public Result extract(Collection<String> identifiers,String auditWho) {
				Result result = new Result().open();
				FileValidator.validateExtractBytesInputs(identifiers, auditWho);
				Collection<String> unexistingIdentifiers = getEntityManager().createNamedQuery(getUnexistingByIdentifiersQueryIdentifier(), String.class).setParameter("identifiers", identifiers).getResultList();
				Collection<Object[]> arrays = CollectionHelper.isEmpty(unexistingIdentifiers) ? null : getArrays(unexistingIdentifiers);
				if(CollectionHelper.isNotEmpty(arrays)) {
					String auditIdentifier = getAuditIdentifier();
					LocalDateTime auditWhen = LocalDateTime.now();			
					extract(arrays, result, auditIdentifier, getAuditFunctionality(),auditWho, auditWhen, getEntityManager());
				}

				// Return of message
				result.close().setName(String.format("Extraction of %s file's %s by %s",CollectionHelper.getSize(arrays),getDataName(),auditWho)).log(getClass());
				result.addMessages(String.format("%s file's %s extracted",getDataName(),CollectionHelper.getSize(arrays)));
				return result;
			}
			
			void extract(Collection<Object[]> arrays,Result result,String auditIdentifier,String auditFunctionality,String auditWho,LocalDateTime auditWhen,EntityManager entityManager) {
				Collection<T> instances = new ArrayList<>();
				arrays.stream().parallel().forEach(array -> {
					byte[] bytes = getBytes(getUniformResourceLocator(array),result);
					if(bytes == null || bytes.length == 0)
						return;
					T instance = instantiate( (String)(array)[0],getUniformResourceLocator(array),bytes,getExtension(array),getMimeType(array),result);
					if(instance == null)
						return;
					audit(instance, auditIdentifier, auditFunctionality, auditWho, auditWhen);
					synchronized(instances) {
						instances.add(instance);
					}
				});
				if(Boolean.TRUE.equals(isTransactionNotManaged()))
					entityManager.getTransaction().begin();
				instances.forEach(instance -> {
					entityManager.persist(instance);
				});
				if(Boolean.TRUE.equals(isTransactionNotManaged()))
					entityManager.getTransaction().commit();
			}
			
			Integer extractOf(Collection<String> identifiers,Result result,String auditIdentifier,String auditFunctionality,String auditWho,LocalDateTime auditWhen,EntityManager entityManager) {
				Collection<Object[]> arrays = getArrays(identifiers);
				extract(arrays, result, auditIdentifier, auditFunctionality,auditWho, auditWhen, entityManager);
				return arrays.size();
			}
			
			@Override
			public Result extract(String auditWho, String... identifiers) {
				return extract(ArrayHelper.isEmpty(identifiers) ? null : CollectionHelper.listOf(identifiers),auditWho);
			}
			
			@Override
			public Result extractOfAll(String auditWho) {
				Result result = new Result().open();
				FileValidator.validateExtractBytesAllInputs(auditWho);
				
				String auditIdentifier = getAuditIdentifier();
				LocalDateTime auditWhen = LocalDateTime.now();
				
				List<String> identifiers = getEntityManager().createNamedQuery(getUnexistingQueryIdentifier(), String.class).getResultList();;
				Integer count = 0;
				if(CollectionHelper.isNotEmpty(identifiers)) {
					List<Integer> counts = new ArrayList<>();
					/*List<List<String>> batches = CollectionHelper.getBatches(identifiers, 5);
					batches.parallelStream().forEach(batch -> {
						Integer c = extractOf(batch, result, auditIdentifier, getAllAuditFunctionality(), auditWho, auditWhen, getEntityManager());
						synchronized(counts) {
							counts.add(c);
						}
					});
					*/
					identifiers.forEach(identifier -> {
						Integer c = extractOf(List.of(identifier), result, auditIdentifier, getAllAuditFunctionality(), auditWho, auditWhen, getEntityManager());
						synchronized(counts) {
							counts.add(c);
						}
					});
					
					for(Integer index : counts)
						count = count + index;
				}
				// Return of message
				result.close().setName(String.format("Extraction of all file's %s(%s) by %s",getDataName(),count,auditWho)).log(getClass());
				result.addMessages(String.format("%s file's %s extracted",getDataName(),count));
				return result;		
			}
			
			abstract T instantiate(String identifier,String uniformResourceLocator,byte[] bytes,String extension,String mimeType,Result result);
			
			String getAuditIdentifier() {
				return generateAuditIdentifier(FileImpl.class);
			}
			
			abstract String getDataName();
			
			abstract String getAuditFunctionality();
			
			abstract String getAllAuditFunctionality();
			
			EntityManager getEntityManager() {
				return EntityManagerGetter.getInstance().get();
			}
			
			Boolean isTransactionNotManaged() {
				return Boolean.TRUE;
			}
			
			FilePersistence getPersistence() {
				return DependencyInjection.inject(FilePersistence.class);
			}
			
			Collection<Object[]> getArrays(Collection<String> identifiers) {
				return new FileImplUniformResourceLocatorExtensionMimeTypeReader().readByIdentifiers(identifiers, null);
			}
			
			String getUniformResourceLocator(Object[] array) {
				return (String) array[1];
			}
			
			String getExtension(Object[] array) {
				return (String) array[2];
			}
			
			String getMimeType(Object[] array) {
				return (String) array[3];
			}
			
			abstract String getUnexistingByIdentifiersQueryIdentifier();
			abstract String getUnexistingQueryIdentifier();
		}
	}

	public class BytesExtractorImpl extends BytesExtractor.AbstractImpl<FileBytesImpl> implements Serializable {

		@Override
		String getDataName() {
			return "bytes";
		}
		
		@Override
		String getAuditFunctionality() {
			return EXTRACT_BYTES_AUDIT_IDENTIFIER;
		}
		
		@Override
		String getAllAuditFunctionality() {
			return EXTRACT_BYTES_OF_ALL_AUDIT_IDENTIFIER;
		}
		
		@Override
		FileBytesImpl instantiate(String identifier,String uniformResourceLocator, byte[] bytes,String extension,String mimeType,Result result) {
			return new FileBytesImpl(identifier,bytes);
		}
		
		@Override
		String getUnexistingByIdentifiersQueryIdentifier() {
			return FileImpl.QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS_BY_IDENTIFIERS;
		}
		
		@Override
		String getUnexistingQueryIdentifier() {
			return FileImpl.QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS;
		}
	}
	
	public static enum ResultKey {
		TEXT_EXTRACTOR
		;
		
		public static final String TEXT_EXTRACTOR_TEXT = "TEXT_EXTRACTOR_TEXT";
		public static final String TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION = "TEXT_EXTRACTOR_OPTICAL_CHARACTER_RECOGNITION";
		public static final String TEXT_EXTRACTOR_OTHERS = "TEXT_EXTRACTOR_OTHERS";
		public static final String TEXT_EXTRACTOR_FETCH = "TEXT_EXTRACTOR_FETCH";
	}
}