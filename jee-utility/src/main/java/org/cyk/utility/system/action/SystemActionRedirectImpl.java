package org.cyk.utility.system.action;

import java.io.Serializable;

public class SystemActionRedirectImpl extends AbstractSystemActionImpl implements SystemActionRedirect, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		setIdentifier(IDENTIFIER);
	}
	
}
