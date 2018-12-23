package org.chat.server.graphql;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.servlet.SimpleGraphQLServlet;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.chat.server.model.CountMRepository;
import org.chat.server.model.TraceMRepository;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;


import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

  @Inject
  public GraphQLEndpoint(CountMRepository countMRepository, TraceMRepository traceMRepository) {
    super(SchemaParser.newParser()
        .file("schema.graphqls")
        .resolvers(new Query(countMRepository, traceMRepository), new
            Mutation(countMRepository, traceMRepository))
        .build()
        .makeExecutableSchema());
  }

  @Override
  protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
    return errors.stream()
        .filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e))
        .map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
        .collect(Collectors.toList());
  }

}