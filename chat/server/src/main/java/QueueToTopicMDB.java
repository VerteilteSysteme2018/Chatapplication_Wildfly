import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;
import javax.annotation.Resource;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/queue/chatQueue"
        )
})
public class QueueToTopicMDB implements MessageListener {

    @Resource(lookup = "java:/jms/chatFactory")
    TopicConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/topic/chatTopic")
    Topic topic;


    @Override
    public void onMessage(Message message) {

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
                            session.createPublisher(topic);
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
        } catch (Exception ex) {
        }


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
    }
}




