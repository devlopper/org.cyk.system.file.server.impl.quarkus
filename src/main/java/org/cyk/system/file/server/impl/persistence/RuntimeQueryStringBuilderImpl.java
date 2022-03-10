package org.cyk.system.file.server.impl.persistence;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.utility.persistence.server.query.string.RuntimeQueryStringBuilder;

import io.quarkus.arc.Unremovable;

@ApplicationScoped @org.cyk.system.file.server.api.System @Unremovable
public class RuntimeQueryStringBuilderImpl extends RuntimeQueryStringBuilder.AbstractImpl implements Serializable{

}