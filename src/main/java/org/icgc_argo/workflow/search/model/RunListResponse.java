package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/** The service will return a RunListResponse when receiving a successful RunListRequest. */
@ApiModel(
    description =
        "The service will return a RunListResponse when receiving a successful RunListRequest.")
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
public class RunListResponse {

  @JsonProperty("runs")
  @Valid
  private List<RunStatus> runs = null;

  @JsonProperty("next_page_token")
  private String nextPageToken = null;

  public RunListResponse runs(List<RunStatus> runs) {
    this.runs = runs;
    return this;
  }

  public RunListResponse addRunsItem(RunStatus runsItem) {
    if (this.runs == null) {
      this.runs = new ArrayList<>();
    }
    this.runs.add(runsItem);
    return this;
  }

  /**
   * A list of workflow runs that the service has executed or is executing. The list is filtered to
   * only include runs that the caller has permission to see.
   *
   * @return runs
   */
  @ApiModelProperty(
      value =
          "A list of workflow runs that the service has executed or is executing. The list is filtered to only include runs that the caller has permission to see.")
  @Valid
  public List<RunStatus> getRuns() {
    return runs;
  }

  public void setRuns(List<RunStatus> runs) {
    this.runs = runs;
  }

  public RunListResponse nextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
    return this;
  }

  /**
   * A token which may be supplied as `page_token` in workflow run list request to get the next page
   * of results. An empty string indicates there are no more items to return.
   *
   * @return nextPageToken
   */
  @ApiModelProperty(
      value =
          "A token which may be supplied as `page_token` in workflow run list request to get the next page of results.  An empty string indicates there are no more items to return.")
  public String getNextPageToken() {
    return nextPageToken;
  }

  public void setNextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RunListResponse runListResponse = (RunListResponse) o;
    return Objects.equals(this.runs, runListResponse.runs)
        && Objects.equals(this.nextPageToken, runListResponse.nextPageToken);
  }

  @Override
  public int hashCode() {
    return Objects.hash(runs, nextPageToken);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RunListResponse {\n");

    sb.append("    runs: ").append(toIndentedString(runs)).append("\n");
    sb.append("    nextPageToken: ").append(toIndentedString(nextPageToken)).append("\n");
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
