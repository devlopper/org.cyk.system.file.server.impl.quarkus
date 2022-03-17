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

import org.cyk.system.file.server.api.persistence.FileText;
import org.cyk.utility.persistence.entity.AbstractIdentifiableSystemScalarStringAuditedImpl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
@Entity(name = FileTextImpl.ENTITY_NAME) @Access(AccessType.FIELD)
@Table(name=FileTextImpl.TABLE_NAME)
@AttributeOverrides(value= {
		@AttributeOverride(name = FileTextImpl.FIELD___AUDIT_IDENTIFIER__,column = @Column(name=FileTextImpl.COLUMN___AUDIT_IDENTIFIER__,nullable = false))
		,@AttributeOverride(name = FileTextImpl.FIELD___AUDIT_WHO__,column = @Column(name=FileTextImpl.COLUMN___AUDIT_WHO__,nullable = false))
		,@AttributeOverride(name = FileTextImpl.FIELD___AUDIT_WHAT__,column = @Column(name=FileTextImpl.COLUMN___AUDIT_WHAT__,nullable = false))
		,@AttributeOverride(name = FileTextImpl.FIELD___AUDIT_WHEN__,column = @Column(name=FileTextImpl.COLUMN___AUDIT_WHEN__,nullable = false))
		,@AttributeOverride(name = FileTextImpl.FIELD___AUDIT_FUNCTIONALITY__,column = @Column(name=FileTextImpl.COLUMN___AUDIT_FUNCTIONALITY__,nullable = false))
})
public class FileTextImpl extends AbstractIdentifiableSystemScalarStringAuditedImpl implements FileText,Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull @Lob @Column(name=COLUMN_TEXT,nullable = false) private String text;
	
	public FileTextImpl() {}
	
	public FileTextImpl(String identifier,String text) {
		setIdentifier(identifier);
		setText(text);
	}
	
	/**/
	
	@Override
	public FileTextImpl setIdentifier(String identifier) {
		return (FileTextImpl) super.setIdentifier(identifier);
	}
	
	/**/
	public static final String FIELD_TEXT = "text";
	
	public static final String ENTITY_NAME = "FileTextImpl";
	public static final String TABLE_NAME = "at_file_text";
	
	public static final String COLUMN_TEXT = "text";
	
	public static final String COLUMN___AUDIT_IDENTIFIER__ = "AUDIT_IDENTIFIER";
	public static final String COLUMN___AUDIT_WHO__ = "AUDIT_ACTOR";
	public static final String COLUMN___AUDIT_WHAT__ = "AUDIT_ACTION";
	public static final String COLUMN___AUDIT_FUNCTIONALITY__ = "AUDIT_FUNCTIONALITY";
	public static final String COLUMN___AUDIT_WHEN__ = "AUDIT_DATE";
}