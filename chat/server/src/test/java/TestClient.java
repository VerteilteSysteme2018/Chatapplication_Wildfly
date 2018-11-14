
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Zusammenkopiertes Zeug um der Queue eine Nachricht zu schicken
 */
public class TestClient {
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

    public TestClient(String name, String serverIP, String serverPort) {
        this.name = name;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;
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




    public void doStuff() {



        try {
            QueueConnection connection = (QueueConnection)
                    connectionFactory.createConnection(
                            "user",
                            "user"
                    );

            try {
                QueueSession session =
                        connection.createQueueSession(
                                false,
                                Session.AUTO_ACKNOWLEDGE
                        );

                try {
                    MessageProducer producer =
                            session.createProducer(this.queue);

                    try {
                        ObjectMessage pduMessage =
                                session.createObjectMessage();
                        ChatMessage pdu = new ChatMessage();
                        pdu.setMessage("PDU Test Message");
                        pdu.setUserName("Test User");
                        pduMessage.setObject(pdu);
                        TextMessage message =
                                session.createTextMessage(
                                        "Nachricht vom externen Client"
                                );

                        producer.send(message);
                        producer.send(pduMessage);

                        System.out.println(
                                "Message an die Queue gesendet"
                        );
                        System.out.println("Nachricht in der Queue angekommen");
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

    public static void main(String[] args) {
        TestClient mq = new TestClient("test", "localhost", "8080");
        mq.initializeConnectionFactory();
        mq.prepareQueue();


        mq.doStuff();


    }
/**
    public void sendMessage(String message) {
        try (JMSContext context = this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD))) {
            log.info("Sending message with content: " + content);
            // Send the specified message
            context.createProducer().setProperty("userName", this.name).send(this.queue, content);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }
/**
    public JMSContext getJMSContext() {
        try {
            return this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD));
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
        }
    }
*/
}