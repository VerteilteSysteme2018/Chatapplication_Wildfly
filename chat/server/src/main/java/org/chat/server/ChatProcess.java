package org.chat.server;

import org.chat.common.ChatMessage;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import javax.annotation.Resource;
import java.util.Date;

/**
 * This is the core class of the server where the incoming chat requests come in and all the other stuff has to be done.
 * looks really ugly but i don't know how to make it looking beautiful
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/queue/chatQueue"
        )
})
public class ChatProcess implements MessageListener {

    @Resource(lookup = "java:/jms/RemoteConnectionFactory")
    TopicConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/topic/chatTopic")
    Topic chatTopic;

    @Override
    public void onMessage(Message message) {

        if (message instanceof ObjectMessage) {
            //TODO add transaction
            //TODO add check if user is logged in

            //set server time of the pdu and publish it to the topic
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                ChatMessage chatMessage = (ChatMessage) objectMessage.getObject();
                System.out.println("Queue:" + chatMessage.getMessage() + "von" + chatMessage.getUserName());
                if (userLoggedIn(chatMessage.getUserName())) {
                    //TODO do any db transaction with the message stuff
                    sendMessageToTopic(message);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean userLoggedIn(String userName) {
        // TODO implement user request to topic
        return true;
    }

    /**
     * creates the topic connection and publish the message to the topic
     *
     * @param message the message to publish to the topic
     */
    private void sendMessageToTopic(Message message) {
        try {
            TopicConnection connection = connectionFactory.createTopicConnection(
                    "user",
                    "user"
            );
            connection.start();
            try {
                TopicSession session =
                        connection.createTopicSession(
                                false,
                                Session.AUTO_ACKNOWLEDGE
                        );
                try {
                    TopicPublisher publisher =
                            session.createPublisher(chatTopic);
                    try {
                        publisher.publish(message);
                    } finally {
                        publisher.close();
                    }
                } finally {
                    session.close();
                }
            }
            finally {
                connection.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}




