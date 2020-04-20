package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunsDataFetcher implements DataFetcher<List<Run>> {

  /** Dependency */
  private final WorkflowService workflowService;

  @Autowired
  public RunsDataFetcher(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  @Override
  public List<Run> get(DataFetchingEnvironment environment) throws Exception {
    val args = environment.getArguments();
    log.debug(args.toString());

    return workflowService.getRuns(args);
  }
}
