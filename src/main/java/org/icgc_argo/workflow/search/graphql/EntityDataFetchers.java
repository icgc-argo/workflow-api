package org.icgc_argo.workflow.search.graphql;

import static java.util.stream.Collectors.toList;

import com.apollographql.federation.graphqljava._Entity;
import graphql.schema.DataFetcher;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EntityDataFetchers {

  public static final String RUN_ENTITY = "Run";

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
                    final Object runName = values.get("runName");
                    if (runName instanceof String) {
                      return runService.getRunByName((String) runName);
                    }
                  }
                  return null;
                })
            .collect(toList());
  }
}
