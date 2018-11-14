import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Class which handles all the client stuff, including jms connection, log-in, log-out...
 * tbh, at the moment it just creates a queue connection and sends a default message --> TODO
 */
public class Client {
    private static final Logger log = Logger.getLogger(Client.class.getName());

    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/chatQueue";
    private static final String TOPIC_DESTINATION = "jms/topic/chatTopic";

    private final String serverIP;
    private final String serverPort;
    private final String providerURL;

    private Context namingContext = null;
    private ConnectionFactory connectionFactory;
    private Destination queue;
    private Destination topic;

    public Client(String name, String serverIP, String serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;
    }


    public void initializeConnectionFactory() {
        try {
            String userName = System.getProperty("username", USERNAME);
            String password = System.getProperty("password", PASSWORD);

            // JNDI lookup naming context
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

    public void prepareQueue() {
        try {
            String destinationString = System.getProperty("destination", QUEUE_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            this.queue = (Destination) this.namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void prepareTopic() {
        try {
            String destinationString = System.getProperty("destination", TOPIC_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            this.topic = (Destination) this.namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void doStuff() {
        try {
            QueueConnection connection = (QueueConnection) connectionFactory.createConnection("user", "user");
            try {
                QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                try {
                    MessageProducer producer = session.createProducer(this.queue);
                    try {
                        ObjectMessage chatObject = session.createObjectMessage();
                        //TODO put data from gui here
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setMessage("Test Message");
                        chatMessage.setUserName("TestUser");
                        chatObject.setObject(chatMessage);
                        TextMessage message = session.createTextMessage("Nachricht vom externen Client");
                        producer.send(message);
                        producer.send(chatObject);
                        System.out.println("Message an die Queue gesendet");
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

    /**
     * Main method to test if all that fancy stuff is working
     * TODO remove when GUI works
     * @param args
     */
    public static void main(String[] args) {
        Client mq = new Client("test", "localhost", "8080");
        mq.initializeConnectionFactory();
        mq.prepareQueue();
        mq.doStuff();
    }

}