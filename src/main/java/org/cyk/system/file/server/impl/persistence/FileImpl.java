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
import org.cyk.system.file.server.api.persistence.Parameters;
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
		,@NamedQuery(name = FileImpl.QUERY_READ_IDENTIFIER_BY_UNIFORM_RESOURCE_LOCATOR,query = "SELECT t."+FileImpl.FIELD_IDENTIFIER+" FROM "+FileImpl.ENTITY_NAME+" t WHERE t."+FileImpl.FIELD_UNIFORM_RESOURCE_LOCATOR+" = :"+Parameters.UNIFORM_RESOURCE_LOCATOR)
		,@NamedQuery(name = FileImpl.QUERY_READ_SHA1,query = "SELECT t."+FileImpl.FIELD_SHA1+" FROM "+FileImpl.ENTITY_NAME+" t WHERE t."+FileImpl.FIELD_SHA1+" IS NOT NULL ORDER BY t."+FileImpl.FIELD_SHA1+" ASC")
		,@NamedQuery(name = FileImpl.QUERY_READ_WHERE_SHA1_IS_NULL,query = "SELECT t FROM "+FileImpl.ENTITY_NAME+" t WHERE t."+FileImpl.FIELD_SHA1+" IS NULL ORDER BY t."+FileImpl.FIELD_IDENTIFIER+" ASC")
		,@NamedQuery(name = FileImpl.QUERY_READ_SHA1_HAVING_COUNT_SHA1_GREATER_THAN_ONE,query = "SELECT t.sha1 FROM FileImpl t WHERE t.sha1 IS NOT NULL GROUP BY t.sha1 HAVING COUNT(t.sha1) > 1")
		,@NamedQuery(name = FileImpl.QUERY_UPDATE_AUDITS_BY_IDENTIFIERS,query = "UPDATE FileImpl SET __auditIdentifier__ = :auditIdentifier,__auditFunctionality__ = :auditFunctionality,__auditWho__ = :auditWho,__auditWhat__ = :auditWhat,__auditWhen__ = :auditWhen WHERE identifier IN :identifiers")
		//,@NamedQuery(name = FileImpl.QUERY_DELETE_HAVING_COUNT_SHA1_GREATER_THAN_ONE,query = "DELETE FROM FileImpl t WHERE t.sha1 IN (SELECT t.sha1 FROM FileImpl t WHERE t.sha1 IS NOT NULL GROUP BY t.sha1 HAVING COUNT(t.sha1) > 1)")
		,@NamedQuery(name = FileImpl.QUERY_DELETE_BY_IDENTIFIERS,query = "DELETE FROM FileImpl t WHERE t.identifier IN :identifiers")
		,@NamedQuery(name = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS,query = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS_QUERY)
		,@NamedQuery(name = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS_BY_IDENTIFIERS,query = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS_QUERY+" AND t.identifier IN :identifiers")
		,@NamedQuery(name = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS,query = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS_QUERY+" ORDER BY t.size ASC")
		,@NamedQuery(name = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS_BY_IDENTIFIERS,query = FileImpl.QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS_QUERY+" AND t.identifier IN :identifiers ORDER BY t.size ASC")
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
	@NotNull @Column(name=COLUMN_MIME_TYPE,nullable = false,length=100) String mimeType;
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
	public static final String COLUMN_UNIFORM_RESOURCE_LOCATOR = "url";
	public static final String COLUMN_SIZE = "size";
	public static final String COLUMN_SHA1 = "sha1";
	
	public static final String COLUMN___AUDIT_IDENTIFIER__ = "AUDIT_IDENTIFIER";
	public static final String COLUMN___AUDIT_WHO__ = "AUDIT_ACTOR";
	public static final String COLUMN___AUDIT_WHAT__ = "AUDIT_ACTION";
	public static final String COLUMN___AUDIT_FUNCTIONALITY__ = "AUDIT_FUNCTIONALITY";
	public static final String COLUMN___AUDIT_WHEN__ = "AUDIT_DATE";
	
	public static final String QUERY_READ_IDENTIFIER_BY_UNIFORM_RESOURCE_LOCATOR = "FileImpl.readIdentifierByUniformResourceLocator";
	public static final String QUERY_READ_UNIFORM_RESOURCE_LOCATOR = "FileImpl.readUniformResourceLocator";
	public static final String QUERY_READ_SHA1 = "FileImpl.readSha1";
	public static final String QUERY_READ_WHERE_SHA1_IS_NULL = "FileImpl.readWhereSha1IsNull";
	public static final String QUERY_READ_SHA1_HAVING_COUNT_SHA1_GREATER_THAN_ONE = "FileImpl.readSha1HavingCountSha1GreaterThanOne";
	public static final String QUERY_UPDATE_AUDITS_BY_IDENTIFIERS = "FileImpl.updateAuditsByIdentifiers";
	public static final String QUERY_DELETE_BY_IDENTIFIERS = "FileImpl.deleteByIdentifiers";
	
	public static final String QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS = "FileImpl.readIdentifiersWhereBytesNotExists";
	public static final String QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS_BY_IDENTIFIERS = "FileImpl.readIdentifiersWhereBytesNotExistsByIdentifiers";
	public static final String QUERY_READ_IDENTIFIERS_WHERE_BYTES_NOT_EXISTS_QUERY = "SELECT t.identifier FROM FileImpl t WHERE NOT EXISTS(SELECT fb.identifier FROM FileBytesImpl fb WHERE fb.identifier = t.identifier)";
	
	public static final String QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS = "FileImpl.readIdentifiersWhereTextNotExists";
	public static final String QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS_BY_IDENTIFIERS = "FileImpl.readIdentifiersWhereTextNotExistsByIdentifiers";
	public static final String QUERY_READ_IDENTIFIERS_WHERE_TEXT_NOT_EXISTS_QUERY = "SELECT t.identifier FROM FileImpl t WHERE NOT EXISTS(SELECT ft.identifier FROM FileTextImpl ft WHERE ft.identifier = t.identifier)";
}