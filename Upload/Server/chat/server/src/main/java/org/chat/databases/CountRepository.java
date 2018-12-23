package org.chat.databases;

import java.util.List;

public interface CountRepository {

  int clear();
  Count findByUserName(String username);
  List<Count> findAll();
  void updateCount(String username);

}
