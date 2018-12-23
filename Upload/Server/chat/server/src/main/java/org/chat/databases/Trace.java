package org.chat.databases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.wildfly.common.annotation.NotNull;

@Entity(name= "Trace")
@Table (name = "trace", schema = "tracedb")
public class Trace {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  @Column(name = "username")
  private String username;

  @Column(name = "clientthread")
  private String clientThread;

  @NotNull
  @Column(name = "message")
  private String message;

  @Column(name = "serverthread")
  private String serverthread;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUserName() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getClientThread() {
    return this.clientThread;
  }

  public void setClientThread(String clientThread) {
    this.clientThread = clientThread;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getServerthread() {
    return this.serverthread;
  }

  public void setServerthread(String serverthread) {
    this.serverthread = serverthread;
  }

  @Override
  public String toString() {
    return "Trace{username='" + this.username + "'" +
        "message='" + this.message + "'" +
        "clientThread='" +this.clientThread + "'}";
  }
}
