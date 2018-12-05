package org.chat.server.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import org.chat.server.model.CountMRepository;
import org.chat.server.model.TraceMRepository;

public class Mutation implements GraphQLMutationResolver {

  private CountMRepository countMRepository;
  private TraceMRepository traceMRepository;

  public Mutation(CountMRepository countMRepository, TraceMRepository traceMRepository) {
    this.countMRepository = countMRepository;
    this.traceMRepository = traceMRepository;
  }

  public boolean clearCount() {
    countMRepository.clear();
    return true;
  }

  public int clearTrace() {
    return traceMRepository.clear();
  }
}