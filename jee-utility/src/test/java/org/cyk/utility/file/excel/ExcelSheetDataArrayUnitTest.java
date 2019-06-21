package org.cyk.utility.file.excel;

import java.io.InputStream;

import org.cyk.utility.array.ArrayInstanceTwoDimensionString;
import org.cyk.utility.test.weld.AbstractWeldUnitTest;
import org.junit.jupiter.api.Test;

public class ExcelSheetDataArrayUnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;

	@Test
	public void read() {
		FileExcelSheetDataArrayReader reader = __inject__(FileExcelSheetDataArrayReader.class);
		InputStream inputStream = getClass().getResourceAsStream("file01.xlsx");
		reader.setWorkbookInputStream(inputStream);
		reader.setSheetName("mysheet01");
		ArrayInstanceTwoDimensionString arrayInstance = reader.execute().getOutput();
		assertionHelper.assertEquals("Column01", arrayInstance.get(0, 0));
	}
	
	
}