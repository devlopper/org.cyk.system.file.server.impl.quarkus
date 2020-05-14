package org.cyk.utility.network;

import java.util.Date;
import java.util.UUID;

import org.cyk.utility.network.protocol.ProtocolDefaults;
import org.cyk.utility.security.Credentials;
import org.cyk.utility.stream.distributed.Consumer;
import org.cyk.utility.stream.distributed.ConsumerBuilder;
import org.cyk.utility.stream.distributed.Messages;
import org.cyk.utility.stream.distributed.Producer;
import org.cyk.utility.stream.distributed.ProducerBuilder;
import org.cyk.utility.stream.distributed.Topic;
import org.cyk.utility.test.weld.AbstractWeldUnitTest;
import org.cyk.utility.time.TimeHelper;
import org.junit.jupiter.api.Test;

public class MailProducerConsumerUnitTest extends AbstractWeldUnitTest {
	private static final long serialVersionUID = 1L;
	
	//@Test
	public void produceAndConsume() {
		if(Boolean.TRUE.equals(__isRunnable__(Producer.class))) {
			__inject__(ProtocolDefaults.class).getSimpleMailTransfer().setHost("smtp.gmail.com").setPort(587).setIsAuthenticationRequired(Boolean.TRUE)
			.setIsSecuredConnectionRequired(Boolean.TRUE)
			.setAuthenticationCredentials(__inject__(Credentials.class).setIdentifier("dgbfdtideveloppers").setSecret("dgbf2016dti"));
			
			startServersZookeeperAndKafka();
			
			Consumer consumer = __inject__(ConsumerBuilder.class).setTopic(Topic.MAIL).execute().getOutput();
			
			//Testing purpose
			consumer.setIsKeepMessages(Boolean.TRUE).setNumberOfMessages(1l).setIsExecuteAsynchronously(Boolean.TRUE);
			Thread thread = (Thread) consumer.execute().getProperties().getThread();
			
			System.out.println("Mail consumer has started. we are waiting some times before producing...");
			__inject__(TimeHelper.class).pause(1000l * 5);
			
			org.cyk.utility.network.message.Message mail = __inject__(org.cyk.utility.network.message.Message.class);
			mail.setTitle("Hi from distributed! Time was "+new Date()).setBody("This is a hi from distributed stream.").addReceiversByIdentifiers("kycdev@gmail.com");
			
			Producer producer = __inject__(ProducerBuilder.class).setTopic(Topic.MAIL).execute().getOutput();
			producer.setMessage(UUID.randomUUID().toString(),mail);		
			producer.execute();
			
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			Messages messages = consumer.getMessages();
			assertionHelper.assertNotNull("messages is null",messages);
			assertionHelper.assertTrue("no message has been read",messages.getSize()>0);
			assertionHelper.assertEquals(mail.getTitle(), ((org.cyk.utility.network.message.Message)consumer.getMessages().getLast().getValue()).getTitle());
			assertionHelper.assertEquals(mail.getBody(), ((org.cyk.utility.network.message.Message)consumer.getMessages().getLast().getValue()).getBody());
			
			stopServersKafkaAndZookeeper();	
		}
	}
	
	@Test
	public void produceAndMailShouldHaveBeenSent() {
		if(Boolean.TRUE.equals(__isRunnable__(Producer.class))) {
			__inject__(ProtocolDefaults.class).getSimpleMailTransfer().setHost("smtp.gmail.com").setPort(587).setIsAuthenticationRequired(Boolean.TRUE)
			.setIsSecuredConnectionRequired(Boolean.TRUE)
			.setAuthenticationCredentials(__inject__(Credentials.class).setIdentifier("dgbfdtideveloppers").setSecret("dgbf2016dti"));
			
			startServersZookeeperAndKafka();
			
			Topic.MAIL.startConsumer();
			
			System.out.println("Mail consumer has started. we are waiting some times before producing...");
			__inject__(TimeHelper.class).pause(1000l * 5);
			
			__inject__(MailHelper.class).produce("Hi from distributed stream! Time was "+new Date(), "This is a hi from distributed stream.", "kycdev@gmail.com");
		
			//Get message from POP
			/*
			Messages messages = consumer.getMessages();
			assertionHelper.assertNotNull("messages is null",messages);
			assertionHelper.assertTrue("no message has been read",messages.getSize()>0);
			assertionHelper.assertEquals(mail.getTitle(), ((org.cyk.utility.network.message.Message)consumer.getMessages().getLast().getValue()).getTitle());
			assertionHelper.assertEquals(mail.getBody(), ((org.cyk.utility.network.message.Message)consumer.getMessages().getLast().getValue()).getBody());
			*/
			
			__inject__(TimeHelper.class).pause(1000l * 15);
			
			stopServersKafkaAndZookeeper();	
		}
	}
	
}