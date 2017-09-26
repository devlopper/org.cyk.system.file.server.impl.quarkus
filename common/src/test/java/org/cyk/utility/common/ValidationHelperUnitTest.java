package org.cyk.utility.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javax.validation.constraints.NotNull;

import org.cyk.utility.common.helper.StringHelper;
import org.cyk.utility.common.helper.ThrowableHelper;
import org.cyk.utility.common.helper.ValidationHelper;
import org.cyk.utility.test.unit.AbstractUnitTest;
import org.hibernate.validator.constraints.Email;
import org.junit.Test;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class ValidationHelperUnitTest extends AbstractUnitTest {

	private static final long serialVersionUID = -6691092648665798471L;
		
	static {
		StringHelper.ToStringMapping.Datasource.Adapter.Default.initialize();
		
	}
	
	@Test
    public void manyConstraints() {
		new Try(ThrowableHelper.ThrowableMarker.class,"1 ##field.f.1## : ne peut pas être nul"+Constant.LINE_DELIMITER+"2 ##field.f.2## : ne peut pas être nul"
				+Constant.LINE_DELIMITER+"3 ##field.f.3## : ne peut pas être nul"+Constant.LINE_DELIMITER+"4 ##field.f.4## : ne peut pas être nul"){ 
			private static final long serialVersionUID = -8176804174113453706L;
			@Override protected void code() {new ValidationHelper.Validate.Adapter.Default(new ManyConstraints())
				.setIsThrowMessages(Boolean.TRUE)
    			.execute();}
		}.execute();
	}
	
	@Test
    public void manyConstraintsOtherOrder() {
		
	}
	
	@Test
    public void electronicMailFormatMessage() {
		assertList(new ArrayList<>(new ValidationHelper.Validate.Adapter.Default(new ElectronicMail("a..@mail.com"))
    			.execute()), Arrays.asList("adresse : a..@mail.com n'est pas une adresse de courrier électronique bien formée"));
		assertList(new ArrayList<>(new ValidationHelper.Validate.Adapter.Default(new ElectronicMail("a..@mail.com")).setIsFieldNameIncludedInMessage(false)
    			.execute()), Arrays.asList("a..@mail.com n'est pas une adresse de courrier électronique bien formée"));
		assertList(new ArrayList<>(new ValidationHelper.Validate.Adapter.Default(new ElectronicMail("a..@mail.com")).setIsFieldNameIncludedInMessage(true)
    			.execute()), Arrays.asList("adresse : a..@mail.com n'est pas une adresse de courrier électronique bien formée"));
    }

	@Test(expected=RuntimeException.class)
    public void electronicMailFormatThrowMessage() {
    	new ValidationHelper.Validate.Adapter.Default(new ElectronicMail("a..@mail.com")).setIsThrowMessages(Boolean.TRUE).execute();
    }
	
	@Test
    public void electronicMailFormatMessageCustom() {
		assertList(new ArrayList<>(new ValidationHelper.Validate.Adapter.Default(new ElectronicMailCustomMessage("a..@mail.com")).execute())
				, Arrays.asList("adresse : ##pas vraiment bon##"));
    }
    
    /**/
    
    @Getter @Setter @Accessors(chain=true)
    public static class ElectronicMail implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	@Email @NotNull
		private String address;
    	
    	public ElectronicMail() {}
    	
    	public ElectronicMail(String address) {
    		this.address = address;
    	}
    }
    
    @Getter @Setter @Accessors(chain=true)
    public static class ElectronicMailCustomMessage implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	@Email(message="{pas vraiment bon}") @NotNull
		private String address;
    	
    	public ElectronicMailCustomMessage() {}
    	
    	public ElectronicMailCustomMessage(String address) {
    		this.address = address;
    	}
    }
	
    @Getter @Setter @Accessors(chain=true)
    public static class ManyConstraints implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	@NotNull private String f1;
    	@NotNull private String f2;
    	@NotNull private String f3;
    	@NotNull private String f4;
    	
    }
    
    @Getter @Setter @Accessors(chain=true)
    public static class ManyConstraintsOtherOrder implements Serializable {
    	private static final long serialVersionUID = 1L;
    	
    	@NotNull private String f1;
    	@NotNull private String f3;
    	@NotNull private String f2;
    	@NotNull private String f5;
    	@NotNull private String f4;
    	
    }
}
