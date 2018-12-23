package org.chat.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.chat.databases.Count;
import org.chat.databases.CountRepository;

public class CountMRepository {
  @Inject
  CountRepository countRepository;

  public CountMapper findByUserName(String username) {
    Count count = countRepository.findByUserName(username);
    CountMapper cm = new CountMapper();
    cm.setCounting(count.getCounting());
    cm.setId(count.getId());
    cm.setUsername(count.getUsername());
    return cm;
  }

  public List<CountMapper> findAll() {
    List<Count> countlist = countRepository.findAll();
    List<CountMapper> resultlist = new ArrayList<>();
    for (Count c : countlist) {
      CountMapper cm = new CountMapper();
      cm.setCounting(c.getCounting());
      cm.setId(c.getId());
      cm.setUsername(c.getUsername());
      resultlist.add(cm);
    }
    return resultlist;
  }

  public int clear() {
    return countRepository.clear();
  }
}