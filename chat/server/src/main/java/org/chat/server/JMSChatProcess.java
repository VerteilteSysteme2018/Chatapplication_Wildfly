package org.chat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.ejb.MessageDrivenContext;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.chat.common.ChatMessage;
import org.chat.databases.CountRepository;
import org.chat.databases.Trace;
import org.chat.databases.TraceRepository;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import javax.transaction.Transactional;
import java.util.Properties;

/**
 * This is the core class of the server where the incoming jms chat requests arrive
 *
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destination",
                propertyValue = "java:/jms/queue/chatQueue"
        )
})
@Transactional
public class JMSChatProcess implements MessageListener {

    @Resource(lookup = "java:/jms/RemoteConnectionFactory")
    TopicConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/topic/chatTopic")
    Topic chatTopic;

    @Resource(mappedName = "java:/jms/queue/response")
    private Queue responseQueue;
    @Inject
    private JMSContext context;

    @Resource
    private MessageDrivenContext messageDrivenContext;

    @EJB
    private TraceRepository traceRepository;

    @EJB
    private CountRepository countRepository;

    @Override
    public void onMessage(Message message) {
        ChatMessage chatMessage = null;

        if (message instanceof TextMessage) {
            System.out.println("TextMessage empfangen");
            Gson gson = new GsonBuilder().create();
            try {
                chatMessage = gson.fromJson(((TextMessage) message).getText(), ChatMessage.class);
            } catch (JMSException e) {
                System.out.println("Fehler beim nachricht entpacken");
                e.printStackTrace();
                messageDrivenContext.setRollbackOnly();
            }
        }
        if (message instanceof ObjectMessage) {
            System.out.println("ObjectMessage empfangen");
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                chatMessage = (ChatMessage) objectMessage.getObject();
                System.out.println("Queue:" + chatMessage.getMessage() + "von" + chatMessage.getUserName());
            } catch (JMSException e) {
                System.out.println("fehler beim nachricht entpacken");
                e.printStackTrace();
                messageDrivenContext.setRollbackOnly();
            }
        }

        // update tracedb
        System.out.println("update tracedb");
        Trace trace = new Trace();
        trace.setClientThread(chatMessage.getClientThread());
        trace.setUsername(chatMessage.getUserName());
        trace.setMessage(chatMessage.getMessage());
        trace.setServerthread(chatMessage.getServerThread());
        traceRepository.create(trace);

        //update countdb
        System.out.println("update countdb");
        countRepository.updateCount(chatMessage.getUserName());

        //send to topic
        //sendMessageToTopic(message);
        context.createProducer().send(chatTopic, message);

        // Send success response to queue/response
        context.createProducer().setProperty("userName", chatMessage.getUserName()).setProperty("success", true)
                .send(responseQueue, String.format("User '%s' is logged in, processed message.", chatMessage.getUserName()));
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
            } finally {
                connection.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}




