package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @org.cyk.system.file.server.api.System @Unremovable
public class EntityReaderImpl extends org.cyk.quarkus.extension.hibernate.orm.EntityReaderImpl implements Serializable {

}