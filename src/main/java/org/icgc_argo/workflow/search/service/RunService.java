package org.icgc_argo.workflow.search.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.springframework.stereotype.Service;

@Service
public class RunService {

  private final RunRepository runRepository;

  public RunService(RunRepository runRepository) {
    this.runRepository = runRepository;
  }

  public List<Run> getRuns(Map<String, Object> args) {
    val response =
        (args == null || args.size() == 0) ? runRepository.getRuns() : runRepository.getRuns(args);
    val hitStream = Arrays.stream(response.getHits().getHits());
    return hitStream.map(RunService::hitToRun).collect(toUnmodifiableList());
  }

  public Run getRunById(String runId) {
    val response = runRepository.getRuns(Map.of("runId", runId));
    val runOpt = Arrays.stream(response.getHits().getHits()).map(RunService::hitToRun).findFirst();
    return runOpt.orElse(null);
  }

  private static Run hitToRun(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return Run.parse(sourceMap);
  }
}
