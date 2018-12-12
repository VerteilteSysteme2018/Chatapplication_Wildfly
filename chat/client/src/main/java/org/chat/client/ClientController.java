package org.chat.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.chat.common.ChatMessage;

import javax.ejb.Singleton;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

@Singleton
public class ClientController {

    private static final Logger log = Logger.getLogger(TopicSubscriber.class.getName());

    // JMS
    private static final String USERNAME = "user";
    private static final String PASSWORD = "user";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String QUEUE_DESTINATION = "jms/queue/chatQueue";
    private static final String TOPIC_DESTINATION = "jms/topic/chatTopic";


    private Destination topic;
    private Destination queue;


    private Context namingContext = null;
    private ConnectionFactory connectionFactory;


    // Individual data of client
    private final String name;
    private final String serverIP;
    private final String serverPort;
    private final String providerURL;
    private final String URL;


    ObjectMessage objectMessage;


    private boolean loggedIn = false;

    private static final String REST = "/server-1.0-SNAPSHOT/rest/users/";

    //private static final String URI = "http://localhost:8080/server-1.0-SNAPSHOT/rest/users/";

    /**
     * Main method to test if all that fancy stuff is working
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {

        //Teststuff for user interactions
        /*
        ClientController clientController = new ClientController();
        clientController.getCurrentUsers();
        clientController.login("TestUser");
        clientController.login("TestUser");
        clientController.logout("TestUser");
        clientController.login("testUser"+Math.round(Math.random()*100));
        clientController.getCurrentUsers();
        clientController.sendMessage();
        clientController.subscribeTopic();

        */
    }


    public ClientController(String name, String serverIP, String serverPort) {
        this.name = name;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;
        this.URL = "http://" + this.serverIP + ":" + this.serverPort;

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

    public boolean login(String userName) throws IOException {
        String uri = this.URL + REST + "login/" + userName;
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
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
    }


    // Code von https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    //TODO change String to array after missing json stuff is implemented
    public ArrayList getCurrentUsers() throws IOException {
        String uri = this.URL + REST + "currentusers";
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        JsonParser jsonParser = new JsonParser();
        JsonArray usersJsonArray = jsonParser.parse(response.toString()).getAsJsonArray();
        Gson gJson = new Gson();
        ArrayList javaArrayListFromGSON = gJson.fromJson(usersJsonArray, ArrayList.class);
        System.out.println(javaArrayListFromGSON);
        return javaArrayListFromGSON;
    }


    public boolean sendMessageToQueue(String message) {
        if(loggedIn) {
                try {
                    QueueConnection connection = (QueueConnection) connectionFactory.createConnection("user", "user");
                    try {
                        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                        try {
                            MessageProducer producer = session.createProducer(this.queue);
                            try {
                                ObjectMessage chatObject = session.createObjectMessage();
                                ChatMessage chatMessage =
                                        new ChatMessage(this.name, message, System.currentTimeMillis(),
                                                Thread.currentThread().toString(), "Wildfly");
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
            return true;
        }
        return false;
    }


    public boolean logout(String userName) throws IOException {
        if(loggedIn) {
            String uri = URL + REST + "logout/" + userName;
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            System.out.println("\nSending 'Delete' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            if (responseCode == 200) {
                loggedIn = false;
                System.out.println("User successfully logged out");
                return true;
            } else {
                System.out.println("User was not logged in at the server");
                return false;
            }
        } else {
            System.out.println("client wasn't logged in");
            return false;
        }
    }


    public TopicConnection subscribeTopic() {
        try {
            return (TopicConnection) this.connectionFactory.createConnection(USERNAME, PASSWORD);
        } catch (JMSException e) {
            e.printStackTrace();
        } return null;
    }

    public Destination getTopic() { return this.topic; }


    /*public JMSConsumer subscribeTopic() {
        if(loggedIn) {
            TopicSubscriber s = new TopicSubscriber(this.providerURL);
            s.initializeConnectionFactory();
            s.lookupTopic();
            this.topic = s.getTopic();

            JMSContext context = s.getJMSContext();
            return context.createConsumer(topic);

            /*Thread t1 = new Thread(() -> {
                while (true) {
                        if (clientLoggedOut) {
                            break;
                        }
                            try {
                                connection.start();
                                TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                                javax.jms.TopicSubscriber subscriber = session.createSubscriber((Topic) this.topic);
                                try {
                                    //TODO change to log in check
                                    ObjectMessage objectMessage = (ObjectMessage) subscriber.receive(5000); //TODO what's that number?
                                    if (objectMessage != null) {
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
                });
                t1.start();
            }
            return true;
        }
        return false;
        */


}