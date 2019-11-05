package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/** RunLog */
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
public class RunLog {
  @JsonProperty("run_id")
  private String runId = null;

  @JsonProperty("request")
  private RunRequest request = null;

  @JsonProperty("state")
  private State state = null;

  @JsonProperty("run_log")
  private Log runLog = null;

  @JsonProperty("task_logs")
  @Valid
  private List<Log> taskLogs = null;

  @JsonProperty("outputs")
  private Object outputs = null;

  public RunLog runId(String runId) {
    this.runId = runId;
    return this;
  }

  /**
   * workflow run ID
   *
   * @return runId
   */
  @ApiModelProperty(value = "workflow run ID")
  public String getRunId() {
    return runId;
  }

  public void setRunId(String runId) {
    this.runId = runId;
  }

  public RunLog request(RunRequest request) {
    this.request = request;
    return this;
  }

  /**
   * The original request message used to initiate this execution.
   *
   * @return request
   */
  @ApiModelProperty(value = "The original request message used to initiate this execution.")
  @Valid
  public RunRequest getRequest() {
    return request;
  }

  public void setRequest(RunRequest request) {
    this.request = request;
  }

  public RunLog state(State state) {
    this.state = state;
    return this;
  }

  /**
   * The state of the run e.g. RUNNING (see State)
   *
   * @return state
   */
  @ApiModelProperty(value = "The state of the run e.g. RUNNING (see State)")
  @Valid
  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public RunLog runLog(Log runLog) {
    this.runLog = runLog;
    return this;
  }

  /**
   * The logs, and other key info like timing and exit code, for the overall run of this workflow.
   *
   * @return runLog
   */
  @ApiModelProperty(
      value =
          "The logs, and other key info like timing and exit code, for the overall run of this workflow.")
  @Valid
  public Log getRunLog() {
    return runLog;
  }

  public void setRunLog(Log runLog) {
    this.runLog = runLog;
  }

  public RunLog taskLogs(List<Log> taskLogs) {
    this.taskLogs = taskLogs;
    return this;
  }

  public RunLog addTaskLogsItem(Log taskLogsItem) {
    if (this.taskLogs == null) {
      this.taskLogs = new ArrayList<>();
    }
    this.taskLogs.add(taskLogsItem);
    return this;
  }

  /**
   * The logs, and other key info like timing and exit code, for each step in the workflow run.
   *
   * @return taskLogs
   */
  @ApiModelProperty(
      value =
          "The logs, and other key info like timing and exit code, for each step in the workflow run.")
  @Valid
  public List<Log> getTaskLogs() {
    return taskLogs;
  }

  public void setTaskLogs(List<Log> taskLogs) {
    this.taskLogs = taskLogs;
  }

  public RunLog outputs(Object outputs) {
    this.outputs = outputs;
    return this;
  }

  /**
   * The outputs from the workflow run.
   *
   * @return outputs
   */
  @ApiModelProperty(value = "The outputs from the workflow run.")
  public Object getOutputs() {
    return outputs;
  }

  public void setOutputs(Object outputs) {
    this.outputs = outputs;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RunLog runLog = (RunLog) o;
    return Objects.equals(this.runId, runLog.runId)
        && Objects.equals(this.request, runLog.request)
        && Objects.equals(this.state, runLog.state)
        && Objects.equals(this.runLog, runLog.runLog)
        && Objects.equals(this.taskLogs, runLog.taskLogs)
        && Objects.equals(this.outputs, runLog.outputs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(runId, request, state, runLog, taskLogs, outputs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RunLog {\n");

    sb.append("    runId: ").append(toIndentedString(runId)).append("\n");
    sb.append("    request: ").append(toIndentedString(request)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    runLog: ").append(toIndentedString(runLog)).append("\n");
    sb.append("    taskLogs: ").append(toIndentedString(taskLogs)).append("\n");
    sb.append("    outputs: ").append(toIndentedString(outputs)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
