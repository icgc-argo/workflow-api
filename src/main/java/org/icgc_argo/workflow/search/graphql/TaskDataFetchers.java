package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.model.graphql.Task;
import org.icgc_argo.workflow.search.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskDataFetchers {

  /** Dependency */
  private final TaskService taskService;

  @Autowired
  public TaskDataFetchers(TaskService taskService) {
    this.taskService = taskService;
  }

  public DataFetcher<List<Task>> getTasksDataFetcher() {
    return environment -> {
      val args = environment.getArguments();
      return taskService.getTasks(null, args);
    };
  }

  public DataFetcher<List<Task>> getNestedTaskDataFetcher() {
    return environment -> {
      val args = environment.getArguments();
      val runId = ((Run) environment.getSource()).getRunId();
      return taskService.getTasks(runId, args);
    };
  }
}
