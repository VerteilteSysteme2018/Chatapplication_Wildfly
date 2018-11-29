package org.chat.server.TestStuff;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * This class is just for Debugging the server process
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/topic/chatTopic"
        )
})
public class TopicMDB implements MessageListener {
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;

            try {
                System.out.println(
                        String.format(
                                "Message vom TopicListener: '%s'",
                                textMessage.getText()
                        )
                );
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}