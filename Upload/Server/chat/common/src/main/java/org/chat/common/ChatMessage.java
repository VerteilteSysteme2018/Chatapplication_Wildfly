package org.chat.common;

import java.io.Serializable;

/**
 * Class which represents a chat message.
 */
public class ChatMessage implements Serializable {

    private String message;

    private String userName;

    private long timestamp;

    private String clientThread;

    private String serverThread;

    public ChatMessage(String user, String message, long timestamp,
        String clientThread, String serverThread) {
        this.userName = user;
        this.message = message;
        this.timestamp = timestamp;
        this.clientThread = clientThread;
        this.serverThread = serverThread;
    }

    public ChatMessage(long timestamp) {
        this.timestamp = timestamp;
    }

    public ChatMessage() {

    }

    //Getter and Setter

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //generated code for equals, hashCode and toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatMessage that = (ChatMessage) o;

        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        return userName != null ? userName.equals(that.userName) : that.userName == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "message='" + message + '\'' +
                ", userName='" + userName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", clientThread='" + clientThread + '\'' +
                ", serverThread='" + serverThread + '\'' +
                '}';
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setClientThread(String clientThread) {
        this.clientThread = clientThread;
    }

    public String getClientThread() {
        return this.clientThread;
    }

    public void setServerThread(String serverThread) {
        this.serverThread = serverThread;
    }

    public String getServerThread() {
        return this.serverThread;
    }
}
