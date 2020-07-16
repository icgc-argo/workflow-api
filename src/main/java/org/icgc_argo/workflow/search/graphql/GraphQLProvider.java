package org.icgc_argo.workflow.search.graphql;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import com.apollographql.federation.graphqljava.Federation;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import java.io.IOException;
import java.net.URL;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.config.websecurity.AuthProperties;
import org.icgc_argo.workflow.search.graphql.security.AddSecurityContextDecorator;
import org.icgc_argo.workflow.search.graphql.security.VerifyAuthQueryExecutionDecorator;
import org.icgc_argo.workflow.search.model.graphql.Analysis;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.model.graphql.Workflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
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

  private final EntityDataFetchers entityDataFetchers;

  private final AuthProperties authProperties;

  public GraphQLProvider(
          RunDataFetchers runDataFetchers,
          TaskDataFetchers taskDataFetchers,
          EntityDataFetchers entityDataFetchers,
          AuthProperties authProperties) {
    this.runDataFetchers = runDataFetchers;
    this.taskDataFetchers = taskDataFetchers;
    this.entityDataFetchers = entityDataFetchers;
    this.authProperties = authProperties;
  }

  @Bean
  @Profile("!secure")
  public GraphQL graphQL() {
    return graphQL;
  }


    @Bean
    @Profile("secure")
    public GraphQL secureGraphQL() {
        return graphQL.transform(this::toSecureGraphql);
    }

    private void toSecureGraphql(GraphQL.Builder graphQLBuilder) {
        // For more info on `Execution Strategies` see: https://www.graphql-java.codm/documentation/v15/execution/
        graphQLBuilder.queryExecutionStrategy(
               new AddSecurityContextDecorator(
                       new VerifyAuthQueryExecutionDecorator(new AsyncExecutionStrategy(), queryScopesToCheck())
               ));
    }

  @PostConstruct
  public void init() throws IOException {
    URL url = Resources.getResource("schema.graphqls");
    String sdl = Resources.toString(url, Charsets.UTF_8);
    graphQLSchema = buildSchema(sdl);
    this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
  }

  private GraphQLSchema buildSchema(String sdl) {
    return Federation.transform(sdl, buildWiring())
        .fetchEntities(entityDataFetchers.getDataFetcher())
        .resolveEntityType(
            typeResolutionEnvironment -> {
              final Object src = typeResolutionEnvironment.getObject();
              if (src instanceof Run) {
                return typeResolutionEnvironment
                    .getSchema()
                    .getObjectType(EntityDataFetchers.RUN_ENTITY);
              }
              if (src instanceof Analysis) {
                return typeResolutionEnvironment
                    .getSchema()
                    .getObjectType(EntityDataFetchers.ANALYSIS_ENTITY);
              }
              if (src instanceof Workflow) {
                return typeResolutionEnvironment
                    .getSchema()
                    .getObjectType(EntityDataFetchers.WORKFLOW_ENTITY);
              }
              return null;
            })
        .build();
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

    private ImmutableList<String> queryScopesToCheck() {
        return ImmutableList.copyOf(
                Iterables.concat(
                        authProperties.getGraphqlScopes().getQueryOnly(),
                        authProperties.getGraphqlScopes().getQueryAndMutation()
                ));
    }
}
