package org.cyk.system.file.server.impl.business;
import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.file.server.impl.persistence.FileImpl;
import org.cyk.utility.__kernel__.klass.ClassHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.throwable.ThrowablesMessages;
import org.cyk.utility.business.Validator;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @org.cyk.system.file.server.api.System @Unremovable
public class ValidatorImpl extends Validator.AbstractImpl implements Serializable {

	@Override
	protected <T> void __validate__(Class<T> klass, T entity, Object actionIdentifier,ThrowablesMessages throwablesMessages) {
		super.__validate__(klass, entity, actionIdentifier, throwablesMessages);
		if(Boolean.TRUE.equals(ClassHelper.isInstanceOf(klass, FileImpl.class)))
			validate(actionIdentifier, (FileImpl) entity, throwablesMessages);
	}
	
	/**/
	
	private void validate(Object actionIdentifier,FileImpl file,ThrowablesMessages throwablesMessages) {
		throwablesMessages.addIfTrue("file bytes or uri is required",(file.getBytes() == null || file.getBytes().length == 0)
				&& StringHelper.isBlank(file.getUniformResourceLocator()));
		throwablesMessages.addIfTrue("mime type is required", StringHelper.isBlank(file.getMimeType()));
		throwablesMessages.addIfTrue("size is required",file.getSize() == null);
		throwablesMessages.addIfTrue(String.format("size <<%s>> must be greater than 0",file.getSize()),NumberHelper.isLessThanOrEqualZero(file.getSize()));
	}
}