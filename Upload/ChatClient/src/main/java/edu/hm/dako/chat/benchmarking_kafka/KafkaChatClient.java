package edu.hm.dako.chat.benchmarking_kafka;

import edu.hm.dako.chat.client.ClientCommunication;
import edu.hm.dako.chat.client.ClientUserInterface;
import edu.hm.dako.chat.client.SharedClientData;
import edu.hm.dako.chat.client_jms.ClientController;
import edu.hm.dako.chat.common.ChatMessage;
import edu.hm.dako.chat.common.ClientConversationStatus;
import edu.hm.dako.chat.connection.Connection;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

public class KafkaChatClient implements ClientCommunication {


    protected String userName;


    protected ClientUserInterface userInterface;

    protected Connection connection;


    private static final Logger log = Logger.getLogger(ClientController.class.getName());

    //REST
    private static final String REST = "http://localhost:8080/server-1.0-SNAPSHOT/rest/users/";

    //CLIENT DATA
    private String name;


    public KafkaChatClient() { }


    @Override
    public void login(String name) throws IOException {

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
            this.name = name;
        } else {
            System.out.println("user not logged in");
        }

    }

    @Override
    public void logout(String name) throws IOException {

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
            this.name = null;
        } else {
            System.out.println("User was not logged in at the server");
        }

    }

    @Override
    public void tell(String name, String text) throws IOException {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "edu.hm.dako.chat.common.ChatMessageSerializer");
        properties.put("group.id", name);

        KafkaProducer kafkaProducer = new KafkaProducer<String, ChatMessage>(properties);

        try {
            System.out.println(text);
            ChatMessage chatMessage =
                    new ChatMessage(name, text, System.currentTimeMillis(),
                            Thread.currentThread().toString(), "Kafka");
            ProducerRecord<String, ChatMessage> pR = new ProducerRecord<String, ChatMessage>("requestTopic", chatMessage);
            kafkaProducer.send(pR);
            System.out.println("MESSAGE GESENDET "+ pR.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            kafkaProducer.close();
        }


    }



    @Override
    public void cancelConnection() {
    //
    }

    @Override
    public boolean isLoggedOut() {
        return false;
    }


}
