package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.*;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/**
 * A message containing useful information about the running service, including supported versions
 * and default settings.
 */
@ApiModel(
    description =
        "A message containing useful information about the running service, including supported versions and default settings.")
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
public class ServiceInfo {
  @JsonProperty("workflow_type_versions")
  @Valid
  private Map<String, WorkflowTypeVersion> workflowTypeVersions = null;

  @JsonProperty("supported_wes_versions")
  @Valid
  private List<String> supportedWesVersions = null;

  @JsonProperty("supported_filesystem_protocols")
  @Valid
  private List<String> supportedFilesystemProtocols = null;

  @JsonProperty("workflow_engine_versions")
  @Valid
  private Map<String, String> workflowEngineVersions = null;

  @JsonProperty("default_workflow_engine_parameters")
  @Valid
  private List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters = null;

  @JsonProperty("system_state_counts")
  @Valid
  private Map<String, Long> systemStateCounts = null;

  @JsonProperty("auth_instructions_url")
  private String authInstructionsUrl = null;

  @JsonProperty("contact_info_url")
  private String contactInfoUrl = null;

  @JsonProperty("tags")
  @Valid
  private Map<String, String> tags = null;

  public ServiceInfo workflowTypeVersions(Map<String, WorkflowTypeVersion> workflowTypeVersions) {
    this.workflowTypeVersions = workflowTypeVersions;
    return this;
  }

  public ServiceInfo putWorkflowTypeVersionsItem(
      String key, WorkflowTypeVersion workflowTypeVersionsItem) {
    if (this.workflowTypeVersions == null) {
      this.workflowTypeVersions = new HashMap<String, WorkflowTypeVersion>();
    }
    this.workflowTypeVersions.put(key, workflowTypeVersionsItem);
    return this;
  }

  /**
   * A map with keys as the workflow format type name (currently only CWL and WDL are used although
   * a service may support others) and value is a workflow_type_version object which simply contains
   * an array of one or more version strings
   *
   * @return workflowTypeVersions
   */
  @ApiModelProperty(
      value =
          "A map with keys as the workflow format type name (currently only CWL and WDL are used although a service may support others) and value is a workflow_type_version object which simply contains an array of one or more version strings")
  @Valid
  public Map<String, WorkflowTypeVersion> getWorkflowTypeVersions() {
    return workflowTypeVersions;
  }

  public void setWorkflowTypeVersions(Map<String, WorkflowTypeVersion> workflowTypeVersions) {
    this.workflowTypeVersions = workflowTypeVersions;
  }

  public ServiceInfo supportedWesVersions(List<String> supportedWesVersions) {
    this.supportedWesVersions = supportedWesVersions;
    return this;
  }

  public ServiceInfo addSupportedWesVersionsItem(String supportedWesVersionsItem) {
    if (this.supportedWesVersions == null) {
      this.supportedWesVersions = new ArrayList<String>();
    }
    this.supportedWesVersions.add(supportedWesVersionsItem);
    return this;
  }

  /**
   * The version(s) of the WES schema supported by this service
   *
   * @return supportedWesVersions
   */
  @ApiModelProperty(value = "The version(s) of the WES schema supported by this service")
  public List<String> getSupportedWesVersions() {
    return supportedWesVersions;
  }

  public void setSupportedWesVersions(List<String> supportedWesVersions) {
    this.supportedWesVersions = supportedWesVersions;
  }

  public ServiceInfo supportedFilesystemProtocols(List<String> supportedFilesystemProtocols) {
    this.supportedFilesystemProtocols = supportedFilesystemProtocols;
    return this;
  }

  public ServiceInfo addSupportedFilesystemProtocolsItem(String supportedFilesystemProtocolsItem) {
    if (this.supportedFilesystemProtocols == null) {
      this.supportedFilesystemProtocols = new ArrayList<>();
    }
    this.supportedFilesystemProtocols.add(supportedFilesystemProtocolsItem);
    return this;
  }

  /**
   * The filesystem protocols supported by this service, currently these may include common
   * protocols using the terms 'http', 'https', 'sftp', 's3', 'gs', 'file', or 'synapse', but others
   * are possible and the terms beyond these core protocols are currently not fixed. This section
   * reports those protocols (either common or not) supported by this WES service.
   *
   * @return supportedFilesystemProtocols
   */
  @ApiModelProperty(
      value =
          "The filesystem protocols supported by this service, currently these may include common protocols using the terms 'http', 'https', 'sftp', 's3', 'gs', 'file', or 'synapse', but others  are possible and the terms beyond these core protocols are currently not fixed.   This section reports those protocols (either common or not) supported by this WES service.")
  public List<String> getSupportedFilesystemProtocols() {
    return supportedFilesystemProtocols;
  }

  public void setSupportedFilesystemProtocols(List<String> supportedFilesystemProtocols) {
    this.supportedFilesystemProtocols = supportedFilesystemProtocols;
  }

  public ServiceInfo workflowEngineVersions(Map<String, String> workflowEngineVersions) {
    this.workflowEngineVersions = workflowEngineVersions;
    return this;
  }

  public ServiceInfo putWorkflowEngineVersionsItem(String key, String workflowEngineVersionsItem) {
    if (this.workflowEngineVersions == null) {
      this.workflowEngineVersions = new HashMap<String, String>();
    }
    this.workflowEngineVersions.put(key, workflowEngineVersionsItem);
    return this;
  }

  /**
   * The engine(s) used by this WES service, key is engine name (e.g. Cromwell) and value is version
   *
   * @return workflowEngineVersions
   */
  @ApiModelProperty(
      value =
          "The engine(s) used by this WES service, key is engine name (e.g. Cromwell) and value is version")
  public Map<String, String> getWorkflowEngineVersions() {
    return workflowEngineVersions;
  }

  public void setWorkflowEngineVersions(Map<String, String> workflowEngineVersions) {
    this.workflowEngineVersions = workflowEngineVersions;
  }

  public ServiceInfo defaultWorkflowEngineParameters(
      List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters) {
    this.defaultWorkflowEngineParameters = defaultWorkflowEngineParameters;
    return this;
  }

  public ServiceInfo addDefaultWorkflowEngineParametersItem(
      DefaultWorkflowEngineParameter defaultWorkflowEngineParametersItem) {
    if (this.defaultWorkflowEngineParameters == null) {
      this.defaultWorkflowEngineParameters = new ArrayList<DefaultWorkflowEngineParameter>();
    }
    this.defaultWorkflowEngineParameters.add(defaultWorkflowEngineParametersItem);
    return this;
  }

  /**
   * Each workflow engine can present additional parameters that can be sent to the workflow engine.
   * This message will list the default values, and their types for each workflow engine.
   *
   * @return defaultWorkflowEngineParameters
   */
  @ApiModelProperty(
      value =
          "Each workflow engine can present additional parameters that can be sent to the workflow engine. This message will list the default values, and their types for each workflow engine.")
  @Valid
  public List<DefaultWorkflowEngineParameter> getDefaultWorkflowEngineParameters() {
    return defaultWorkflowEngineParameters;
  }

  public void setDefaultWorkflowEngineParameters(
      List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters) {
    this.defaultWorkflowEngineParameters = defaultWorkflowEngineParameters;
  }

  public ServiceInfo systemStateCounts(Map<String, Long> systemStateCounts) {
    this.systemStateCounts = systemStateCounts;
    return this;
  }

  public ServiceInfo putSystemStateCountsItem(String key, Long systemStateCountsItem) {
    if (this.systemStateCounts == null) {
      this.systemStateCounts = new HashMap<String, Long>();
    }
    this.systemStateCounts.put(key, systemStateCountsItem);
    return this;
  }

  /**
   * The system statistics, key is the statistic, value is the count of runs in that state. See the
   * State enum for the possible keys.
   *
   * @return systemStateCounts
   */
  @ApiModelProperty(
      value =
          "The system statistics, key is the statistic, value is the count of runs in that state. See the State enum for the possible keys.")
  public Map<String, Long> getSystemStateCounts() {
    return systemStateCounts;
  }

  public void setSystemStateCounts(Map<String, Long> systemStateCounts) {
    this.systemStateCounts = systemStateCounts;
  }

  public ServiceInfo authInstructionsUrl(String authInstructionsUrl) {
    this.authInstructionsUrl = authInstructionsUrl;
    return this;
  }

  /**
   * A web page URL with human-readable instructions on how to get an authorization token for use
   * with a specific WES endpoint.
   *
   * @return authInstructionsUrl
   */
  @ApiModelProperty(
      value =
          "A web page URL with human-readable instructions on how to get an authorization token for use with a specific WES endpoint.          ")
  public String getAuthInstructionsUrl() {
    return authInstructionsUrl;
  }

  public void setAuthInstructionsUrl(String authInstructionsUrl) {
    this.authInstructionsUrl = authInstructionsUrl;
  }

  public ServiceInfo contactInfoUrl(String contactInfoUrl) {
    this.contactInfoUrl = contactInfoUrl;
    return this;
  }

  /**
   * An email address URL (mailto:) or web page URL with contact information for the operator of a
   * specific WES endpoint. Users of the endpoint should use this to report problems or security
   * vulnerabilities.
   *
   * @return contactInfoUrl
   */
  @ApiModelProperty(
      value =
          "An email address URL (mailto:) or web page URL with contact information for the operator of a specific WES endpoint.  Users of the endpoint should use this to report problems or security vulnerabilities.")
  public String getContactInfoUrl() {
    return contactInfoUrl;
  }

  public void setContactInfoUrl(String contactInfoUrl) {
    this.contactInfoUrl = contactInfoUrl;
  }

  public ServiceInfo tags(Map<String, String> tags) {
    this.tags = tags;
    return this;
  }

  public ServiceInfo putTagsItem(String key, String tagsItem) {
    if (this.tags == null) {
      this.tags = new HashMap<>();
    }
    this.tags.put(key, tagsItem);
    return this;
  }

  /**
   * A key-value map of arbitrary, extended metadata outside the scope of the above but useful to
   * report back
   *
   * @return tags
   */
  @ApiModelProperty(
      value =
          "A key-value map of arbitrary, extended metadata outside the scope of the above but useful to report back")
  public Map<String, String> getTags() {
    return tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceInfo serviceInfo = (ServiceInfo) o;
    return Objects.equals(this.workflowTypeVersions, serviceInfo.workflowTypeVersions)
        && Objects.equals(this.supportedWesVersions, serviceInfo.supportedWesVersions)
        && Objects.equals(
            this.supportedFilesystemProtocols, serviceInfo.supportedFilesystemProtocols)
        && Objects.equals(this.workflowEngineVersions, serviceInfo.workflowEngineVersions)
        && Objects.equals(
            this.defaultWorkflowEngineParameters, serviceInfo.defaultWorkflowEngineParameters)
        && Objects.equals(this.systemStateCounts, serviceInfo.systemStateCounts)
        && Objects.equals(this.authInstructionsUrl, serviceInfo.authInstructionsUrl)
        && Objects.equals(this.contactInfoUrl, serviceInfo.contactInfoUrl)
        && Objects.equals(this.tags, serviceInfo.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        workflowTypeVersions,
        supportedWesVersions,
        supportedFilesystemProtocols,
        workflowEngineVersions,
        defaultWorkflowEngineParameters,
        systemStateCounts,
        authInstructionsUrl,
        contactInfoUrl,
        tags);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceInfo {\n");

    sb.append("    workflowTypeVersions: ")
        .append(toIndentedString(workflowTypeVersions))
        .append("\n");
    sb.append("    supportedWesVersions: ")
        .append(toIndentedString(supportedWesVersions))
        .append("\n");
    sb.append("    supportedFilesystemProtocols: ")
        .append(toIndentedString(supportedFilesystemProtocols))
        .append("\n");
    sb.append("    workflowEngineVersions: ")
        .append(toIndentedString(workflowEngineVersions))
        .append("\n");
    sb.append("    defaultWorkflowEngineParameters: ")
        .append(toIndentedString(defaultWorkflowEngineParameters))
        .append("\n");
    sb.append("    systemStateCounts: ").append(toIndentedString(systemStateCounts)).append("\n");
    sb.append("    authInstructionsUrl: ")
        .append(toIndentedString(authInstructionsUrl))
        .append("\n");
    sb.append("    contactInfoUrl: ").append(toIndentedString(contactInfoUrl)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
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
