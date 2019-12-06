package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RunResponse {
  @JsonProperty("run_id")
  @NonNull
  private String runId;

  @JsonProperty("request")
  private RunRequest request;

  @JsonProperty("state")
  private State state;

  @JsonProperty("run_log")
  private RunLog runLog;

  @JsonProperty("task_logs")
  private List<TaskLog> taskLogs;

  @JsonProperty("outputs")
  private Object outputs;
}
