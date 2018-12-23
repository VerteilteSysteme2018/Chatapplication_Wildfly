package org.chat.databases;

import java.util.List;

public interface TraceRepository {

  Trace findByUserName(String username);
  List<Trace> findAll();
  void create(Trace trace);
  int clear();

}
