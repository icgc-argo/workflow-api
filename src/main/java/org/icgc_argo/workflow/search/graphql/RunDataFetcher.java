package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.NonNull;
import lombok.val;
import org.icgc_argo.workflow.search.graphql.model.Run;
import org.icgc_argo.workflow.search.service.RunService;
import org.icgc_argo.workflow.search.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunDataFetcher implements DataFetcher<Run> {

  private RunService runService;
  private static final String RUN_ID = "runId";

  @Autowired
  public RunDataFetcher(@NonNull RunService runService) {
    this.runService = runService;
  }

  @Override
  public Run get(DataFetchingEnvironment environment) throws Exception {
    String runId = environment.getArgument(RUN_ID);
    val doc = runService.getWorkflowDocumentById(runId).get();
    return Converter.buildRun(doc);
  }
}
