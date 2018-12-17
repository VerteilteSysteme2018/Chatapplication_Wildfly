package org.chat.client.gui;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.chat.client.ClientController;
import org.chat.common.ChatMessage;

import javax.jms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClientGUI implements Runnable {
    private final int fHeight = 430;
    private final int fWidth = 770;

    private String username;
    private String serverIP;
    private String serverPort;

    private boolean clientIsLoggedOut = true;

    private ClientController clientController;

    private Destination topic;


    private JFrame chatFrame;

    private JTextArea textareaUserList;
    private JTextArea textareaChat;
    private JTextArea textareaChatMessage;

    private JLabel labelName;
    private JLabel labelServerIP;
    private JLabel labelServerPort;
    private JLabel labelUserList;

    private JTextField textfieldUserName;
    private JTextField textfieldServerIP;
    private JTextField textfieldServerPort;

    private JButton buttonLogin;
    private JButton buttonLogout;
    private JButton buttonSendMessage;

    private JScrollPane scrollpaneUserList;
    private JScrollPane scrollpaneChat;
    private JScrollPane scrollpaneChatMessage;

    private JSeparator separatorVertical;


    private void initializeChatGui() {

        chatFrame = new JFrame("F R I E N D S    M E S S E N G E R");
        chatFrame.setResizable(false);
        chatFrame.getContentPane().setLayout(null);
        chatFrame.getContentPane().setBackground(Color.DARK_GRAY);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int posX = (screenSize.width - fWidth) / 2;
        int posY = (screenSize.height - fHeight) / 3;
        chatFrame.setBounds(posX, posY, fWidth, fHeight);
        chatFrame.setPreferredSize(new Dimension(fWidth, fHeight));
        chatFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        chatFrame.addWindowListener(new WindowAdapter() {
            /**
             * If Gui is closed and user is still logged in, log out first.
             * @param event Window closing event
             */
            @Override
            public void windowClosing(WindowEvent event) {
                if (clientIsLoggedOut == false) {
                    logoutUser();

                }
                chatFrame.dispose();
                System.exit(0);
            }
        });


        //User Listing
        textareaUserList = new JTextArea();
        scrollpaneUserList = new JScrollPane(textareaUserList);
        scrollpaneUserList.setBounds(490, 32, 220, 152);
        chatFrame.getContentPane().add(scrollpaneUserList);

        //Chat Frame
        textareaChat = new JTextArea();
        scrollpaneChat = new JScrollPane(textareaChat);
        scrollpaneChat.setBounds(15, 14, 420, 241);
        chatFrame.getContentPane().add(scrollpaneChat);

        //Chat Messages
        textareaChatMessage = new JTextArea("Please write your message here...");
        scrollpaneChatMessage = new JScrollPane(textareaChatMessage);
        scrollpaneChatMessage.setBounds(15, 267, 340, 130);
        chatFrame.getContentPane().add(scrollpaneChatMessage);


        //Label Name
        labelName = new JLabel("Username:");
        labelName.setForeground(Color.WHITE);
        labelName.setBounds(490, 215, 77, 16);
        chatFrame.getContentPane().add(labelName);

        //Label Server IP
        labelServerIP = new JLabel("Server IP:");
        labelServerIP.setForeground(Color.WHITE);
        labelServerIP.setBounds(490, 243, 65, 16);
        chatFrame.getContentPane().add(labelServerIP);

        //Label Server Port
        labelServerPort = new JLabel("Server Port:");
        labelServerPort.setForeground(Color.WHITE);
        labelServerPort.setBounds(490, 271, 77, 16);
        chatFrame.getContentPane().add(labelServerPort);

        //Label User List
        labelUserList = new JLabel("You can chat with:");
        labelUserList.setForeground(Color.WHITE);
        labelUserList.setBounds(490, 14, 200, 16);
        chatFrame.getContentPane().add(labelUserList);


        //Textfield Name
        textfieldUserName = new JTextField();
        textfieldUserName.setBounds(577, 209, 134, 28);
        chatFrame.getContentPane().add(textfieldUserName);
        textfieldUserName.setColumns(10);

        //Textfield Server IP
        textfieldServerIP = new JTextField();
        textfieldServerIP.setText("localhost");
        textfieldServerIP.setBounds(577, 237, 134, 28);
        chatFrame.getContentPane().add(textfieldServerIP);
        textfieldServerIP.setColumns(10);

        //Textfield Server Port
        textfieldServerPort = new JTextField();
        textfieldServerPort.setText("8080");
        textfieldServerPort.setBounds(577, 265, 134, 28);
        chatFrame.getContentPane().add(textfieldServerPort);
        textfieldServerPort.setColumns(10);


        //Button Send Chat Message
        buttonSendMessage = new JButton("Send");
        buttonSendMessage.setBackground(Color.LIGHT_GRAY);
        buttonSendMessage.setForeground(Color.BLACK);
        buttonSendMessage.addActionListener(e -> sendMessage());
        buttonSendMessage.setBounds(368, 350, 68, 49);
        chatFrame.getContentPane().add(buttonSendMessage);

        //Button Login
        buttonLogin = new JButton("Login");
        buttonLogin.setBackground(Color.LIGHT_GRAY);
        buttonLogin.setForeground(Color.BLACK);
        buttonLogin.addActionListener(e -> loginUser());
        buttonLogin.setBounds(586, 310, 60, 60);
        chatFrame.getContentPane().add(buttonLogin);

        //Button Logout
        buttonLogout = new JButton("Logout");
        buttonLogout.setBackground(Color.LIGHT_GRAY);
        buttonLogout.setForeground(Color.RED);
        buttonLogout.addActionListener(e -> logoutUser());
        buttonLogout.setBounds(650, 310, 60, 60);
        chatFrame.getContentPane().add(buttonLogout);



        //Separator vertical
        separatorVertical = new JSeparator();
        separatorVertical.setOrientation(SwingConstants.VERTICAL);
        separatorVertical.setForeground(Color.WHITE);
        separatorVertical.setBackground(Color.WHITE);
        separatorVertical.setBounds(455, 6, 12, 398);
        chatFrame.getContentPane().add(separatorVertical);


        //Initial Gui Setup
        textareaUserList.setEditable(false);
        textareaChat.setEnabled(false);
        textareaChat.setEditable(false);
        textareaChatMessage.setEnabled(false);
        textareaChatMessage.setEditable(false);
        buttonSendMessage.setEnabled(false);
        buttonLogout.setEnabled(false);


        chatFrame.pack();
        chatFrame.setVisible(true);
    }


    private void enableMessaging() {
        textareaChat.setEnabled(true);
        textareaChatMessage.setEnabled(true);
        textareaChatMessage.setEditable(true);
        textfieldUserName.setEnabled(false);
        textfieldServerIP.setEnabled(false);
        textfieldServerPort.setEnabled(false);
        buttonLogin.setEnabled(false);
        buttonLogout.setEnabled(true);
        buttonSendMessage.setEnabled(true);
    }


    private void disableMessaging() {
        buttonLogin.setEnabled(true);
        buttonLogout.setEnabled(false);
        buttonSendMessage.setEnabled(false);
        textfieldUserName.setEnabled(true);
        textfieldServerIP.setEnabled(true);
        textfieldServerPort.setEnabled(true);
        buttonLogin.setEnabled(true);
    }

    private void loginUser() {
        if (textfieldUserName.getText().equals("")) {
            textareaUserList.append("\nThat didn't work." + "\n" + "Please insert a User Name!");
        } else if (textfieldServerIP.getText().equals("")) {
            textareaUserList.append("\nThat didn't work." + "\n" + "Please insert a ServerIP!");
        } else if (textfieldServerPort.getText().equals("")) {
            textareaUserList.append("\nThat didn't work." + "\n" + "Please insert a ServerPort!");
        } else if (!textfieldUserName.getText().equals("") && !textfieldServerIP.getText().equals("") && !textfieldServerPort.getText().equals("")) {
            username = textfieldUserName.getText();
            serverIP = textfieldServerIP.getText();
            serverPort = textfieldServerPort.getText();

            clientController = new ClientController(username, serverIP, serverPort);

            try {
                if (clientController.login(username)) {
                    clientIsLoggedOut = false;
                    textareaUserList.setText("You've logged in successfully!" + "\n" + "You can start chatting," + username);
                    enableMessaging();
                    chatFrame.setTitle("Welcome to your Messenger, " + username);

                    listAllUsers();

                    clientController.initializeConnectionFactory();
                    clientController.lookupQueue();
                    clientController.lookupTopic();

                    getMessages();
                } else {
                    textareaUserList.setText("Login failed. Please try again!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            textareaChat.append("\nLogin failed. Please insert valid data");
        }
    }

    private void logoutUser() {
        textareaUserList.setText("Thank you for using Messenger, " + "\n" + username);
        textareaChat.setText("");
        disableMessaging();

        try {
            clientController.logout(username);
            clientIsLoggedOut = true;
        } catch (IOException e) {
            textareaUserList.setText("That didn't work." + "\n" + "Please try again!");
        }
    }


    private void listAllUsers() {

        Thread t1 = new Thread(() -> {
            while (true) {
                if (clientIsLoggedOut) {
                    break;
                }

                ArrayList names = new ArrayList();

                try {
                    names = clientController.getCurrentUsers();
                } catch (IOException e) {
                    textareaUserList.setText("Sorry, we currently can't fetch other user names");
                }

                StringBuilder stringbuilder = new StringBuilder();

                for (Object s : names) {
                    if (s.equals(username)) {
                        stringbuilder.append("*** YOU ***");
                        stringbuilder.append("\n");
                    } else {
                        stringbuilder.append(s);
                        stringbuilder.append("\n");
                    }
                }

                String userList = stringbuilder.toString();

                textareaUserList.setText(userList);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }


    private void sendMessage() {
        if (!textareaChatMessage.getText().equals("")) {
            String message = textareaChatMessage.getText();
            //TODO kafka option here
            clientController.sendMessage(message);
            textareaChatMessage.setText("");


        } else {
            textareaChat.append("#### We're sorry, but you can't send an empty message! ####\n");
            textareaChat.update(textareaChat.getGraphics());
        }
    }

    private void getMessages() {
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

                        StringBuilder sb = new StringBuilder();

                        if (chatName.equals(username)) {
                            sb.append("*** YOU ***");
                        } else {
                            sb.append(chatName);
                        }
                        sb.append(":\t" + chatMessage + "\n");

                        textareaChat.append(sb.toString());

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

                        StringBuilder sb = new StringBuilder();

                        if (chatName.equals(username)) {
                            sb.append("*** YOU ***");
                        } else {
                            sb.append(chatName);
                        }
                        sb.append(":\t" + chatMessage + "\n");

                        textareaChat.append(sb.toString());

                        System.out.println(message.getUserName() + ":" + message.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    @Override
    public void run() {
        this.initializeChatGui();
    }
}
