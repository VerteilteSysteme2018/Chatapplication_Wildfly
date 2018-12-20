package edu.hm.dako.chat.newClient;

import com.google.gson.Gson;
import edu.hm.dako.chat.common.ChatMessage;

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
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

//

public class ClientManager {
    private static final Logger log = Logger.getLogger(ClientManager.class.getName());

    //DEFAULTS REST SERVICE
    private static final String REST_SERVICE_URL = "chatServer-web/rest";
    private static final String REST_SERVICE_ENDPOINT = "chatUsers";

    //DEFAULTS JMS
    private static final String USERNAME = "appUser01";
    private static final String PASSWORD = "appPwd!1";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/newMessage";
    private static final String TOPIC_DESTINATION = "jms/topic/chat";

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

    public ClientManager(String name, String serverIP, String serverPort) {
        this.name = name;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;
    }

    public String getCurrentChatUsers() {
        HttpURLConnection connection = initConnection(REST_SERVICE_ENDPOINT, "GET");
        connection.setRequestProperty("Accept", "application/json");
        checkConnection(connection);

        return result(connection);
    }

    public void logout() {
        if (this.name != null) {
            String newEndpoint = REST_SERVICE_ENDPOINT + "/" + this.name;
            HttpURLConnection connection = initConnection(newEndpoint, "DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            checkConnection(connection);

            this.name = null;
        } else {
            throw new RuntimeException("Failed : User was not logged in yet");
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
            return this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD));
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
        }
    }

    public boolean login(String name) {
        try {
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

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private HttpURLConnection initConnection(String urlPart, String requestMethod) {
        try {
            URL url = new URL("http://" + serverIP + ":" + serverPort + "/" + REST_SERVICE_URL + "/" + urlPart);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);

            return conn;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    public Destination getTopic() {
        return this.topic;
    }

    private String result(HttpURLConnection conn) {
        String result;
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String temp;
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