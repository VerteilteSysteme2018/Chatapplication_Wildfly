package org.chat.server.graphql;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.google.inject.Inject;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import org.chat.server.model.CountMRepository;
import org.chat.server.model.CountMapper;
import org.chat.server.model.TraceMRepository;
import org.chat.server.model.TraceMapper;

public class Query implements GraphQLResolver<Void> {

  private CountMRepository countMRepository;
  private TraceMRepository traceMRepository;

  public Query(CountMRepository countMRepository, TraceMRepository traceMRepository) {
    this.traceMRepository = traceMRepository;
    this.countMRepository = countMRepository;
  }

  public List<TraceMapper> allTrace() {
    return traceMRepository.findAll();
  }
  public List<CountMapper> allCount() {
    return countMRepository.findAll();
  }
}