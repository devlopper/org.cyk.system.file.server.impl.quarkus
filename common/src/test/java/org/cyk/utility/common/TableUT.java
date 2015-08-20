package org.cyk.utility.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.cyk.utility.common.model.table.DefaultCell;
import org.cyk.utility.common.model.table.DefaultColumn;
import org.cyk.utility.common.model.table.DefaultRow;
import org.cyk.utility.common.model.table.DefaultTable;
import org.cyk.utility.common.model.table.TableAdapter;
import org.cyk.utility.test.unit.AbstractUnitTest;
import org.junit.Assert;
import org.junit.Test;


public class TableUT extends AbstractUnitTest {

	private static final long serialVersionUID = -6691092648665798471L;
	
	@Test
	public void simple(){
		DefaultTable<RowData> table = new DefaultTable<>(RowData.class);
		table.getTableListeners().add(new TableAdapter<DefaultRow<RowData>, DefaultColumn, RowData, String, DefaultCell, String>(){
			
		});
		
		table.addColumnFromDataClass();
		
		table.addRow(new RowData("Yao"));
		table.addRow(new RowData("Zadi"));
		table.addRow(new RowData("Kodom"));
		
		table.build();
		
		Assert.assertEquals("Row 1",0,table.getRows().get(0).getUiIndex().intValue());
		Assert.assertEquals("Row 2",1,table.getRows().get(1).getUiIndex().intValue());
		Assert.assertEquals("Row 3",2,table.getRows().get(2).getUiIndex().intValue());
	}
	
	@Test
	public void complex1(){
		DefaultTable<RowData> table = new DefaultTable<>(RowData.class);
		table.getTableListeners().add(new TableAdapter<DefaultRow<RowData>, DefaultColumn, RowData, String, DefaultCell, String>(){
			@Override
			public Boolean isCountable(DefaultRow<RowData> row) {
				return !row.getData().getValue().equals("Total");
			}
		});
		table.addColumnFromDataClass();
		
		table.addRow(new RowData("Yao"));
		table.addRow(new RowData("Zadi"));
		table.addRow(new RowData("Total"));
		table.addRow(new RowData("N1"));
		table.addRow(new RowData("N2"));
		table.addRow(new RowData("Kodom"));
		table.addRow(new RowData("Total"));
		
		table.build();
		
		Assert.assertEquals("Row 1",0,table.getRows().get(0).getUiIndex().intValue());
		Assert.assertEquals("Row 2",1,table.getRows().get(1).getUiIndex().intValue());
		Assert.assertNull("Row 3",table.getRows().get(2).getUiIndex());
		Assert.assertEquals("Row 4",2,table.getRows().get(3).getUiIndex().intValue());
		Assert.assertEquals("Row 5",3,table.getRows().get(4).getUiIndex().intValue());
		Assert.assertEquals("Row 6",4,table.getRows().get(5).getUiIndex().intValue());
		Assert.assertNull("Row 7",table.getRows().get(6).getUiIndex());
	}
	/*
	@Test
	public void complex2(){
		DefaultTable<RowData> table = new DefaultTable<>(RowData.class);
		DefaultRow<RowData> row = null;
		table.addColumnFromDataClass();
		
		table.addRow(new RowData("Yao"));
		table.addRow(new RowData("Zadi"));
		row = table.addRow(new RowData("Total"));
		row.setType(DimensionType.SUMMARY);
		table.addRow(new RowData("N1"));
		table.addRow(new RowData("N2"));
		table.addRow(new RowData("Kodom"));
		row = table.addRow(new RowData("Total"));
		row.setType(DimensionType.SUMMARY);
			
		table.build();
		
		Assert.assertEquals("Row 1",0,table.getRows().get(0).getUiIndex().intValue());
		Assert.assertEquals("Row 2",1,table.getRows().get(1).getUiIndex().intValue());
		Assert.assertNull("Row 3",table.getRows().get(2).getUiIndex());
		Assert.assertEquals("Row 4",2,table.getRows().get(3).getUiIndex().intValue());
		Assert.assertEquals("Row 5",3,table.getRows().get(4).getUiIndex().intValue());
		Assert.assertEquals("Row 6",4,table.getRows().get(5).getUiIndex().intValue());
		Assert.assertNull("Row 7",table.getRows().get(6).getUiIndex());
	}*/
	
	@Getter @Setter @AllArgsConstructor
	private static class RowData{
		private String value;
	}
	
}
