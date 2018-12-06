package org.chat.client;

import org.chat.common.ChatMessage;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * subscribes the chat topic
 */
public class TopicSubscriber {

    private static final Logger log = Logger.getLogger(TopicSubscriber.class.getName());

    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";

    private static final String TOPIC_DESTINATION = "jms/topic/chatTopic";

    private final String providerURL;

    private Context namingContext = null;
    private ConnectionFactory connectionFactory;
    private Destination topic;


    public TopicSubscriber(String providerURL) {
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

    public void lookupTopic() {
        try {
            String destinationString = System.getProperty("destination", TOPIC_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            this.topic = (Destination) this.namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void subscribeTopic() {
        try {
            TopicConnection connection = (TopicConnection)
                    this.connectionFactory.createConnection(USERNAME, PASSWORD);
            try {
                connection.start();
                TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                javax.jms.TopicSubscriber subscriber = session.createSubscriber((Topic) this.topic);
                try {
                   //TODO change to log in check
                        ObjectMessage objectMessage = (ObjectMessage) subscriber.receive(5000); //TODO what's that number?
                        if(objectMessage != null) {
                            ChatMessage message = (ChatMessage) objectMessage.getObject();
                            System.out.println(objectMessage.getObject().toString());
                            System.out.println(message.getUserName() + ":" + message.getMessage());
                        }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
