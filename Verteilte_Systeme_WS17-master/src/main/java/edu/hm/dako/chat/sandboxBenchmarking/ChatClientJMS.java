package edu.hm.dako.chat.sandboxBenchmarking;

import com.google.gson.Gson;
import edu.hm.dako.chat.client.ClientCommunication;
import edu.hm.dako.chat.client.ClientUserInterface;
import edu.hm.dako.chat.client.SharedClientData;
import edu.hm.dako.chat.common.ChatMessage;
import edu.hm.dako.chat.common.ClientConversationStatus;
import edu.hm.dako.chat.connection.Connection;
import edu.hm.dako.chat.newClient.ClientManager;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class ChatClientJMS implements ClientCommunication {

    // Username (Login-Kennung) des Clients
    protected String userName;

    protected String threadName;

    protected int localPort;

    // protected int serverPort;
    protected String remoteServerAddress;

    protected ClientUserInterface userInterface;

    // Connection Factory und Verbindung zum Server
    // protected ConnectionFactory connectionFactory;
    protected Connection connection;


    // Gemeinsame Daten des Clientthreads und dem Message-Listener-Threads
    protected SharedClientData sharedClientData;

    // Thread, der die ankommenden Nachrichten fuer den Client verarbeitet
    protected Thread messageListenerThread;

    private static final Logger log = Logger.getLogger(ClientManager.class.getName());

    //DEFAULTS REST SERVICE
    private static final String REST_SERVICE_URL = "server-1.0-SNAPSHOT/rest/login/asdf";
    private static final String REST_SERVICE_ENDPOINT = "users";

    private static final String REST = "http://localhost:8080/server-1.0-SNAPSHOT/rest/users/";

    //DEFAULTS JMS
    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/chatQueue";
    private static final String TOPIC_DESTINATION = "jms/topic/chatTopic";

    //CLIENT DATA
    private String name;
    private String serverIP;
    protected String serverPort;
    private String providerURL;

    //OH SHIT!
    private Context namingContext = null;
    private ConnectionFactory connectionFactory;
    private Destination queue;
    private Destination topic;

    /**
     * @param abstractChatClientJMS GUI-Interface
     * @param serverPort            Port des Servers
     * @param remoteServerAddress   Adresse des Servers
     */

    public ChatClientJMS(ClientUserInterface userInterface, String name, String serverPort,
                         String remoteServerAddress) {

        this.userInterface = userInterface;

        this.name = name;

        this.serverPort = serverPort;
        serverPort = "8080";

        this.remoteServerAddress = remoteServerAddress;
        remoteServerAddress = "localhost";

        this.providerURL = "http-remoting://" + this.remoteServerAddress + ":" + this.serverPort;

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
        // sharedClientData.status = ClientConversationStatus.REGISTERING;
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

        /*try {
            sharedClientData.userName = name;
            // sharedClientData.status = ClientConversationStatus.REGISTERING;

            HttpURLConnection connection = initConnection(REST_SERVICE_ENDPOINT, "POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String input = "{\"name\": \"" + name + "\"}";

            OutputStream os;
            os = connection.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            checkConnection(connection);
            this.name = name;

            sharedClientData.status = ClientConversationStatus.REGISTERED;

        } catch (Exception e) {
            e.printStackTrace();
        }*/

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

        /*

        if (name != null) {
            String newEndpoint = REST + name;
            HttpURLConnection connection = initConnection(newEndpoint, "DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            checkConnection(connection);
            sharedClientData.status = ClientConversationStatus.UNREGISTERED;
            this.name = null;
        } else {
            throw new RuntimeException("Failed : User was not logged in yet");
        }

        */

    }

    @Override
    public void tell(String name, String text) throws IOException {

        // Prepare content
        ChatMessage chatMessage = new ChatMessage(this.name, text, Thread.currentThread().toString(), "JMS");
        Gson gson = new Gson();
        String content = gson.toJson(chatMessage);

        try (JMSContext context = this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD))) {
            // log.info("Sending message with content: " + content);
            // Send the specified message
            context.createProducer().setProperty("userName", this.name).send(this.queue, content);
            sharedClientData.confirmCounter.incrementAndGet();
            sharedClientData.messageCounter.incrementAndGet();
        } catch (Exception e) {
            ((Logger) log).severe(e.getMessage());
        }

    }

    @Override
    public void cancelConnection() {

    }

    @Override
    public boolean isLoggedOut() {
        return (sharedClientData.status == ClientConversationStatus.UNREGISTERED);
    }


    private void checkConnection(HttpURLConnection connection) {
        try {
            int responseCode = connection.getResponseCode();


            if (responseCode != 200 && responseCode != 204) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HttpURLConnection initConnection(String urlPart, String requestMethod) {
        try {
            // URL url = new URL("http://" + remoteServerAddress + ":" + serverPort + "/" + REST_SERVICE_URL + "/" + urlPart);
            //URL url = new URL("http://127.0.0.1:8080/" + REST_SERVICE_URL + "/" + urlPart);
            URL url = new URL("http://localhost:8080/server-1.0-SNAPSHOT/rest/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);

            return conn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrentChatUsers() {
        HttpURLConnection connection = initConnection(REST_SERVICE_ENDPOINT, "GET");
        connection.setRequestProperty("Accept", "application/json");
        checkConnection(connection);

        return result(connection);
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


    // obsolete
    public void sendMessage(String message) {
        // Prepare content
        ChatMessage chatMessage = new ChatMessage(this.name, message, Thread.currentThread().toString(), "JMS");
        Gson gson = new Gson();
        String content = gson.toJson(chatMessage);

        try (JMSContext context = this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD))) {
            log.info("Sending message with content: " + content);
            // Send the specified message
            context.createProducer().setProperty("userName", this.name).send(this.queue, content);
        } catch (Exception e) {
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

    private String result(HttpURLConnection conn) {
        String result = null;
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String temp = null;
            StringBuilder sb = new StringBuilder();

            while ((temp = br.readLine()) != null) {
                sb.append(temp).append(" ");
            }

            result = sb.toString();
            br.close();

            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
