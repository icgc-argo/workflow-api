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
public class RunLog {
  @JsonProperty("run_id")
  @NonNull
  private String runId;

  @JsonProperty("request")
  private RunRequest request;

  @JsonProperty("state")
  private State state;

  @JsonProperty("run_log")
  private Log runLog;

  @JsonProperty("task_logs")
  private List<Log> taskLogs;

  @JsonProperty("outputs")
  private Object outputs;
}
