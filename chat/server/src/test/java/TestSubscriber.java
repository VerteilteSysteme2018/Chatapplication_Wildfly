import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Zusammenkopiertes Zeug um ein Topic zu abonnieren.
 * Funktioniert auch noch nicht ;)
 * TODO AWE fix
 */
public class TestSubscriber {

    // Set up all the default values
    private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/topic/chatTopic";
    private static final String DEFAULT_MESSAGE_COUNT = "1";
    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PASSWORD = "user";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";
    private static final Logger log = Logger.getLogger(TestClient.class.getName());

    //DEFAULTS REST SERVICE
    private static final String REST_SERVICE_URL = "chatServer-web/rest";
    private static final String REST_SERVICE_ENDPOINT = "chatUsers";

    //DEFAULTS JMS
    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/chatQueue";
    private static final String TOPIC_DESTINATION = "jms/topic/chatTopic";

    //CLIENT DATA
    private String name;
    private final String serverIP;
    private final String serverPort;
    private final String providerURL;

    //OH SHIT!
    private Context namingContext = null;
    private ConnectionFactory connectionFactory;
    private Destination queue;
    private Destination topic;

    public TestSubscriber(String name, String serverIP, String serverPort) {
        this.name = name;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;
    }
    /**
     * This reciever runs in a endless loop!
     */
    public static void main(String[] args) {

        Context namingContext = null;

        try {
            String userName = System.getProperty("username", DEFAULT_USERNAME);
            String password = System.getProperty("password", DEFAULT_PASSWORD);

            // Set up the namingContext for the JNDI lookup
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);
            namingContext = new InitialContext(env);

            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            Destination destination = (Destination) namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");

            int count = Integer.parseInt(System.getProperty("message.count", DEFAULT_MESSAGE_COUNT));
            String content = System.getProperty("message.content", DEFAULT_MESSAGE);

            TestSubscriber s = new TestSubscriber("test", "localhost", "8080");

            s.initializeConnectionFactory();
            s.prepareTopic();

            s.doStuff(connectionFactory);



        } catch (NamingException e) {
            log.severe(e.getMessage());
        } finally {
            if (namingContext != null) {
                try {
                    namingContext.close();
                } catch (NamingException e) {
                    log.severe(e.getMessage());
                }
            }
        }
    }

    private void doStuff(ConnectionFactory factory) {
        try {
            TopicConnection connection = (TopicConnection)
                    factory.createConnection("user", "user");
            try {
                connection.start();
                TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                TopicSubscriber subscriber = session.createSubscriber((Topic) this.topic);
                try {
                    //TextMessage m = (TextMessage) subscriber.receive(100000);
                    //System.out.print(m.getText());
                    ObjectMessage objectMessage = (ObjectMessage) subscriber.receive(100000);
                    System.out.print(objectMessage.getObject().toString());
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

    public void initializeConnectionFactory() {
        try {
            String userName = System.getProperty("username", USERNAME);
            String password = System.getProperty("password", PASSWORD);

            // Set up the namingContext for the JNDI lookup
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, this.providerURL);
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);
            this.namingContext = new InitialContext(env);

            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            this.connectionFactory = (ConnectionFactory) this.namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }
}
