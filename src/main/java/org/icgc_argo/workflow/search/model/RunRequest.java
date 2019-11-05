package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/**
 * To execute a workflow, send a run request including all the details needed to begin downloading
 * and executing a given workflow.
 */
@ApiModel(
    description =
        "To execute a workflow, send a run request including all the details needed to begin downloading and executing a given workflow.")
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
public class RunRequest {
  @JsonProperty("workflow_params")
  private Object workflowParams = null;

  @JsonProperty("workflow_type")
  private String workflowType = null;

  @JsonProperty("workflow_type_version")
  private String workflowTypeVersion = null;

  @JsonProperty("tags")
  @Valid
  private Map<String, String> tags = null;

  @JsonProperty("workflow_engine_parameters")
  @Valid
  private Map<String, String> workflowEngineParameters = null;

  @JsonProperty("workflow_url")
  private String workflowUrl = null;

  public RunRequest workflowParams(Object workflowParams) {
    this.workflowParams = workflowParams;
    return this;
  }

  /**
   * REQUIRED The workflow run parameterizations (JSON encoded), including input and output file
   * locations
   *
   * @return workflowParams
   */
  @ApiModelProperty(
      value =
          "REQUIRED The workflow run parameterizations (JSON encoded), including input and output file locations")
  public Object getWorkflowParams() {
    return workflowParams;
  }

  public void setWorkflowParams(Object workflowParams) {
    this.workflowParams = workflowParams;
  }

  public RunRequest workflowType(String workflowType) {
    this.workflowType = workflowType;
    return this;
  }

  /**
   * REQUIRED The workflow descriptor type, must be \"CWL\" or \"WDL\" currently (or another
   * alternative supported by this WES instance)
   *
   * @return workflowType
   */
  @ApiModelProperty(
      value =
          "REQUIRED The workflow descriptor type, must be \"CWL\" or \"WDL\" currently (or another alternative supported by this WES instance)")
  public String getWorkflowType() {
    return workflowType;
  }

  public void setWorkflowType(String workflowType) {
    this.workflowType = workflowType;
  }

  public RunRequest workflowTypeVersion(String workflowTypeVersion) {
    this.workflowTypeVersion = workflowTypeVersion;
    return this;
  }

  /**
   * REQUIRED The workflow descriptor type version, must be one supported by this WES instance
   *
   * @return workflowTypeVersion
   */
  @ApiModelProperty(
      value =
          "REQUIRED The workflow descriptor type version, must be one supported by this WES instance")
  public String getWorkflowTypeVersion() {
    return workflowTypeVersion;
  }

  public void setWorkflowTypeVersion(String workflowTypeVersion) {
    this.workflowTypeVersion = workflowTypeVersion;
  }

  public RunRequest tags(Map<String, String> tags) {
    this.tags = tags;
    return this;
  }

  public RunRequest putTagsItem(String key, String tagsItem) {
    if (this.tags == null) {
      this.tags = new HashMap<String, String>();
    }
    this.tags.put(key, tagsItem);
    return this;
  }

  /**
   * OPTIONAL A key-value map of arbitrary metadata outside the scope of `workflow_params` but
   * useful to track with this run request
   *
   * @return tags
   */
  @ApiModelProperty(
      value =
          "OPTIONAL A key-value map of arbitrary metadata outside the scope of `workflow_params` but useful to track with this run request")
  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public RunRequest workflowEngineParameters(Map<String, String> workflowEngineParameters) {
    this.workflowEngineParameters = workflowEngineParameters;
    return this;
  }

  public RunRequest putWorkflowEngineParametersItem(
      String key, String workflowEngineParametersItem) {
    if (this.workflowEngineParameters == null) {
      this.workflowEngineParameters = new HashMap<>();
    }
    this.workflowEngineParameters.put(key, workflowEngineParametersItem);
    return this;
  }

  /**
   * OPTIONAL Additional parameters can be sent to the workflow engine using this field. Default
   * values for these parameters can be obtained using the ServiceInfo endpoint.
   *
   * @return workflowEngineParameters
   */
  @ApiModelProperty(
      value =
          "OPTIONAL Additional parameters can be sent to the workflow engine using this field. Default values for these parameters can be obtained using the ServiceInfo endpoint.")
  public Map<String, String> getWorkflowEngineParameters() {
    return workflowEngineParameters;
  }

  public void setWorkflowEngineParameters(Map<String, String> workflowEngineParameters) {
    this.workflowEngineParameters = workflowEngineParameters;
  }

  public RunRequest workflowUrl(String workflowUrl) {
    this.workflowUrl = workflowUrl;
    return this;
  }

  /**
   * REQUIRED The workflow CWL or WDL document. When `workflow_attachments` is used to attach files,
   * the `workflow_url` may be a relative path to one of the attachments.
   *
   * @return workflowUrl
   */
  @ApiModelProperty(
      value =
          "REQUIRED The workflow CWL or WDL document. When `workflow_attachments` is used to attach files, the `workflow_url` may be a relative path to one of the attachments.")
  public String getWorkflowUrl() {
    return workflowUrl;
  }

  public void setWorkflowUrl(String workflowUrl) {
    this.workflowUrl = workflowUrl;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RunRequest runRequest = (RunRequest) o;
    return Objects.equals(this.workflowParams, runRequest.workflowParams)
        && Objects.equals(this.workflowType, runRequest.workflowType)
        && Objects.equals(this.workflowTypeVersion, runRequest.workflowTypeVersion)
        && Objects.equals(this.tags, runRequest.tags)
        && Objects.equals(this.workflowEngineParameters, runRequest.workflowEngineParameters)
        && Objects.equals(this.workflowUrl, runRequest.workflowUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        workflowParams,
        workflowType,
        workflowTypeVersion,
        tags,
        workflowEngineParameters,
        workflowUrl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RunRequest {\n");

    sb.append("    workflowParams: ").append(toIndentedString(workflowParams)).append("\n");
    sb.append("    workflowType: ").append(toIndentedString(workflowType)).append("\n");
    sb.append("    workflowTypeVersion: ")
        .append(toIndentedString(workflowTypeVersion))
        .append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    workflowEngineParameters: ")
        .append(toIndentedString(workflowEngineParameters))
        .append("\n");
    sb.append("    workflowUrl: ").append(toIndentedString(workflowUrl)).append("\n");
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
