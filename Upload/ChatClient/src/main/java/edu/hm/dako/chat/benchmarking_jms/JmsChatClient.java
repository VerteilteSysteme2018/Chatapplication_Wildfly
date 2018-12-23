package edu.hm.dako.chat.benchmarking_jms;

import com.google.gson.Gson;
import edu.hm.dako.chat.client.ClientCommunication;
import edu.hm.dako.chat.client.ClientUserInterface;
import edu.hm.dako.chat.client.SharedClientData;
import edu.hm.dako.chat.client_jms.ClientController;
import edu.hm.dako.chat.common.ChatMessage;
import edu.hm.dako.chat.common.ClientConversationStatus;
import edu.hm.dako.chat.connection.Connection;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class JmsChatClient implements ClientCommunication {

    protected String userName;

    protected String threadName;

    protected ClientUserInterface userInterface;

    protected Connection connection;

    protected SharedClientData sharedClientData;

    protected Thread messageListenerThread;

    private static final Logger log = Logger.getLogger(ClientController.class.getName());

    //REST
    private static final String REST = "http://localhost:8080/server-1.0-SNAPSHOT/rest/users/";

    //JMS
    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/chatQueue";
    private static final String TOPIC_DESTINATION = "jms/topic/chatTopic";

    private Context namingContext = null;
    private ConnectionFactory connectionFactory;
    private Destination queue;
    private Destination topic;


    //CLIENT
    private String name;
    private String serverIP;
    protected String serverPort;
    private String providerURL;



    /**
     * @param userInterface GUI-Interface
     * @param name Name des Users
     * @param serverPort  Port des Servers
     * @param serverIP   Adresse des Servers
     */

    public JmsChatClient(ClientUserInterface userInterface, String name, String serverPort,
                         String serverIP) {

        this.userInterface = userInterface;

        this.name = name;

        this.serverPort = serverPort;

        this.serverIP = serverIP;

        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;

        initializeConnectionFactory();

        prepareQueue();

        prepareTopic();

        // Gemeinsame Datenstruktur
        sharedClientData = new SharedClientData();
        sharedClientData.messageCounter = new AtomicInteger(0);
        sharedClientData.logoutCounter = new AtomicInteger(0);
        sharedClientData.eventCounter = new AtomicInteger(0);
        sharedClientData.confirmCounter = new AtomicInteger(0);
        sharedClientData.messageCounter = new AtomicInteger(0);
    }


    /**
     * Ergaenzt ConnectionFactory um Logging-Funktionalitaet
     *
     * @param connectionFactory ConnectionFactory
     * @return Dekorierte ConnectionFactory
     */

    @Override
    public void login(String name) throws IOException {
        sharedClientData.userName = name;
        sharedClientData.status = ClientConversationStatus.REGISTERING;
        String uri = REST + "login/" + name;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        if (responseCode == 200 || responseCode == 201) {
            System.out.println("user logged in ");
            sharedClientData.status = ClientConversationStatus.REGISTERED;
            this.name = name;
        } else {
            System.out.println("user not logged in");
        }

    }

    @Override
    public void logout(String name) throws IOException {

        sharedClientData.status = ClientConversationStatus.UNREGISTERING;

        String uri = REST + "logout/" + name;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'Delete' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        if (responseCode == 200) {
            System.out.println("User successfully logged out");
            sharedClientData.status = ClientConversationStatus.UNREGISTERED;
            this.name = null;
        } else {
            System.out.println("User was not logged in at the server");
        }

    }

    @Override
    public void tell(String name, String text) throws IOException {

        // Prepare message
        ChatMessage chatMessage = new ChatMessage(this.name, text, System.currentTimeMillis(), Thread.currentThread().toString(), "JMS");
        Gson gson = new Gson();
        String content = gson.toJson(chatMessage);

        try (JMSContext context = this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD))) {

            context.createProducer().setProperty("userName", this.name).send(this.queue, content);

            sharedClientData.confirmCounter.incrementAndGet();
            sharedClientData.messageCounter.incrementAndGet();
        } catch (Exception e) {
            log.severe(e.getMessage());
            System.out.print("exception while trying to send message");
        }

    }

    @Override
    public void cancelConnection() {

    }

    @Override
    public boolean isLoggedOut() {
        return (sharedClientData.status == ClientConversationStatus.UNREGISTERED);
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
            //log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            this.connectionFactory = (ConnectionFactory) this.namingContext.lookup(connectionFactoryString);
            //log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void prepareQueue() {
        try {
            String destinationString = System.getProperty("destination", QUEUE_DESTINATION);
            //log.info("Attempting to acquire destination \"" + destinationString + "\"");
            this.queue = (Destination) this.namingContext.lookup(destinationString);
            //log.info("Found destination \"" + destinationString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }

    public void prepareTopic() {
        try {
            String destinationString = System.getProperty("destination", TOPIC_DESTINATION);
            //log.info("Attempting to acquire destination \"" + destinationString + "\"");
            this.topic = (Destination) this.namingContext.lookup(destinationString);
            //log.info("Found destination \"" + destinationString + "\" in JNDI");
        } catch (NamingException e) {
            log.severe(e.getMessage());
        }
    }


    public JMSContext getJMSContext() {
        try {
            JMSContext context = this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD));
            return context;
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
        }
    }


    public Destination getTopic() {
        return this.topic;
    }


}
