package org.cyk.utility.common.model.table;

import java.lang.reflect.Field;
import java.util.List;


public interface ColumnListener<DIMENSION extends Column<DATA, CELL, VALUE>,DATA,CELL extends Cell<VALUE>,VALUE> extends DimensionListener<DIMENSION,DATA,CELL,VALUE> {
	
	Boolean isColumn(Field field);
	void populateFromDataClass(Class<?> aClass,List<Field> fields);
	void sort(List<Field> fields);
	
	/**/
	
	public static class Adapter<DIMENSION extends Column<DATA, CELL, VALUE>,DATA,CELL extends Cell<VALUE>,VALUE> extends DimensionListener.Adapter<DIMENSION, DATA, CELL, VALUE> 
		implements ColumnListener<DIMENSION,DATA,CELL,VALUE> {
	
		private static final long serialVersionUID = 1L;

		@Override
		public Boolean isColumn(Field field) {
			return null;
		}
	
		@Override
		public void populateFromDataClass(Class<?> aClass, List<Field> fields) {}
	
		@Override
		public void sort(List<Field> fields) {}
	
	}

}