package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.model.RunResponse;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunDataFetcher implements DataFetcher<RunResponse> {

  private RunService runService;
  private static final String RUN_ID = "runId";

  @Autowired
  public RunDataFetcher(@NonNull RunService runService) {
    this.runService = runService;
  }

  @Override
  public RunResponse get(DataFetchingEnvironment environment) throws Exception {
    String runId = environment.getArgument(RUN_ID);
    val doc = runService.getRunLog(runId);
    return doc;
  }
}
