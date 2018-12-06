package org.chat.client;

import org.chat.common.ChatMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * sends messages to the queue
 */
public class QueueSender {
    private static final Logger log = Logger.getLogger(QueueSender.class.getName());

    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/chatQueue";

    private final String providerURL;

    private Context namingContext = null;
    private ConnectionFactory connectionFactory;
    private Destination queue;

    public QueueSender(String providerURL) {
        this.providerURL = providerURL;
    }

    public void initializeConnectionFactory() {
        try {
            String userName = System.getProperty("username", USERNAME);
            String password = System.getProperty("password", PASSWORD);
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, this.providerURL);
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);
            this.namingContext = new InitialContext(env);
            String connectionFactoryString = System.getProperty("connection.factory", CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            this.connectionFactory = (ConnectionFactory) this.namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void lookupQueue() {
        try {
            String destinationString = System.getProperty("destination", QUEUE_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            this.queue = (Destination) this.namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void sendMessageToQueue(ChatMessage chatMessage) {
        try {
            QueueConnection connection = (QueueConnection) connectionFactory.createConnection("user", "user");
            try {
                QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                try {
                    MessageProducer producer = session.createProducer(this.queue);
                    try {
                        ObjectMessage chatObject = session.createObjectMessage();
                        chatObject.setObject(chatMessage);
                        producer.send(chatObject);
                        System.out.println("Message-Objekt an die Queue gesendet");
                    } finally {
                        producer.close();
                    }
                } finally {
                    session.close();
                }
            } finally {
                connection.close();
            }
        } catch (Exception ex) {
        }
    }
}