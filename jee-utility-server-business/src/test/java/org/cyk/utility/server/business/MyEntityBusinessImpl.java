package org.cyk.utility.server.business;

import java.io.Serializable;

import javax.inject.Singleton;

import org.cyk.utility.__kernel__.properties.Properties;

@Singleton
public class MyEntityBusinessImpl extends AbstractBusinessEntityImpl<MyEntity,MyEntityPersistence> implements MyEntityBusiness,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public BusinessServiceProvider<MyEntity> create(MyEntity myEntity, Properties properties) {
		myEntity.setTimestamp(System.currentTimeMillis());
		return super.create(myEntity, properties);
	}

	@Override
	protected Class<MyEntity> __getEntityClass__() {
		return MyEntity.class;
	}
	
}
