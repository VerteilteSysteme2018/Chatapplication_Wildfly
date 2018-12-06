package org.chat.server.graphql;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.servlet.SimpleGraphQLServlet;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import org.chat.server.model.CountMRepository;
import org.chat.server.model.TraceMRepository;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

  /*public GraphQLEndpoint() {
    super(SchemaParser.newParser()
        .file("schema.graphqls")
        .build()
        .makeExecutableSchema());
  }*/

  @Inject
  public GraphQLEndpoint(CountMRepository countMRepository, TraceMRepository traceMRepository) {
    super(SchemaParser.newParser()
        .file("schema.graphqls")
        .resolvers(new Query(countMRepository, traceMRepository), new
            Mutation(countMRepository, traceMRepository))
        .build()
        .makeExecutableSchema());
  }

}