package org.icgc_argo.workflow.search.graphql;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.IOException;
import java.net.URL;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GraphQLProvider {

  /** State */
  private GraphQL graphQL;

  private GraphQLSchema graphQLSchema;

  /** Dependencies */
  private final RunDataFetchers runDataFetchers;

  private final TaskDataFetchers taskDataFetchers;

  public GraphQLProvider(RunDataFetchers runDataFetchers, TaskDataFetchers taskDataFetchers) {
    this.runDataFetchers = runDataFetchers;
    this.taskDataFetchers = taskDataFetchers;
  }

  @Bean
  public GraphQL graphQL() {
    return graphQL;
  }

  @PostConstruct
  public void init() throws IOException {
    URL url = Resources.getResource("schema.graphqls");
    String sdl = Resources.toString(url, Charsets.UTF_8);
    graphQLSchema = buildSchema(sdl);
    this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
  }

  private GraphQLSchema buildSchema(String sdl) {
    TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
    RuntimeWiring runtimeWiring = buildWiring();
    SchemaGenerator schemaGenerator = new SchemaGenerator();
    return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
  }

  private RuntimeWiring buildWiring() {
    return RuntimeWiring.newRuntimeWiring()
        .scalar(ExtendedScalars.Json)
        .type(newTypeWiring("Query").dataFetcher("runs", runDataFetchers.getRunsDataFetcher()))
        .type(newTypeWiring("Query").dataFetcher("tasks", taskDataFetchers.getTasksDataFetcher()))
        .type(
            newTypeWiring("Run").dataFetcher("tasks", taskDataFetchers.getNestedTaskDataFetcher()))
        .type(newTypeWiring("Task").dataFetcher("run", runDataFetchers.getNestedRunDataFetcher()))
        .build();
  }
}
