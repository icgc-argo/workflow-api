package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/** Small description of a workflow run, returned by server during listing */
@ApiModel(description = "Small description of a workflow run, returned by server during listing")
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
public class RunStatus {

  @JsonProperty("run_id")
  private String runId = null;

  @JsonProperty("state")
  private State state = null;

  public RunStatus runId(String runId) {
    this.runId = runId;
    return this;
  }

  /**
   * Get runId
   *
   * @return runId
   */
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getRunId() {
    return runId;
  }

  public void setRunId(String runId) {
    this.runId = runId;
  }

  public RunStatus state(State state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   *
   * @return state
   */
  @ApiModelProperty(value = "")
  @Valid
  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RunStatus runStatus = (RunStatus) o;
    return Objects.equals(this.runId, runStatus.runId)
        && Objects.equals(this.state, runStatus.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(runId, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RunStatus {\n");

    sb.append("    runId: ").append(toIndentedString(runId)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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
