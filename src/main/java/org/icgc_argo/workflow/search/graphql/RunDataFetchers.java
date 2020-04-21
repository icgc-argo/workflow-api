package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.model.graphql.Task;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunDataFetchers {

  /** Dependency */
  private final RunService runService;

  @Autowired
  public RunDataFetchers(RunService runService) {
    this.runService = runService;
  }

  public DataFetcher<List<Run>> getRunsDataFetcher() {
    return environment -> {
      val args = environment.getArguments();
      return runService.getRuns(args);
    };
  }

  public DataFetcher<Run> getNestedRunDataFetcher() {
    return environment -> {
      val task = (Task) environment.getSource();
      return runService.getRunById(task.getRunId());
    };
  }
}
