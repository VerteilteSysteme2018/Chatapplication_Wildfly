package edu.hm.dako.chat.benchmarking_jms;

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

public class JmsSimpleMessageListenerThread extends AbstractMessageListenerThread {

	private static Log log = LogFactory.getLog(SimpleMessageListenerThreadImpl.class);

	private String threadName;
	private int numberOfMessagesToSend;

	public JmsSimpleMessageListenerThread(ClientUserInterface userInterface, Connection con, SharedClientData sharedData, String threadName, int numberOfMessagesToSend) {

		super(userInterface, con, sharedData);
		this.threadName = threadName;
		this.numberOfMessagesToSend = numberOfMessagesToSend;
	}

	public void run() {

		jmsChatClient = new JmsChatClient(userInterface, "jms", "8080", "localhost");
		JMSContext context = jmsChatClient.getJMSContext();
		Destination topic = jmsChatClient.getTopic();
		JMSConsumer consumer = context.createConsumer(topic);

		boolean receiving = true;

		while(receiving == true) {

			String messages = consumer.receiveBody(String.class, 3000);

			if(messages != null) {

				Gson gson = new Gson();
				JsonObject json = gson.fromJson(messages, JsonObject.class);

				String name = json.get("userName").getAsString();
				String message = json.get("message").getAsString();

				chatMessageEventAction(name);
				chatMessageResponseAction(name);
			}

		}
	}

	@Override
	protected void chatMessageResponseAction(ChatPDU receivedPdu) {
		//

	}

	@Override
	protected void chatMessageEventAction(ChatPDU receivedPdu) {
		//

	}

	@Override
	protected void loginResponseAction(ChatPDU receivedPdu) {
		//

	}

	@Override
	protected void loginEventAction(ChatPDU receivedPdu) {
		//
	}

	@Override
	protected void logoutEventAction(ChatPDU receivedPdu) {
		//
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

}
