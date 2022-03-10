package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.cyk.system.file.server.api.persistence.FileBytes;
import org.cyk.utility.persistence.entity.AbstractIdentifiableSystemScalarStringImpl;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
@Entity(name = FileBytesImpl.ENTITY_NAME) @Access(AccessType.FIELD)
@Table(name=FileBytesImpl.TABLE_NAME)
/**
 * Local physical storage of file content as bytes. <br/>
 * Because bytes can be large , this strategy has been used in order to decouple file properties and its content.
 * Also to avoid eager fetching or loading when querying.
 * @author CYK
 *
 */
public class FileBytesImpl extends AbstractIdentifiableSystemScalarStringImpl implements FileBytes,Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull @OneToOne @JoinColumn(name=COLUMN_FILE,unique=true) private FileImpl file;
	@NotNull @Lob @Column(name=COLUMN_BYTES) private byte[] bytes;
	
	/**/
	
	@Override
	public FileBytesImpl setIdentifier(String identifier) {
		return (FileBytesImpl) super.setIdentifier(identifier);
	}
	
	/**/
	public static final String FIELD_FILE = "file";
	public static final String FIELD_BYTES = "bytes";
	
	public static final String ENTITY_NAME = "FileBytes";
	public static final String TABLE_NAME = "at_file_bytes";
	
	public static final String COLUMN_FILE = "file";
	public static final String COLUMN_BYTES = "bytes";
}