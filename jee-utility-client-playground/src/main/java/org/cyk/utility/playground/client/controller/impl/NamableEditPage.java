package org.cyk.utility.playground.client.controller.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityEditPageContainerManagedImpl;
import org.cyk.utility.playground.client.controller.entities.Namable;

import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class NamableEditPage extends AbstractEntityEditPageContainerManagedImpl<Namable> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected Collection<String> __getEntityFieldsNames__() {
		return List.of("code","name");
	}
	
}
