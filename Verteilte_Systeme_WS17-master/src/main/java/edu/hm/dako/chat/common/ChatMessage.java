package edu.hm.dako.chat.common;

import java.io.Serializable;

/**
 * Defines a chat message
 */
public class ChatMessage {
    /**
     * Username of sender
     */
    private final String userName;
    /**
     * The message itself
     */
    private final String message;
    /**
     * Thread of sending client
     */
    private final String clientThread;

    /**
     * Used server
     */
    private final String serverThread;

    /**
     * Creates a chat message with user name, message and client thread
     * @param userName sender name
     * @param message the message itself
     * @param clientThread thread of sender
     */
    public ChatMessage(String userName, String message, String clientThread, String serverThread) {
        this.userName = userName;
        this.message = message;
        this.clientThread = clientThread;
        this.serverThread = serverThread;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getClientThread() {
        return clientThread;
    }

    public String getServerThread() {
        return serverThread;
    }
}
