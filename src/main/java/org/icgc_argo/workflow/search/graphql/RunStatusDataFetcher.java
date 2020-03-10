package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.model.RunStatus;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RunStatusDataFetcher implements DataFetcher<RunStatus> {

  private RunService runService;
  private static final String RUN_ID = "runId";

  @Autowired
  public RunStatusDataFetcher(@NonNull RunService runService) {
    this.runService = runService;
  }

  @Override
  public RunStatus get(DataFetchingEnvironment environment) throws Exception {
    String runId = environment.getArgument(RUN_ID);
    val status = runService.getRunStatusById(runId);
    return status;
  }
}
