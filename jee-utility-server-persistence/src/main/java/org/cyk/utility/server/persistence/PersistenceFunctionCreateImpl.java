package org.cyk.utility.server.persistence;

import java.io.Serializable;

import org.cyk.utility.architecture.system.SystemAction;

public class PersistenceFunctionCreateImpl extends AbstractPersistenceFunctionTransactionImpl implements PersistenceFunctionCreate, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __executeQuery__(SystemAction action,Object entity) {
		getEntityManager().persist(entity);		
	}

}
