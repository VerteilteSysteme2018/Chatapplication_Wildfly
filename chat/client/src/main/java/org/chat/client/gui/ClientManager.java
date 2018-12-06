package org.chat.client.gui;

import com.google.gson.Gson;
import org.chat.common.ChatMessage;

import javax.jms.*;
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
    private static final String REST_SERVICE_URL = "server-1.0-SNAPSHOT/rest";
    private static final String REST_SERVICE_ENDPOINT = "users";

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

    private boolean loggedIn = false;

    public ClientManager(String name, String serverIP, String serverPort) {
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

    public String getCurrentUsers() {
        String url = REST_SERVICE_ENDPOINT + "/" + "currentusers";
        HttpURLConnection connection = initConnection(url, "GET");
        connection.setRequestProperty("Accept", "application/json");
        checkConnection(connection);

        return result(connection);
    }

    public boolean login(String name) {
        try {
            String url = REST_SERVICE_ENDPOINT + "/" + "login" + "/" + name;
            HttpURLConnection connection = initConnection(url, "POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            if (responseCode == 200) {
                loggedIn = true;
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void logout() {
        if (this.name != null) {
            String newEndpoint = REST_SERVICE_ENDPOINT + "/" + "logout" + "/" + this.name;
            HttpURLConnection connection = initConnection(newEndpoint, "DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            checkConnection(connection);

            this.name = null;
        } else {
            throw new RuntimeException("Failed : User was not logged in yet");
        }
    }

    public void sendMessage(String message) {
        // Prepare content
        ChatMessage chatMessage = new ChatMessage(this.name, message, System.currentTimeMillis());
        Gson gson = new Gson();
        String content = gson.toJson(chatMessage);

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

    public JMSContext getJMSContext() {
        try {
            return this.connectionFactory.createContext(System.getProperty("username", USERNAME), System.getProperty("password", PASSWORD));
        } catch (Exception e) {
            log.severe(e.getMessage());
            return null;
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