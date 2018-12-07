package org.chat.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.chat.common.ChatMessage;

import javax.ejb.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@Singleton
public class ClientController {

    private final String name;
    private final String serverIP;
    private final String serverPort;
    private final String providerURL;

    private boolean loggedIn = false;

    private static final String URI = "http://localhost:8080/server-1.0-SNAPSHOT/rest/users/";

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
    }


    public boolean subscribeTopic() {
        if(loggedIn) {
            TopicSubscriber s = new TopicSubscriber(this.providerURL);
            s.initializeConnectionFactory();
            s.lookupTopic();
            s.subscribeTopic();
            return true;
        }
        return false;

    }

    public boolean sendMessage(String message) {
        if(loggedIn) {
            QueueSender mq = new QueueSender(this.providerURL);
            mq.initializeConnectionFactory();
            mq.lookupQueue();
            ChatMessage chatMessage =
                new ChatMessage(this.name, message, System.currentTimeMillis(),
                    Thread.currentThread().toString(), "Wildfly");
            mq.sendMessageToQueue(chatMessage);
            return true;
        }
        return false;
    }

    // Code von https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
    //TODO change String to array after missing json stuff is implemented
    public ArrayList getCurrentUsers() throws IOException {
        String uri = URI + "currentusers";
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

    public boolean login(String userName) throws IOException {
        String uri = URI + "login/" + userName;
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

    public boolean logout(String userName) throws IOException {
        if(loggedIn) {
            String uri = URI + "logout/" + userName;
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
}