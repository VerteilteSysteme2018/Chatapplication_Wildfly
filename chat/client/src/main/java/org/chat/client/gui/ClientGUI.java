package org.chat.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.chat.client.ClientController;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI implements Runnable {
    //JSON CONVERTER
    private final JsonConverter converter = new JsonConverter();

    //INTS - FRAME DIMENSIONS
    private final int frameWidth = 700;
    private final int frameHeight = 350;

    //STRINGS - CLIENT DATA
    private String username;
    private String serverIP;
    private String serverPort;

    //BOOLEANS
    private boolean clientLoggedOut;

    //CLIENT MANAGER
    private ClientController clientController;

    //BOOLEAN - CURSOR
    private boolean cursor;

    //MAIN FRAME
    private JFrame mainFrame;

    //LABELS
    private JLabel lblName;
    private JLabel lblServerIP;
    private JLabel lblServerPort;
    private JLabel lblUserList;

    //TEXTFIELDS
    private JTextField tfName;
    private JTextField tfServerIP;
    private JTextField tfServerPort;

    //BUTTONS
    private JButton btnLogin;
    private JButton btnLogout;

    //TEXTAREAS
    private JTextArea taUserList;

    //SCROLLPANES
    private JScrollPane spUserList;

    //SEPERATORS
    private JSeparator sepHori;
    private JSeparator sepVerti;
    private JScrollPane spChat;
    private JScrollPane spChatMessage;
    private JButton btnSendMessage;
    private JTextArea taChat;
    private JTextArea taChatMessage;


    /**
     * @wbp.parser.entryPoint
     */
    private void initGui() {
        //MAIN FRAME START
        mainFrame = new JFrame("M E S S E N G E R");
        //mainFrame.getContentPane().setForeground(Color.LIGHT_GRAY);
        mainFrame.getContentPane().setBackground(Color.DARK_GRAY);
        mainFrame.setResizable(false);
        mainFrame.getContentPane().setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int posX = (screenSize.width - frameWidth) / 2;
        int posY = (screenSize.height - frameHeight) / 3;
        mainFrame.setBounds(posX, posY, frameWidth, frameHeight);
        mainFrame.setPreferredSize(new Dimension(frameWidth, frameHeight));
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        /** mainFrame.addWindowListener(new WindowAdapter() {
            /**
             * Logout current user when closing the chat gui.
             * @param event Window closing event
             */
            //@Override
            /**public void windowClosing(WindowEvent event) {
                performLogout();
                mainFrame.dispose();
                System.exit(0);
            }
        });

             */


        //LABELS

        //Label Name
        lblName = new JLabel("Username:");
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(450, 196, 77, 16);
        mainFrame.getContentPane().add(lblName);

        //Label Server IP
        lblServerIP = new JLabel("Server IP:");
        lblServerIP.setForeground(Color.WHITE);
        lblServerIP.setBounds(450, 224, 65, 16);
        mainFrame.getContentPane().add(lblServerIP);

        //Label Server Port
        lblServerPort = new JLabel("Server Port:");
        lblServerPort.setForeground(Color.WHITE);
        lblServerPort.setBounds(450, 252, 77, 16);
        mainFrame.getContentPane().add(lblServerPort);

        //Label User List
        lblUserList = new JLabel("You can chat with:");
        lblUserList.setForeground(Color.WHITE);
        lblUserList.setBounds(450, 14, 200, 16);
        mainFrame.getContentPane().add(lblUserList);

        //TEXTFIELDS

        //Textfield Name
        tfName = new JTextField();
        tfName.setBounds(537, 190, 134, 28);
        mainFrame.getContentPane().add(tfName);
        tfName.setColumns(10);

        //Textfield Server IP
        tfServerIP = new JTextField();
        tfServerIP.setText("localhost");
        tfServerIP.setBounds(537, 218, 134, 28);
        mainFrame.getContentPane().add(tfServerIP);
        tfServerIP.setColumns(10);

        //Textfield Server Port
        tfServerPort = new JTextField();
        tfServerPort.setText("8080");
        tfServerPort.setBounds(537, 246, 134, 28);
        mainFrame.getContentPane().add(tfServerPort);
        tfServerPort.setColumns(10);


        //BUTTONS

        //Button Login
        btnLogin = new JButton("Login");
        btnLogin.setBackground(Color.LIGHT_GRAY);
        btnLogin.setForeground(Color.BLACK);
        btnLogin.addActionListener(e -> performLogin());
        btnLogin.setBounds(556, 280, 117, 29);
        mainFrame.getContentPane().add(btnLogin);

        //Button Logout
        btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.LIGHT_GRAY);
        btnLogout.setForeground(Color.BLACK);
        btnLogout.addActionListener(e -> performLogout());
        btnLogout.setBounds(442, 280, 117, 29);
        btnLogout.setEnabled(false);
        mainFrame.getContentPane().add(btnLogout);

        //Button Send Chat Message
        btnSendMessage = new JButton("Send");
        btnSendMessage.setBackground(Color.LIGHT_GRAY);
        btnSendMessage.setForeground(Color.BLACK);
        btnSendMessage.addActionListener(e -> sendMessage());
        btnSendMessage.setEnabled(false);
        btnSendMessage.setBounds(368, 265, 68, 48);
        mainFrame.getContentPane().add(btnSendMessage);



        //TEXTAREAS

        //TextArea User List
        taUserList = new JTextArea();
        taUserList.setEditable(false);

        //TextArea Chat Frame
        taChat = new JTextArea();
        taChat.setEnabled(false);
        taChat.setEditable(false);

        //TextArea Chat Message
        taChatMessage = new JTextArea("Please write your message here...");
        taChatMessage.setEnabled(false);
        taChatMessage.setEditable(false);


        //SCROLLPANES

        //ScrollPane User List
        spUserList = new JScrollPane(taUserList);
        spUserList.setBounds(450, 32, 220, 152);
        mainFrame.getContentPane().add(spUserList);

        //ScrollPane Chat Frame
        spChat = new JScrollPane(taChat);
        spChat.setBounds(15, 14, 420, 241);
        mainFrame.getContentPane().add(spChat);

        //ScrollPane ChatMessage
        spChatMessage = new JScrollPane(taChatMessage);
        spChatMessage.setBounds(15, 267, 340, 43);
        mainFrame.getContentPane().add(spChatMessage);


        //SEPARATORS

        //Separator horizontal
        sepHori = new JSeparator();
        sepHori.setBackground(Color.WHITE);
        sepHori.setForeground(Color.WHITE);
        sepHori.setOrientation(SwingConstants.HORIZONTAL);
        sepHori.setBounds(440, 182, 235, 16);
        //mainFrame.getContentPane().add(sepHori);

        //Separator vertical
        sepVerti = new JSeparator();
        sepVerti.setForeground(Color.WHITE);
        sepVerti.setBackground(Color.WHITE);
        sepVerti.setOrientation(SwingConstants.VERTICAL);
        sepVerti.setBounds(435, 6, 12, 316);
        mainFrame.getContentPane().add(sepVerti);


        //MAINFRAME END
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void changeCursor() {
        if (cursor) {
            mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            cursor = false;
        } else {
            mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            cursor = true;
        }
    }

    private void performLogin() {
        changeCursor();

        if (tfName.getText() != null && tfServerIP.getText() != null && tfServerPort.getText() != null) {
            username = tfName.getText();
            serverIP = tfServerIP.getText();
            serverPort = tfServerPort.getText();

            clientController = new ClientController(username, serverIP, serverPort);

            try {
                if (clientController.login(username)) {
                    taUserList.setText("Login successfull!");
                    enableGui();
                    mainFrame.setTitle("M E S S E N G E R   OF   " + "### " + username + " ###");

                    clientLoggedOut = false;
                    //startUserListListener();

                    clientController.subscribeTopic();
                    //receiveAndDisplayMessages();
                } else {
                    taUserList.setText("Login failed. Please try again!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
                taUserList.append("\nLogin failed. Please insert valid data");
        }
        changeCursor();
    }


    private void enableGui() {
        tfName.setEnabled(false);
        tfServerIP.setEnabled(false);
        tfServerPort.setEnabled(false);
        btnLogin.setEnabled(false);
        btnLogout.setEnabled(true);
        btnSendMessage.setEnabled(true);
        taChat.setEnabled(true);
        taChatMessage.setEnabled(true);
        taChatMessage.setEditable(true);
    }


    private void performLogout() {
            taChat.setText("");
            taUserList.setText("Thank you " + username + " for using Messenger");
            disableGui();

        try {
            clientController.logout(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientLoggedOut = true;
    }

    private void disableGui() {
        btnLogin.setEnabled(true);
        btnLogout.setEnabled(false);
        btnSendMessage.setEnabled(false);
        tfName.setEnabled(true);
        tfServerIP.setEnabled(true);
        tfServerPort.setEnabled(true);
        btnLogin.setEnabled(true);
    }

   private void startUserListListener() {

            Thread t1 = new Thread(() -> {
                while (true) {
                    if (clientLoggedOut) {
                        break;
                    }

                    ArrayList names = null;
                    try {
                        names = clientController.getCurrentUsers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //String[] names = getNamesFromJson(userList);
                    StringBuilder sb = new StringBuilder();

                    for (Object s : names) {
                        if (s.equals(username)) {
                            sb.append("*" + s + "*");
                        } else {
                            sb.append(s);
                        }
                        sb.append("\n");
                    }

                    taUserList.setText(sb.toString());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t1.start();
    }


    private String[] getNamesFromJson(String json) {
        List<String> userList = converter.formatToList(json);
        return converter.getNames(userList);
    }

    private void sendMessage() {
            if (!taChatMessage.getText().trim().equals("")) {
                String message = taChatMessage.getText();
                clientController.sendMessage(message);
                taChatMessage.setText("");


            } else {
                taChat.append("--WARNING: You can't send an empty message!\n");
                taChat.update(taChat.getGraphics());
            }
        }

    /*private void receiveAndDisplayMessages() {
        Thread t1 = new Thread(() -> {
            JMSContext context = clientManager.getJMSContext();
            Destination topic = clientManager.getTopic();
            JMSConsumer consumer = context.createConsumer(topic);

            while (true) {
                if (clientLoggedOut) {
                    break;
                }

                String messages = consumer.receiveBody(String.class, 5000);

                if (messages != null) {
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(messages, JsonObject.class);

                    String name = json.get("userName").getAsString();
                    String message = json.get("message").getAsString();

                    StringBuilder sb = new StringBuilder();

                    if (name.equals(username)) {
                        sb.append("*" + name + "*");
                    } else {
                        sb.append(name);
                    }
                    sb.append(":\t" + message + "\n");

                    taChat.append(sb.toString());
                }


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }

    */


    @Override
    public void run() {
        this.initGui();
    }
}
