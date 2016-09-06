package org.cyk.utility.common.model.table;

import java.util.Collection;

import org.cyk.utility.common.computation.DataReadConfiguration;

public interface RowListener<DIMENSION extends Row<DATA, CELL, VALUE>,DATA,CELL extends Cell<VALUE>,VALUE> extends DimensionListener<DIMENSION,DATA,CELL,VALUE> {

	Collection<DATA> load(DataReadConfiguration configuration);
	Long count(DataReadConfiguration configuration);
	
	/**/
	
	public static class Adapter<DIMENSION extends Row<DATA, CELL, VALUE>,DATA,CELL extends Cell<VALUE>,VALUE> extends DimensionListener.Adapter<DIMENSION, DATA, CELL, VALUE> 
		implements RowListener<DIMENSION,DATA,CELL,VALUE> {

		private static final long serialVersionUID = 1L;

		@Override
		public Collection<DATA> load(DataReadConfiguration configuration) {
			return null;
		}
	
		@Override
		public Long count(DataReadConfiguration configuration) {
			return null;
		}
	
	
	}
}