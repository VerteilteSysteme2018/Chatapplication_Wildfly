package edu.hm.dako.chat.benchmarking_jms;

import edu.hm.dako.chat.client.AbstractMessageListenerThread;
import edu.hm.dako.chat.client.ClientUserInterface;
import edu.hm.dako.chat.client.SharedClientData;
import edu.hm.dako.chat.common.ChatPDU;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chat.client.ClientController;

public class JMSSimpleMessageListenerThreadImpl extends AbstractMessageListenerThread {

    private static Log log = LogFactory.getLog(JMSSimpleMessageListenerThreadImpl.class);

    private String threadName;
    private int numberOfMessagesToSend;
    private String serverIP;
    private int serverPort;


    public JMSSimpleMessageListenerThreadImpl (ClientUserInterface userInterface, SharedClientData sharedData, String threadName, int numberOfMessagesToSend, String serverIP, int serverPort) {

        super(userInterface, null, sharedData);
        this.threadName = threadName;
        this.numberOfMessagesToSend = numberOfMessagesToSend;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void run() {

        JmsChatClient client = new JmsChatClient(userInterface, this.threadName, this.serverIP, this.serverPort);
        ClientController clientController = client.getClientController();
/*
        TopicConnection topicConnection = clientController.subscribeTopic();
        Destination topic = clientController.getTopic();

        boolean messageCheck = true;

            while (true) {
                if (messageCheck == false) {
                    break;
                }

                try {
                    topicConnection.start();
                    TopicSession session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                    javax.jms.TopicSubscriber subscriber = session.createSubscriber((Topic) topic);
                    ObjectMessage objectMessage = (ObjectMessage) subscriber.receive(5000);


                    if (objectMessage != null) {

                        ChatMessage message = (ChatMessage) objectMessage.getObject();

                        String chatName = message.getUserName();
                        String chatMessage = message.getMessage();

                        chatMessageEventAction(chatName);
                        chatMessageResponseAction(chatName);
                    }

                    if (sharedClientData.messageCounter.get() == numberOfMessagesToSend) {
                        messageCheck = false;

                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            */

    }

     
    protected void chatMessageResponseAction(ChatPDU receivedPdu) {
        // nothing to do for JMS implementation

    }

     
    protected void chatMessageEventAction(ChatPDU receivedPdu) {
        // nothing to do for JMS implementation

    }

     
    protected void loginResponseAction(ChatPDU receivedPdu) {
        // nothing to do for JMS implementation

    }

     
    protected void loginEventAction(ChatPDU receivedPdu) {
        // nothing to do for JMS implementation
    }

     
    protected void logoutEventAction(ChatPDU receivedPdu) {
        // nothing to do for JMS implementation
    }

     
    protected void logoutResponseAction(ChatPDU receivedPdu) {

        sharedClientData.eventCounter.getAndIncrement();
        int events = SharedClientData.logoutEvents.incrementAndGet();

        log.debug("LogoutEventCounter: " + events);
    }

     
    protected void chatMessageResponseAction(String user) {

        userInterface.setLastServerTime(System.currentTimeMillis() / 1000000);
        userInterface.setLock(false);

    }

     
    protected void chatMessageEventAction(String user) {

        sharedClientData.eventCounter.getAndIncrement();
        int events = SharedClientData.messageEvents.incrementAndGet();

        log.debug("MessageEventCounter: " + events);
    }

     
    protected void loginResponseAction(String user) {

        // nothing to do for JMS implementation

    }

     
    protected void loginEventAction(String user) {

        // nothing to do for JMS implementation
    }

     
    protected void logoutEventAction(String user) {

        // nothing to do for JMS implementation
    }

     
    protected void logoutResponseAction(String user) {

        // nothing to do for JMS implementation
    }
}
