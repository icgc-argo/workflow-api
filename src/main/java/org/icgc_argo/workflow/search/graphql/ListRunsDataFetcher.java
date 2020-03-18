package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.model.RunListResponse;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListRunsDataFetcher implements DataFetcher<RunListResponse> {

  private RunService runService;

  @Autowired
  public ListRunsDataFetcher(@NonNull RunService runService) {
    this.runService = runService;
  }

  @Override
  public RunListResponse get(DataFetchingEnvironment environment) throws Exception {
    return runService.listRuns();
  }
}
