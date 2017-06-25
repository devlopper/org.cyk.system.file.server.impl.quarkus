package org.cyk.utility.common;

import org.cyk.utility.common.message.SendMail;
import org.cyk.utility.test.unit.AbstractUnitTest;
import org.junit.Test;

public class SendMailUT extends AbstractUnitTest {

	private static final long serialVersionUID = -6691092648665798471L;
	
	@Test
	public void ping_kycdev_at_gmail_com(){
		new SendMail.Adapter.Default().setHostAndUserProperties("smtp.gmail.com", 465, "kycdev@gmail.com", "p@ssw0rd*").ping();
	}

}