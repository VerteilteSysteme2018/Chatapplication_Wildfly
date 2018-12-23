package org.chat.server.model;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.chat.databases.Trace;
import org.chat.databases.TraceRepository;

public class TraceMRepository {
  @Inject
  TraceRepository traceRepository;

  public int clear() {
    return traceRepository.clear();
  }

  public List<TraceMapper> findAll() {
    List<Trace> tracelist = traceRepository.findAll();
    List<TraceMapper> resutlist = new ArrayList<>();
    if (!tracelist.isEmpty()) {
      for (Trace t : tracelist) {
        TraceMapper tm = new TraceMapper();
        tm.setId(t.getId());
        tm.setClientthread(t.getClientThread());
        tm.setMessage(t.getMessage());
        tm.setUsername(t.getUserName());
        tm.setServerthread(t.getServerthread());
        resutlist.add(tm);
      }
    }
    return resutlist;
  }
}
