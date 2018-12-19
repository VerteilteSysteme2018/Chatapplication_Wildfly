package org.chat.client.gui;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.chat.client.ClientController;
import org.chat.common.ChatMessage;

import javax.jms.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestClient implements Runnable {

    public TestClient(String username, String serverIP, String serverPort) {
        this.username = username;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    private String username;
    private String serverIP;
    private String serverPort;

    private boolean clientIsLoggedOut = true;

    private ClientController clientController;

    private Destination topic;


    public void loginUser() {

        clientController = new ClientController(username, serverIP, serverPort);

            try {
                if (clientController.login(username)) {
                    clientIsLoggedOut = false;

                    clientController.initializeConnectionFactory();
                    clientController.lookupQueue();
                    clientController.lookupTopic();

                    //getMessages();
                } else {
                    System.out.print("Login failed. Please try again!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void logoutUser() {
        try {
            clientController.logout(username);
            clientIsLoggedOut = true;
        } catch (IOException e) {
            System.out.print("That didn't work." + "\n" + "Please try again!");
        }
    }

    public void sendMessage(String message) {
            clientController.sendMessageToQueue(username, message, "JMS");
    }

    public void getMessages() {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (clientIsLoggedOut == true) {
                    break;
                }
                try {

                    TopicConnection topicConnection = clientController.subscribeTopic();
                    this.topic = clientController.getTopic();

                    topicConnection.start();
                    TopicSession session = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
                    javax.jms.TopicSubscriber subscriber = session.createSubscriber((Topic) this.topic);
                    ObjectMessage objectMessage = (ObjectMessage) subscriber.receive(5000);

                    if (objectMessage != null) {
                        ChatMessage message = (ChatMessage) objectMessage.getObject();

                        String chatName = message.getUserName();
                        String chatMessage = message.getMessage();
                        long time = message.getTimestamp();

                        long RTT = System.currentTimeMillis() - time;

                        System.out.print("\n \n Roundtriptime:" + RTT);
                        System.out.println(objectMessage.getObject().toString());
                        System.out.println(message.getUserName() + ":" + message.getMessage());

                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();

        //get Kafka Topic
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.chat.common.ChatMessageDeserializer");
        properties.put("group.id", "test-group");
        KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
        List topics = new ArrayList();
        topics.add("requestTopic");
        kafkaConsumer.subscribe(topics);
        Thread thread = new Thread(() -> {
            try {

                while (true) {
                    ConsumerRecords<String, ChatMessage> messages = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, ChatMessage> omessage : messages) {
                        System.out.println("Message received " + omessage.value().toString());
                        ChatMessage message = omessage.value();
                        String chatName = message.getUserName();
                        String chatMessage = message.getMessage();

                        System.out.println(message.getUserName() + ":" + message.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public boolean getIsClientLoggedOut() { return clientIsLoggedOut; }

    @Override
    public void run() {

    }
}
