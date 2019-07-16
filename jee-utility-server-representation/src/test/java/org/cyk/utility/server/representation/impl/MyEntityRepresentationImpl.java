package org.cyk.utility.server.representation.impl;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.server.business.api.MyEntityBusiness;
import org.cyk.utility.server.persistence.entities.MyEntity;
import org.cyk.utility.server.representation.AbstractRepresentationEntityImpl;
import org.cyk.utility.server.representation.api.MyEntityRepresentation;
import org.cyk.utility.server.representation.entities.MyEntityDto;
import org.cyk.utility.server.representation.entities.MyEntityDtoCollection;

@ApplicationScoped
public class MyEntityRepresentationImpl extends AbstractRepresentationEntityImpl<MyEntity,MyEntityBusiness,MyEntityDto,MyEntityDtoCollection> implements MyEntityRepresentation,Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public Class<MyEntity> getPersistenceEntityClass() {
		return MyEntity.class;
	}
	
}
