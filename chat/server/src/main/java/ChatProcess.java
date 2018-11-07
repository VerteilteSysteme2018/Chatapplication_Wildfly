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
            //TODO add logged-in check with response pdus (deliver via another topic?)
            //TODO add db-actions

            //set server time of the pdu and publish it to the topic
            ObjectMessage pduMessage = (ObjectMessage) message;
            try {
                ChatPDU chatPDU = (ChatPDU) pduMessage.getObject();
                chatPDU.setServerTime(System.currentTimeMillis());
                sendMessageToTopic(pduMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
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

        //From now on, it's only code to create beautiful log messages to see where the messages get lost all the time
        //TODO AWE remove later
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;

            try {
                System.out.println(
                        String.format(
                                "Message erfolgreich weitergeleitet: '%s'",
                                textMessage.getText()
                        )
                );
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        if (message instanceof ObjectMessage) {
            ObjectMessage pduMessage = (ObjectMessage) message;
            try {
                ChatPDU pdu = (ChatPDU) pduMessage.getObject();
                System.out.println("Die Message aus dem PDU ist weitergeleitet und zwar: " + pdu.getMessage() + " Zeit:"+ new Date(pdu.getServerTime()));
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}




