package org.chat.databases;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.wildfly.common.annotation.NotNull;

@Entity (name= "Count")
@Table (name = "countdata", schema = "countdb")
public class Count {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  @Column(name = "username")
  private String username;

  @Column(name = "counting")
  private int counting;

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

  public int getCounting() {
    return counting;
  }

  public void setCounting(int counting) {
    this.counting = counting;
  }

  @Override
  public String toString() {
    return "Count{" +
        "userName='" + username + '\'' +
        "counter='" + counting + '\'' +
        "}";
  }

}
