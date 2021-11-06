package org.cyk.quarkus.extension.hibernate.orm;

@javax.inject.Qualifier
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.PARAMETER })
public @interface Qualifier {

	public static class Class extends javax.enterprise.util.AnnotationLiteral<Qualifier> {
		private static final long serialVersionUID = 1L;
		
	}
	
}