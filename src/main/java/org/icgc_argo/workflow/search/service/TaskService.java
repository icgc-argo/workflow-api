package org.icgc_argo.workflow.search.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.graphql.Task;
import org.icgc_argo.workflow.search.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public List<Task> getTasks(String runId, Map<String, Object> filter, Map<String, Integer> page) {
    val mergedBuilder = ImmutableMap.<String, Object>builder();
    if (runId != null) {
      mergedBuilder.put("runId", runId);
    }
    if (filter != null && filter.size() > 0) {
      mergedBuilder.putAll(filter);
    }
    val merged = mergedBuilder.build();

    val response = taskRepository.getTasks(merged, page);
    val hitStream = Arrays.stream(response.getHits().getHits());
    return hitStream.map(TaskService::hitToTask).collect(toUnmodifiableList());
  }

  private static Task hitToTask(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return Task.parse(sourceMap);
  }
}
