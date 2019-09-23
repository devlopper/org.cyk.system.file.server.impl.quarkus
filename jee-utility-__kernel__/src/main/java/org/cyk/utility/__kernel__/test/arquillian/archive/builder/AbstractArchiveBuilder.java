package org.cyk.utility.__kernel__.test.arquillian.archive.builder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.maven.pom.Dependency;
import org.cyk.utility.__kernel__.maven.pom.Pom;
import org.cyk.utility.__kernel__.maven.pom.PomBuilderImpl;
import org.cyk.utility.__kernel__.maven.pom.Profile;
import org.cyk.utility.__kernel__.object.AbstractObject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class AbstractArchiveBuilder<ARCHIVE extends Archive<?>> extends AbstractObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private Class<ARCHIVE> clazz;
	private ARCHIVE archive;
	
	private Boolean isBeanXmlAddable = Boolean.TRUE;
	private String beanXml,projectDefaultsYml,persistenceXml,ormXml,log4j2Xml,log4jProperties,pomXml,jbossDeploymentStructureXml;
	private Collection<Package> packages;
	private Collection<Clazz> classes;
	private String mavenPomProfileIdentifier;
	
	public AbstractArchiveBuilder(Class<ARCHIVE> clazz) {
		this.clazz = clazz;
		//archive = ShrinkWrap.create(this.clazz);
		//if(archive instanceof JavaArchive)
		//	((JavaArchive)archive).addPackages(Boolean.TRUE,"org.cyk.utility");
	}
	
	public ARCHIVE execute(){
		__logFinest__("Building archive starts");
		if(StringUtils.isBlank(mavenPomProfileIdentifier))
			mavenPomProfileIdentifier = "org.cyk.test";
		Profile profile = Pom.INSTANCE.getProfile(mavenPomProfileIdentifier);
		if(profile == null){
			__logFinest__("No test profile found under id "+mavenPomProfileIdentifier);
		}
		
		if(StringUtils.isBlank(pomXml) && profile!=null)
			pomXml = profile.getProperty("org.cyk.test.maven.pom.file");
		if(StringUtils.isBlank(pomXml))
			pomXml = "pom.xml";
		if(!StringUtils.equalsIgnoreCase("pom.xml", pomXml))
			__logFinest__("Pom : "+pomXml);
		
		if(StringUtils.isBlank(beanXml) && profile!=null)
			beanXml = profile.getProperty("org.cyk.test.cdi.beans.file");
		
		if(StringUtils.isBlank(projectDefaultsYml) && profile!=null)
			projectDefaultsYml = profile.getProperty("org.cyk.test.swarm.project.defaults.file");
		
		if(StringUtils.isBlank(persistenceXml) && profile!=null)
			persistenceXml = profile.getProperty("org.cyk.test.jpa.persistence.file");
		
		if(StringUtils.isBlank(persistenceXml) && profile!=null)
			log4jProperties = profile.getProperty("org.cyk.test.logging.log4j.file");
		
		if(StringUtils.isBlank(ormXml) && profile!=null)
			ormXml = profile.getProperty("org.cyk.test.jpa.orm.file");
		
		if(StringUtils.isBlank(jbossDeploymentStructureXml) && profile!=null)
			jbossDeploymentStructureXml = profile.getProperty("org.cyk.test.jboss.deployment.structure.file");
		
		String _package = profile == null ? null : profile.getProperty("org.cyk.test.package");
		String[] classesArray = profile == null ? null : StringUtils.isBlank(profile.getProperty("org.cyk.test.classes")) ? null : profile.getProperty("org.cyk.test.classes").split(",");
		String[] resourcesFoldersArray = profile == null ? null : StringUtils.isBlank(profile.getProperty("org.cyk.test.resources.folders")) ? null : profile.getProperty("org.cyk.test.resources.folders").split(",");
		
		archive = ShrinkWrap.create(this.clazz);
		if(Boolean.TRUE.equals(isBeanXmlAddable)) {
			//if(beanXml == null || beanXml.isEmpty())
			//	beanXml = String.format(BEAN_XML_EMPTY_FORMAT,"all");
			addBeanXml(beanXml);
		}
		if(projectDefaultsYml!=null)
			addProjectDefaultsYml(projectDefaultsYml);
		if(persistenceXml!=null)
			addPersistenceXml(persistenceXml);
		if(ormXml!=null)
			addOrmXml(ormXml);
		if(log4j2Xml!=null)
			addlog4j2Xml(log4j2Xml);
		if(log4jProperties!=null)
			addlog4jProperties(log4jProperties);
		if(pomXml!=null)
			addPomXml(pomXml,profile);
		if(jbossDeploymentStructureXml!=null)
			addJbossDeploymentStructureXml(jbossDeploymentStructureXml);
		
		if(StringUtils.isNotBlank(_package)){
			if(archive instanceof WebArchive)
				((WebArchive)archive).addPackages(Boolean.TRUE, _package);
		}
		
		String[] suffixes = {"Persistence","PersistenceImpl","Business","BusinessImpl","Representation","RepresentationImpl","Impl","Dto","DtoCollection"
				,"Mapper","DtoMapper","MapperImpl","DtoMapperImpl"
				,"AssertionsProvider","AssertionsProviderImpl"};
		Set<Class<?>> classes = new HashSet<>();
		if(classesArray!=null)
			for(String index : classesArray){
				if(index.contains(".persistence.entities.")) {
					if(index.endsWith("y")) {
						__addClass__(StringUtils.substringBeforeLast(index, "y")+"ies", classes);
						__addClass__(StringUtils.substringBeforeLast(index, "y")+"iesImpl", classes);
					}else {
						__addClass__(index+"s", classes);
						__addClass__(index+"sImpl", classes);	
					}
					
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".persistence.api.")+"Persistence", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".persistence.impl.")+"PersistenceImpl", classes);
					
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".business.api.")+"Business", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".business.impl.")+"BusinessImpl", classes);
					
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".business.api.")+"AssertionsProvider", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".business.api.")+"AssertionsProviderImpl", classes);
					
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".representation.entities.")+"Dto", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".representation.entities.")+"DtoCollection", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".representation.entities.")+"DtoMapper", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".representation.entities.")+"DtoMapperImpl", classes);
					
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".representation.api.")+"Representation", classes);
					__addClass__(StringUtils.replaceOnce(index, ".persistence.entities.", ".representation.impl.")+"RepresentationImpl", classes);
				}
				
				try {
					Class<?> aClass = Class.forName(index);
					classes.add(aClass);
					if(!StringUtils.endsWithAny(aClass.getName(), suffixes)) {
						for(String suffix : suffixes)
							try {
								Class<?> relatedClass = Class.forName(aClass.getName()+suffix);
								classes.add(relatedClass);
							} catch (Exception e) {}
					}
					//if(archive instanceof WebArchive)
					//	((WebArchive)archive).addClass(aClass);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		if(classes!=null && !classes.isEmpty()) {
			__logFinest__("#classes derived : "+classes.size());
			if(archive instanceof WebArchive)
				((WebArchive)archive).addClasses(classes.toArray(new Class<?>[] {}));
		}
		
		/* Resources */
		
		Set<String> resources = new HashSet<>();
		if(resourcesFoldersArray!=null) {
			String javaResourceFolder = System.getProperty("user.dir")+"\\src\\test\\resources\\";
			for(String indexDirectory : resourcesFoldersArray){
				for(File indexFile : FileUtils.listFiles(new File(javaResourceFolder+indexDirectory), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE))
					resources.add(StringUtils.substringAfter(indexFile.getPath(), javaResourceFolder));
			}
		}
		if(resources!=null && !resources.isEmpty()) {
			__logFinest__("#resources derived : "+resources.size());
			for(String index : resources) {
				index = StringUtils.replace(index, "\\", "/");
				((WebArchive) archive).addAsResource(index,index);
			}
		}
		
		if(profile!=null) {
			String[] files = StringUtils.split(profile.getProperty("org.cyk.test.web.inf.resources"),",");
			if(files!=null)
				for(String index : files) {
					String name = StringUtils.contains(index, "/") ? StringUtils.substringAfterLast(index, "/") : index;
					((WebArchive) archive).addAsWebInfResource(index,name);
				}
		}
		
		Collection<Package> packages = getPackages();
		if(packages!=null) {
			for(Package index : packages)
				((ClassContainer<?>)archive).addPackages(index.getIsRecursive(), index.getName());
		}
		
		Collection<Clazz> clazzes = getClasses();
		if(clazzes!=null) {
			for(Clazz index : clazzes)
				if(index.getValue() == null)
					((ClassContainer<?>)archive).addClass(index.getName());
				else
					((ClassContainer<?>)archive).addClass(index.getValue());
		}
		
		__logFinest__("Building archive done.");
		return archive;
	}
	
	private void __addClass__(String name,Collection<Class<?>> classes) {
		try {
			Class<?> klass = Class.forName(name);
			classes.add(klass);
		} catch (Exception e) {}
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addBeanXml(Object beansXml){
		if(beansXml == null){
			if(archive instanceof JavaArchive)
				((JavaArchive)archive).addAsManifestResource(EmptyAsset.INSTANCE,"beans.xml");
			else if(archive instanceof WebArchive){
				//((WebArchive)archive).addAsManifestResource(EmptyAsset.INSTANCE,"beans.xml");
				((WebArchive)archive).addAsWebInfResource(EmptyAsset.INSTANCE,"beans.xml");
			}
		}else{
			if(beansXml instanceof String)
				if(archive instanceof JavaArchive) {
					if(((String) beansXml).endsWith(".xml"))
						((JavaArchive)archive).addAsManifestResource((String) beansXml,"beans.xml");
					else {
						try {
							File file = File.createTempFile("beanXml", null);
							Files.write(file.toPath(), ((String)beansXml).getBytes(), StandardOpenOption.APPEND);
							((JavaArchive)archive).addAsManifestResource(file,"beans.xml");
						} catch (IOException exception) {
							exception.printStackTrace();
						}
					}
				} else if(archive instanceof WebArchive){
					((WebArchive)archive).addAsManifestResource((String) beansXml,"beans.xml");
					((WebArchive)archive).addAsWebInfResource((String) beansXml,"beans.xml");
					//((WebArchive)archive).addAsResource((String) beansXml,"META-INF/beans.xml");
				}
		}
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addJbossDeploymentStructureXml(String path){
		((WebArchive)archive).addAsWebInfResource(path,"jboss-deployment-structure.xml");
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addPersistenceXml(String path){
		((WebArchive)archive).addAsResource(path,"META-INF/persistence.xml");
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addOrmXml(String path){
		((WebArchive)archive).addAsResource(path,"META-INF/orm.xml");
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addProjectDefaultsYml(String path){
		((WebArchive)archive).addAsResource(path,"project-defaults.yml");
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addlog4j2Xml(String path){
		((WebArchive)archive).addAsResource(path,"log4j2.xml");
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addlog4jProperties(String path){
		((WebArchive)archive).addAsResource(path,"log4j.properties");
		return this;
	}
	
	private AbstractArchiveBuilder<ARCHIVE> addPomXml(String path,Profile profile){
		try {
			path = System.getProperty("user.dir")+"/"+path;
			Pom pom = PomBuilderImpl.__execute__(path);// xml == null ? null : (Pom) unmarshaller.unmarshal(new StringReader(xml));
			if(pom == null){
				__logWarning__("Pom not found : "+path);
			}else {
				String version = pom.getVersion() == null ? pom.getParent().getVersion() : pom.getVersion();
				//Collection<File> files = new ArrayList<>();
				File[] dependencies = Maven.resolver().resolve(pom.getGroupId()+":"+pom.getArtifactId()+":"+version).withTransitivity().asFile();
				Collection<Dependency> profileDependencies = profile == null ? null : profile.getDependenciesCollection();
				if(profileDependencies!=null)
					for(Dependency index : profileDependencies) {
						dependencies = (File[]) ArrayUtils.add(dependencies, Maven.resolver().resolve(index.getGroupId()+":"+index.getArtifactId()+":"+index.getVersion()).withoutTransitivity().asSingleFile());
					}
				//File[] dependencies = Resolvers.use(MavenResolverSystem.class).loadPomFromFile(path).importRuntimeAndTestDependencies().resolve().withTransitivity().asFile();
				
				if(archive instanceof WebArchive)
					((WebArchive)archive).addAsLibraries(dependencies);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addPackages(Collection<Package> packages) {
		if(packages!=null && !packages.isEmpty()) {
			if(this.packages == null)
				this.packages = new ArrayList<Package>();
			this.packages.addAll(packages);
		}
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addPackages(Package...packages) {
		if(packages!=null && packages.length>0) {
			Collection<Package> collection = new ArrayList<Package>();
			for(Package index : packages)
				collection.add(index);
			addPackages(collection);
		}
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addPackage(String name,Boolean isRecursive) {
		addPackages(new Package().setName(name).setIsRecursive(isRecursive));
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addPackage(String name) {
		addPackage(name,Boolean.FALSE);
		return this;
	}
	
	/**/
	
	public AbstractArchiveBuilder<ARCHIVE> addClasses(Collection<Clazz> classes) {
		if(classes!=null && !classes.isEmpty()) {
			if(this.classes == null)
				this.classes = new ArrayList<Clazz>();
			this.classes.addAll(classes);
		}
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addClasses(Clazz...classes) {
		if(classes!=null && classes.length>0) {
			Collection<Clazz> collection = new ArrayList<Clazz>();
			for(Clazz index : classes)
				collection.add(index);
			addClasses(collection);
		}
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addClasses(String name) {
		addClasses(new Clazz().setName(name));
		return this;
	}
	
	public AbstractArchiveBuilder<ARCHIVE> addClass(Class<?> aClass) {
		addClasses(new Clazz().setValue(aClass));
		return this;
	}
	
	/**/
	
	//private static final String BEAN_XML_EMPTY_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
	//		"<beans version=\"1.1\" bean-discovery-mode=\"%s\"></beans>";
	
	//private static final String BEAN_XML_EMPTY_FORMAT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
	//		"<beans xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
	//		"	xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://docs.jboss.org/cdi/beans_1_1.xsd\" version=\"1.1\" bean-discovery-mode=\"%s\">\r\n" + 
	//		"</beans> ";
	
}