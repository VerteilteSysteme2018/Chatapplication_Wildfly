package edu.hm.dako.chat.sandboxBenchmarking;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import edu.hm.dako.chat.client.AbstractMessageListenerThread;
import edu.hm.dako.chat.client.ClientUserInterface;
import edu.hm.dako.chat.client.SharedClientData;
import edu.hm.dako.chat.client.SimpleMessageListenerThreadImpl;
import edu.hm.dako.chat.common.ChatPDU;
import edu.hm.dako.chat.connection.Connection;

public class SimpleJMSMessageListenerThread extends AbstractMessageListenerThread {

	private static Log log = LogFactory.getLog(SimpleMessageListenerThreadImpl.class);
	
	private String threadName;
	private int numberOfMessagesToSend;
	
	public SimpleJMSMessageListenerThread(ClientUserInterface userInterface, Connection con, SharedClientData sharedData, String threadName, int numberOfMessagesToSend) {
		
		super(userInterface, con, sharedData);
		this.threadName = threadName;
		this.numberOfMessagesToSend = numberOfMessagesToSend;
	}
	
	public void run() {
		
		client = new ChatClientJMS(userInterface, "jms", "8080", "127.0.0.1");
		JMSContext context = client.getJMSContext();
		Destination topic = client.getTopic();
		JMSConsumer consumer = context.createConsumer(topic);
		
		boolean messageCheck = true;
		while(messageCheck == true) {
			
			String messages = consumer.receiveBody(String.class, 5000);
					
			if(messages != null) {
			
				Gson gson = new Gson();
	 			JsonObject json = gson.fromJson(messages, JsonObject.class);
	 			
	 			String name = json.get("userName").getAsString();
	 			String message = json.get("message").getAsString();
 				
				chatMessageEventAction(name);
	 			chatMessageResponseAction(name);
			}
			
			
//			if(sharedClientData.messageCounter.get() == numberOfMessagesToSend) {
//				messageCheck = false;
//			}
		}
	}

	@Override
	protected void chatMessageResponseAction(ChatPDU receivedPdu) {
		// nothing to do for JMS implementation
		
	}

	@Override
	protected void chatMessageEventAction(ChatPDU receivedPdu) {
		// nothing to do for JMS implementation
		
	}

	@Override
	protected void loginResponseAction(ChatPDU receivedPdu) {
		// nothing to do for JMS implementation
		
	}

	@Override
	protected void loginEventAction(ChatPDU receivedPdu) {
		// nothing to do for JMS implementation
	}

	@Override
	protected void logoutEventAction(ChatPDU receivedPdu) {
		// nothing to do for JMS implementation
	}

	@Override
	protected void logoutResponseAction(ChatPDU receivedPdu) {

		sharedClientData.eventCounter.getAndIncrement();
		int events = SharedClientData.logoutEvents.incrementAndGet();

		log.debug("LogoutEventCounter: " + events);
	}

	@Override
	protected void chatMessageResponseAction(String user) {

		userInterface.setLastServerTime(System.currentTimeMillis() / 1000000);
		userInterface.setLock(false);
		
	}

	@Override
	protected void chatMessageEventAction(String user) {

		sharedClientData.eventCounter.getAndIncrement();
		int events = SharedClientData.messageEvents.incrementAndGet();

		log.debug("MessageEventCounter: " + events);
	}

	@Override
	protected void loginResponseAction(String user) {
		
		// nothing to do for JMS implementation 
		
	}

	@Override
	protected void loginEventAction(String user) {
		
		// nothing to do for JMS implementation
	}

	@Override
	protected void logoutEventAction(String user) {
		
		// nothing to do for JMS implementation
	}

	@Override
	protected void logoutResponseAction(String user) {
		
		// nothing to do for JMS implementation
	}

}
