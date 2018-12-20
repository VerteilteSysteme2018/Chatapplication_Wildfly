package edu.hm.dako.chat.test;

import javax.jms.*;
import javax.naming.*;
import java.io.*;
import java.io.InputStreamReader;
import java.util.Properties;

public class Chat implements javax.jms.MessageListener {
    private TopicSession pubSession;
    private TopicSession subSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;


    private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/topic/chat";
    private static final String DEFAULT_MESSAGE_COUNT = "1";
    private static final String DEFAULT_USERNAME = "appUser01";
    private static final String DEFAULT_PASSWORD = "appPwd!1";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";

    /* Constructor. Establish JMS publisher and subscriber */
    public Chat(String topicName, String username, String password)
            throws Exception {
        // Obtain a JNDI connection
        // ... specify the JNDI properties specific to the vendor
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
        env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
        env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);

        InitialContext jndi = new InitialContext(env);

        // Look up a JMS connection factory
        TopicConnectionFactory conFactory = (TopicConnectionFactory) jndi.lookup(DEFAULT_CONNECTION_FACTORY);

        // Create a JMS connection
        TopicConnection connection = conFactory.createTopicConnection(username, password);

        // Create two JMS session objects
        TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        // Look up a JMS topic
        Topic chatTopic = (Topic) jndi.lookup(topicName);

        // Create a JMS publisher and subscriber
        TopicPublisher publisher = pubSession.createPublisher(chatTopic);
        TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);

        // Set a JMS message listener
        subscriber.setMessageListener(this);

        // Intialize the Chat application
        set(connection, pubSession, subSession, publisher, username);

        // Start the JMS connection; allows messages to be delivered
        connection.start();

    }

    /* Initialize the instance variables */
    public void set(TopicConnection con, TopicSession pubSess, TopicSession subSess,
                    TopicPublisher pub, String username) {
        this.connection = con;
        this.pubSession = pubSess;
        this.subSession = subSess;
        this.publisher = pub;
        this.username = username;
    }

    /* Receive message from topic subscriber */
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println(text);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    /* Create and send message using topic publisher */
    protected void writeMessage(String text) throws JMSException {
        TextMessage message = pubSession.createTextMessage();
        message.setText(username + " : " + text);
        publisher.publish(message);
    }

    /* Close the JMS connection */
    public void close() throws JMSException {
        connection.close();
    }

    /* Run the Chat client */
    public static void main(String[] args) {
        try {
            Chat chat = new Chat(DEFAULT_DESTINATION, DEFAULT_USERNAME, DEFAULT_PASSWORD);

            // Read from command line
            BufferedReader commandLine = new
                    java.io.BufferedReader(new InputStreamReader(System.in));

            // Loop until the word "exit" is typed
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    chat.close(); // close down connection
                    System.exit(0);// exit program
                } else
                    chat.writeMessage(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}