package org.cyk.system.file.server.impl.business;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;

import org.cyk.system.file.server.api.business.FileTextBusiness;
import org.cyk.system.file.server.api.persistence.FileText;
import org.cyk.utility.business.server.AbstractSpecificBusinessImpl;

@ApplicationScoped
public class FileTextBusinessImpl extends AbstractSpecificBusinessImpl<FileText> implements FileTextBusiness,Serializable {

}