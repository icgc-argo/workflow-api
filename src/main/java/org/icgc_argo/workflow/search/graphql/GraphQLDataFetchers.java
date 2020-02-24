package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import lombok.NonNull;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GraphQLDataFetchers {

  private RunService runService;

  @Autowired
  public GraphQLDataFetchers(@NonNull RunService runService) {
    this.runService = runService;
  }

  public DataFetcher getRunByRunName() {
    return dataFetchingEnvironment -> {
      String runName = dataFetchingEnvironment.getArgument("runName");
      return runService.getWorkflowDocumentById(runName).get();
    };
  }
}
