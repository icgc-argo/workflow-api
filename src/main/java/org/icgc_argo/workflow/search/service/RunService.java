package org.icgc_argo.workflow.search.service;

import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.icgc_argo.workflow.search.model.SearchFields.ANALYSIS_ID;
import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;

@Service
public class RunService {

  private final RunRepository runRepository;

  public RunService(RunRepository runRepository) {
    this.runRepository = runRepository;
  }

  private static Run hitToRun(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return Run.parse(sourceMap);
  }

  public List<Run> getRuns(Map<String, Object> filter, Map<String, Integer> page) {
    val response = runRepository.getRuns(filter, page);
    val hitStream = Arrays.stream(response.getHits().getHits());
    return hitStream.map(RunService::hitToRun).collect(toUnmodifiableList());
  }

  public Run getRunByRunId(String runId) {
    val response = runRepository.getRuns(Map.of(RUN_ID, runId), null);
    val runOpt = Arrays.stream(response.getHits().getHits()).map(RunService::hitToRun).findFirst();
    return runOpt.orElse(null);
  }

  public List<Run> getRunByAnalysisId(String analysisId) {
    val response = runRepository.getRuns(Map.of(ANALYSIS_ID, analysisId), null);
    val hitStream = Arrays.stream(response.getHits().getHits());
    return hitStream.map(RunService::hitToRun).collect(toUnmodifiableList());
  }
}
