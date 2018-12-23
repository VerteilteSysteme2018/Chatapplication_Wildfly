package org.chat.server.model;

public class CountMapper {

  private int id;
  private String username;
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
}
