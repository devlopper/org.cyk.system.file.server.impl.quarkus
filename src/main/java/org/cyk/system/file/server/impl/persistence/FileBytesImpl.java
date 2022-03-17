package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.cyk.system.file.server.api.persistence.FileBytes;
import org.cyk.utility.persistence.entity.AbstractIdentifiableSystemScalarStringAuditedImpl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
@Entity(name = FileBytesImpl.ENTITY_NAME) @Access(AccessType.FIELD)
@Table(name=FileBytesImpl.TABLE_NAME)
@AttributeOverrides(value= {
		@AttributeOverride(name = FileBytesImpl.FIELD___AUDIT_IDENTIFIER__,column = @Column(name=FileBytesImpl.COLUMN___AUDIT_IDENTIFIER__,nullable = false))
		,@AttributeOverride(name = FileBytesImpl.FIELD___AUDIT_WHO__,column = @Column(name=FileBytesImpl.COLUMN___AUDIT_WHO__,nullable = false))
		,@AttributeOverride(name = FileBytesImpl.FIELD___AUDIT_WHAT__,column = @Column(name=FileBytesImpl.COLUMN___AUDIT_WHAT__,nullable = false))
		,@AttributeOverride(name = FileBytesImpl.FIELD___AUDIT_WHEN__,column = @Column(name=FileBytesImpl.COLUMN___AUDIT_WHEN__,nullable = false))
		,@AttributeOverride(name = FileBytesImpl.FIELD___AUDIT_FUNCTIONALITY__,column = @Column(name=FileBytesImpl.COLUMN___AUDIT_FUNCTIONALITY__,nullable = false))
})
/**
 * Local physical storage of file content as bytes. <br/>
 * Because bytes can be large , this strategy has been used in order to decouple file properties and its content.
 * Also to avoid eager fetching or loading when querying.
 * @author CYK
 *
 */
public class FileBytesImpl extends AbstractIdentifiableSystemScalarStringAuditedImpl implements FileBytes,Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull @Lob @Column(name=COLUMN_BYTES,nullable = false) private byte[] bytes;
	
	/**/
	
	public FileBytesImpl() {}
	
	public FileBytesImpl(String identifier,byte[] bytes) {
		setIdentifier(identifier);
		setBytes(bytes);
	}
	
	@Override
	public FileBytesImpl setIdentifier(String identifier) {
		return (FileBytesImpl) super.setIdentifier(identifier);
	}
	
	/**/
	public static final String FIELD_BYTES = "bytes";
	
	public static final String ENTITY_NAME = "FileBytesImpl";
	public static final String TABLE_NAME = "at_file_bytes";
	
	public static final String COLUMN_BYTES = "bytes";
	
	public static final String COLUMN___AUDIT_IDENTIFIER__ = "AUDIT_IDENTIFIER";
	public static final String COLUMN___AUDIT_WHO__ = "AUDIT_ACTOR";
	public static final String COLUMN___AUDIT_WHAT__ = "AUDIT_ACTION";
	public static final String COLUMN___AUDIT_FUNCTIONALITY__ = "AUDIT_FUNCTIONALITY";
	public static final String COLUMN___AUDIT_WHEN__ = "AUDIT_DATE";
}