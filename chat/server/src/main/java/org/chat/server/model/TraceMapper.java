package org.chat.server.model;

public class TraceMapper {

  private int id;
  private String username;
  private String clientthread;
  private String message;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getClientthread() {
    return clientthread;
  }

  public void setClientthread(String clientthread) {
    this.clientthread = clientthread;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
