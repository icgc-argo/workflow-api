package org.icgc_argo.workflow.search.graphql;

import com.apollographql.federation.graphqljava._Entity;
import graphql.schema.DataFetcher;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.model.graphql.Analysis;
import org.icgc_argo.workflow.search.model.graphql.Workflow;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class EntityDataFetchers {

  public static final String RUN_ENTITY = "Run";
  public static final String ANALYSIS_ENTITY = "Analysis";
  public static final String WORKFLOW_ENTITY = "Workflow";

  /** Dependency */
  private final RunService runService;

  public EntityDataFetchers(RunService runService) {
    this.runService = runService;
  }

  public DataFetcher getDataFetcher() {
    return environment ->
        environment.<List<Map<String, Object>>>getArgument(_Entity.argumentName).stream()
            .map(
                values -> {
                  if (RUN_ENTITY.equals(values.get("__typename"))) {
                    final Object runId = values.get("runId");
                    if (runId instanceof String) {
                      return runService.getRunByRunId((String) runId);
                    }
                  }
                  if (ANALYSIS_ENTITY.equals(values.get("__typename"))) {
                    final Object analysisId = values.get("analysisId");
                    if (analysisId instanceof String) {
                      return new Analysis(
                          (String) analysisId, runService.getRunByAnalysisId((String) analysisId));
                    }
                  }
                  if (WORKFLOW_ENTITY.equals(values.get("__typename"))) {
                    final Object runId = values.get("runId");
                    if (runId instanceof String) {
                      return new Workflow((String) runId, runService.getRunByRunId((String) runId));
                    }
                  }
                  return null;
                })
            .collect(toList());
  }
}
