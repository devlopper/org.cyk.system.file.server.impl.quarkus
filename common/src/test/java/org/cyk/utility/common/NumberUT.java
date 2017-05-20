package org.cyk.utility.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.common.helper.NumberHelper;
import org.cyk.utility.test.unit.AbstractUnitTest;
import org.junit.Test;
import org.mockito.InjectMocks;

public class NumberUT extends AbstractUnitTest {

	private static final long serialVersionUID = -6691092648665798471L;
	
	@InjectMocks private NumberHelper numberHelper;
	
	@Override
	protected void _execute_() {
		super._execute_();
		assertEquals("Create student. Class {} , Level {}", new LogMessage.Builder().set("Create","student","Class",5,"Level","B2").build().getTemplate());
	}
		
	@Test
	public void concatenate(){
		List<Long> numbers = Arrays.asList(7657L,7827L,7997L,8167L,8337L,8507L,8677L,8847L,9017L,9187L,9357L,9527L,9697L,9867L,10037L,10207L,10377L,10547L,10717L,10887L);
		assertConcatenate(Long.class, numbers, numbers.size()-1, "0765707827079970816708337085070867708847090170918709357095270969709867100371020710377105471071710887");
	}
	
	@Test
	public void base10ToBase16(){
		assertEncode16("15","F");
	}
	
	@Test
	public void base10ToBase36(){
		assertEncode36("15","F");
		assertEncode36("123","3F");
		assertEncode36("753159","G553");
		assertEncode36("8245691","4WQEZ");
		assertEncode36("123456789852","1KPQZGPO");	
	}
	
	@Test
	public void base10ToBase62(){
		assertEncode62("15","f");
		assertEncode62("9517345682523141","HAycT9quN");
		assertEncode62("951753456825231419517534568252","jXNmjZ0aNKC6Z25f6");
		assertEncode62("9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"			
				,"zpwSzgM2K7bJnGiuX23l6N9LhD93b9rbPHhpS0K7263tRYbM0sdamkZ7uT0k4rYhhnBgCEvxtHkV");
		                
		assertEncode62("9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				+"9517534568252314195175345682523141951753456825231419517534568252314195175345682523141951753456825231419517534568252314195175345682523141"
				
				,"ZxvsZDCkYRxD0Mr0UjYZNZWqW1Fm7wguRLCvIjWFROqtHZewPBnlMQpFjmy7tsDoMzB1XChlY2HDHTgqGLFH0erOolOlnteVTADRrepIJWbMTjxgTFV9v64lWcozqt2w4oACyXAGM"
				+ "4DHKAD6R65GjoaPaSZRznmzM46v05k9MyhC82DoCZXsdEQnpzMU5Z8ylJXqbCRfZAuPwHLvmplFUoRloeKSH31ZXZWDQDAFEcU5jlzaX1HA3qz2oqih364mnyAEIZ5HfVCfeuFBJL"
				+ "riMNLm6RPGXb08xnFSqpgCpLD8hfrEpMobXI9ZPLukQq2akPoATxtOX7CiHxP8lolfzy7ud3vyw2w4Bt2BJ0Uv7M2zHlu2W9vVuftyi1J2p9eRYfvqk4j37tZ0y0B1MRBWJLhS98O"
				+ "tEk3mYjY0qpzwvOIew3Qcl41YSxNcS1yvvD8T9msreP1oGi4WKyaUH7mODiBsKOgqvOImcn8TGi0VPX8OMy7ts6KenCBYCKmrOtxcq1zynFdg5bI9lq3QTF6UY1nsiQIA6fMbknpV"
				+ "XoKdE06R7xcrw487Yus0CEKnTeCrSz2H6VLIANJxgP2AT57ukyiJuzpBwmF");
		
		assertEncode62(StringUtils.repeat("85479632541", 20 * 50 * 5),"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");

	}
	
	/**/
	
	private <NUMBER extends Number> void assertConcatenate(Class<NUMBER> numberClass,List<NUMBER> numbers,Integer highestIndex,String expectedConcatenation){
		NUMBER highest = numberHelper.getHighest(numbers);
		assertEquals(numbers.get(highestIndex.intValue()), highest);
		String c = numberHelper.concatenate(numbers, highest.toString().length());
		assertEquals(expectedConcatenation, c);
		List<NUMBER> numbers2 = new ArrayList<>(numberHelper.deconcatenate(numberClass, c, highest.toString().length()));
		for(int i=0;i<numbers.size();i++)
			assertEquals(numbers.get(i), numbers2.get(i));
	}
	/*
	private <NUMBER extends Number> void assertFormatSequences(Class<NUMBER> numberClass,List<NUMBER> numbers,Collection<String> results){
		NUMBER highest = numberBusiness.findHighest(numbers);
		assertEquals(numbers.get(highestIndex.intValue()), highest);
		String c = numberBusiness.concatenate(numbers, highest.toString().length());
		assertEquals(expectedConcatenation, c);
		List<NUMBER> numbers2 = new ArrayList<>(numberBusiness.deconcatenate(numberClass, c, highest.toString().length()));
		for(int i=0;i<numbers.size();i++)
			assertEquals(numbers.get(i), numbers2.get(i));
	}*/
	
	private void assertEncode16(String number,String result){
		assertCoding(number, "16", numberHelper.encodeToBase16(number), result);
		//assertEquals(number+" to base 16" ,result, numberBusiness.encodeToBase16(number));
		assertEquals(result+" to base 10" ,number, numberHelper.decodeBase16(result));
	}
	
	private void assertEncode36(String number,String result){
		assertCoding(number, "36", numberHelper.encodeToBase36(number), result);
		//assertEquals(number+" to base 36" ,result, numberBusiness.encodeToBase36(number));
		assertEquals(result+" to base 10" ,number, numberHelper.decodeBase36(result));
	}
	
	private void assertEncode62(String number,String result){
		assertCoding(number, "62", numberHelper.encodeToBase62(number), result);
		//assertEquals(number+" to base 36" ,result, numberBusiness.encodeToBase36(number));
		assertEquals(result+" to base 10" ,number, numberHelper.decodeBase62(result));
	}
	
	private void assertCoding(String number,String base,String actualResult,String expectedResult){
		String message = number+" to base "+base;
		assertEquals(message,expectedResult, actualResult);
		Integer numberOfCharacterDifference = number.length()-expectedResult.length();
		BigDecimal ratio = new BigDecimal(numberOfCharacterDifference).divide(new BigDecimal(number.length()),2,RoundingMode.DOWN);
		System.out.println(number.length()+" characters to "+expectedResult.length()+". Difference : "+numberOfCharacterDifference+". Ratio = "+ratio);
	}
	
}
