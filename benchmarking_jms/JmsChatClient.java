package edu.hm.dako.chat.benchmarking_jms;


import edu.hm.dako.chat.client.ClientCommunication;
import edu.hm.dako.chat.client.ClientUserInterface;
import edu.hm.dako.chat.client.SharedClientData;
import edu.hm.dako.chat.common.ClientConversationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chat.client.ClientController;
import org.chat.common.ChatMessage;

import javax.jms.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class JmsChatClient implements ClientCommunication {

    private static Log log = LogFactory.getLog(JmsChatClient.class);

    // Username (Login-Kennung) des Clients
    //protected String userName;

    protected ClientUserInterface userInterface;

    protected String name;
    protected int serverPort;
    protected String serverIP;
    protected String providerURL;

    protected ClientController clientController;


    // Gemeinsame Daten des Clientthreads und dem Message-Listener-Threads
    protected SharedClientData sharedClientData;

    // Thread, der die ankommenden Nachrichten fuer den Client verarbeitet
    protected Thread messageListenerThread;




    public JmsChatClient(ClientUserInterface userInterface, String name, String serverIP, int serverPort) {

        this.userInterface = userInterface;
        this.name = name;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.providerURL = "http-remoting://" + this.serverIP + ":" + this.serverPort;
        this.clientController = new ClientController(this.name, this.serverIP, Integer.toString(serverPort));

        //Verbindung zum Server aufbauen
        try {

            clientController.initializeConnectionFactory();
            clientController.lookupQueue();
            clientController.lookupTopic();


            // Gemeinsame Datenstruktur
            sharedClientData = new SharedClientData();
            sharedClientData.messageCounter = new AtomicInteger(0);
            sharedClientData.logoutCounter = new AtomicInteger(0);
            sharedClientData.eventCounter = new AtomicInteger(0);
            sharedClientData.confirmCounter = new AtomicInteger(0);
            sharedClientData.messageCounter = new AtomicInteger(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void login(String userName) throws IOException {

        sharedClientData.userName = this.name;

        sharedClientData.status = ClientConversationStatus.REGISTERING;

        clientController.login(this.name);

        sharedClientData.status = ClientConversationStatus.REGISTERED;

    }

    @Override
    public void logout(String userName) throws IOException {

        sharedClientData.status = ClientConversationStatus.UNREGISTERING;

        clientController.logout(this.name);

        isLoggedOut();
    }

    @Override
    public void tell(String name, String text) throws IOException {
        ChatMessage chatMessage = new ChatMessage(this.name, text, System.currentTimeMillis(), Thread.currentThread().toString(), "Wildfly");

        clientController.sendMessageToQueue(text);

        sharedClientData.confirmCounter.incrementAndGet();
        sharedClientData.messageCounter.incrementAndGet();

    }



    @Override
    public void cancelConnection() {
    }

    @Override
    public boolean isLoggedOut() {
        return (sharedClientData.status == ClientConversationStatus.UNREGISTERED);
    }

    public Destination getTopic() {
        return clientController.getTopic();
    }

    public ClientController getClientController() { return this.clientController; }

    public String getServerIP() {return this.serverIP; }

    public int getServerPort() {return this.serverPort; }

}