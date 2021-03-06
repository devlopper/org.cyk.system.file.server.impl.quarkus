package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.cyk.system.file.server.api.persistence.File;
import org.cyk.utility.persistence.entity.AbstractIdentifiableSystemScalarStringAuditedImpl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true) 
@Entity(name = FileImpl.ENTITY_NAME) @Access(AccessType.FIELD)
@Table(name=FileImpl.TABLE_NAME)
@AttributeOverrides(value= {
		@AttributeOverride(name = FileImpl.FIELD___AUDIT_IDENTIFIER__,column = @Column(name=FileImpl.COLUMN___AUDIT_IDENTIFIER__,nullable = false))
		,@AttributeOverride(name = FileImpl.FIELD___AUDIT_WHO__,column = @Column(name=FileImpl.COLUMN___AUDIT_WHO__,nullable = false))
		,@AttributeOverride(name = FileImpl.FIELD___AUDIT_WHAT__,column = @Column(name=FileImpl.COLUMN___AUDIT_WHAT__,nullable = false))
		,@AttributeOverride(name = FileImpl.FIELD___AUDIT_WHEN__,column = @Column(name=FileImpl.COLUMN___AUDIT_WHEN__,nullable = false))
		,@AttributeOverride(name = FileImpl.FIELD___AUDIT_FUNCTIONALITY__,column = @Column(name=FileImpl.COLUMN___AUDIT_FUNCTIONALITY__,nullable = false))
})
@NamedQueries(value = {
		@NamedQuery(name = FileImpl.QUERY_READ_UNIFORM_RESOURCE_LOCATOR,query = "SELECT t."+FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR+" FROM "+FileImpl.ENTITY_NAME+" t ORDER BY t."+FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR+" ASC")
})
public class FileImpl extends AbstractIdentifiableSystemScalarStringAuditedImpl implements File,Serializable{

	/**
	 * Physical location. Can be local (file:///) or remote (ftp, http, ...)
	 */
	@Column(name=COLUMN_UNIFORM_RESOURCE_LOCATOR,unique=true) String uniformResourceLocator;
	
	/** 
	 * Logical name
	 */
	@NotNull
	@Column(name=COLUMN_NAME,nullable = false) 
	//@FullTextField(projectable = Projectable.YES)
	private String name;
	
	/* 
	 * Derived informations. Those informations can be derived from bytes or collected from inputed file.
	 * */
	
	@Column(name=COLUMN_EXTENSION,length=10) String extension;
	@NotNull @Column(name=COLUMN_MIME_TYPE,nullable = false,length=50) String mimeType;
	@NotNull @Column(name=COLUMN_SIZE,nullable = false) Long size;
	@Column(name=COLUMN_SHA1,length=40) String sha1;

	/**/
	
	@Transient private Boolean isBytesPersistableOnCreate;
	@Transient private Boolean isTextPersistableOnCreate;
	
	@Transient private String nameAndExtension;
	@Transient private byte[] bytes;
	@Transient private Boolean isBytesAccessibleFromUniformResourceLocator;
	@Transient private String text;
	
	/**/
	
	@Override
	public FileImpl setIdentifier(String identifier) {
		return (FileImpl) super.setIdentifier(identifier);
	}
	
	@Override
	public String toString() {
		return identifier+":"+name+":"+extension+":"+mimeType+":"+size+":"+nameAndExtension;
	}
	
	/**/
	
	public static final String FIELD_NAME = "name";
	public static final String FIELD_BYTES = "bytes";
	public static final String FIELD_TEXT = "text";
	public static final String FIELD_EXTENSION = "extension";
	public static final String FIELD_NAME_AND_EXTENSION = "nameAndExtension";
	public static final String FIELD_NAME_AND_EXTENSION_MIME_TYPE_SIZE = "nameAndExtensionMimeTypeSize";
	public static final String FIELD_MIME_TYPE = "mimeType";
	public static final String FIELD_UNIFORM_RESOURCE_LOCATOR = "uniformResourceLocator";
	public static final String FIELD_SIZE = "size";
	public static final String FIELD_SHA1 = "sha1";
	public static final String FIELDS_UNIFORM_RESOURCE_LOCATOR_NAME_AND_EXTENSION_MIME_TYPE_SIZE_BYTES = "uniformResourceLocatorNameAndExtensionMimeTypeSizeBytes";
	
	public static final String ENTITY_NAME = "FileImpl";
	public static final String TABLE_NAME = "at_file";
	
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EXTENSION = "extension";
	public static final String COLUMN_MIME_TYPE = "mime";
	public static final String COLUMN_UNIFORM_RESOURCE_LOCATOR = "uri";
	public static final String COLUMN_SIZE = "size";
	public static final String COLUMN_SHA1 = "sha1";
	
	public static final String COLUMN___AUDIT_IDENTIFIER__ = "AUDIT_IDENTIFIER";
	public static final String COLUMN___AUDIT_WHO__ = "AUDIT_ACTOR";
	public static final String COLUMN___AUDIT_WHAT__ = "AUDIT_ACTION";
	public static final String COLUMN___AUDIT_FUNCTIONALITY__ = "AUDIT_FUNCTIONALITY";
	public static final String COLUMN___AUDIT_WHEN__ = "AUDIT_DATE";
	
	public static final String QUERY_READ_UNIFORM_RESOURCE_LOCATOR = "FileImpl.readUniformResourceLocator";
}