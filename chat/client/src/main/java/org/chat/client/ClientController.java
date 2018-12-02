package org.chat.client;

import org.chat.common.ChatMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class ClientController {

    private String uri = "http://localhost:8080/server-1.0-SNAPSHOT/rest/users/";

    /**
     * Main method to test if all that fancy stuff is working
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {

        ClientController clientController = new ClientController();
        clientController.getCurrentUsers();
        clientController.login("testUser"+Math.round(Math.random()*100));
        clientController.getCurrentUsers();
        clientController.sendMessage();
        clientController.subscribeTopic();
    }

    private void subscribeTopic() {
        TopicSubscriber s = new TopicSubscriber("test", "localhost", "8080");
        s.initializeConnectionFactory();
        s.lookupTopic();
        s.subscribeTopic();
    }

    private void sendMessage() {
        QueueSender mq = new QueueSender("test", "localhost", "8080");
        mq.initializeConnectionFactory();
        mq.lookupQueue();
        mq.sendMessageToQueue(new ChatMessage());
    }

    // Code von https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    //TODO change String to array after missing json stuff is implemented
    private String getCurrentUsers() throws IOException {
        String uri = this.uri + "currentusers";
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
        System.out.println(response.toString());
        return response.toString();
    }

    private boolean login(String userName) throws IOException {
        String currentUsers = getCurrentUsers();
        if (!currentUsers.contains(userName)) {
            String uri = this.uri + "login/" + userName;
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            return true;
        }
        return false;
    }
}
