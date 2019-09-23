package org.cyk.utility.client.controller.test;

import org.cyk.utility.client.controller.Controller;
import org.cyk.utility.collection.CollectionHelper;
import org.cyk.utility.field.FieldHelperImpl;
import org.cyk.utility.field.FieldName;
import org.cyk.utility.value.ValueUsageType;

public class TestControllerReadImpl extends AbstractTestControllerFunctionIntegrationImpl implements TestControllerRead {
	private static final long serialVersionUID = 1L;

	@Override
	protected void __perform__(Object object) throws Exception {
		Boolean mustUnexist =  Boolean.TRUE.equals(__inject__(CollectionHelper.class).contains(getUnexistingObjectIdentifiers(), object));
		ValueUsageType valueUsageType = getIdentifierValueUsageType();
		Object one = __inject__(Controller.class).readByIdentifier(getObjectClass(),object,valueUsageType);
		assertionHelper.assertEquals(getObjectClass()+" with "+valueUsageType+" identifier <"+object+">"+(mustUnexist ? "" : " not")+" found", !mustUnexist,one!=null);
		if(mustUnexist) {
			
		}else {
			assertionHelper.assertEquals(valueUsageType+" identitier do not match", object,FieldHelperImpl.__read__(one, FieldName.IDENTIFIER, valueUsageType));
		}
	}
	
}