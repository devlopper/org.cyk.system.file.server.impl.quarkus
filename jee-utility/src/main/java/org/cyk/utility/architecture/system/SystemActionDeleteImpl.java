package org.cyk.utility.architecture.system;

import java.io.Serializable;

public class SystemActionDeleteImpl extends AbstractSystemActionImpl implements SystemActionDelete, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		setIdentifier(IDENTIFIER);
	}
	
}
