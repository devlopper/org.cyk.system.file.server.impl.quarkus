package org.cyk.system.file.server.impl.persistence;
import org.cyk.utility.persistence.server.query.ArraysReaderByIdentifiers;

public abstract class AbstractFileImplReader extends ArraysReaderByIdentifiers.AbstractImpl.DefaultImpl<FileImpl> {

	@Override
	protected Class<FileImpl> getEntityClass() {
		return FileImpl.class;
	}
}