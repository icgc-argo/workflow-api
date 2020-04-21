package org.icgc_argo.workflow.search.graphql;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import java.util.List;
import java.util.Map;
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

  @SuppressWarnings("unchecked")
  public DataFetcher<List<Run>> getRunsDataFetcher() {
    return environment -> {
      val args = environment.getArguments();

      val filter = ImmutableMap.<String, Object>builder();
      val page = ImmutableMap.<String, Integer>builder();

      if (args != null) {
        if (args.get("filter") != null) filter.putAll((Map<String, Object>) args.get("filter"));
        if (args.get("page") != null) page.putAll((Map<String, Integer>) args.get("page"));
      }
      return runService.getRuns(filter.build(), page.build());
    };
  }

  public DataFetcher<Run> getNestedRunDataFetcher() {
    return environment -> {
      val task = (Task) environment.getSource();
      return runService.getRunById(task.getRunId());
    };
  }
}
