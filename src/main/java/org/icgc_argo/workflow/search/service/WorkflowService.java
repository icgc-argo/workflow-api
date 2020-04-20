package org.icgc_argo.workflow.search.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

  private final RunRepository runRepository;

  @Autowired
  public WorkflowService(RunRepository runRepository) {
    this.runRepository = runRepository;
  }

  public List<Run> getRuns(Map<String, Object> args) {
    val getResponse = args.size() == 0 ? runRepository.getRuns() : runRepository.getRuns(args);

    val hitStream = Arrays.stream(getResponse.getHits().getHits());
    return hitStream.map(WorkflowService::hitToRun).collect(toUnmodifiableList());
  }

  private static Run hitToRun(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return Run.parse(sourceMap);
  }
}
