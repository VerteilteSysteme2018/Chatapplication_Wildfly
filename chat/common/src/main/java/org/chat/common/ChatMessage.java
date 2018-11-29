package org.chat.common;

import java.io.Serializable;

/**
 * Class which represents a chat message.
 */
public class ChatMessage implements Serializable {

    private String message;

    private String userName;

    public ChatMessage(String user, String message) {
        this.userName = user;
        this.message = message;
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
                '}';
    }
}
