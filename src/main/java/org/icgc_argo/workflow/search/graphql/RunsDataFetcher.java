package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.model.wes.ServiceInfo;
import org.icgc_argo.workflow.search.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetRunsDataFetcher implements DataFetcher<List<Run>> {

  /**
   * Dependency
   */
  private final WorkflowService workflowService;

  @Autowired
  public GetRunsDataFetcher(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @Override
  public List<Run> get(DataFetchingEnvironment environment) throws Exception {
    return workflowService.getRuns();
  }

}
