package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.*;
import javax.validation.Valid;
import lombok.*;

/**
 * A message containing useful information about the running service, including supported versions
 * and default settings.
 */
@ApiModel(
    description =
        "A message containing useful information about the running service, including supported versions and default settings.")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ServiceInfo {

  @JsonProperty("workflow_type_versions")
  @Valid
  private Map<String, WorkflowTypeVersion> workflowTypeVersions;

  @JsonProperty("supported_wes_versions")
  @Valid
  private List<String> supportedWesVersions;

  @JsonProperty("supported_filesystem_protocols")
  @Valid
  private List<String> supportedFilesystemProtocols;

  @JsonProperty("workflow_engine_versions")
  @Valid
  private Map<String, String> workflowEngineVersions;

  @JsonProperty("default_workflow_engine_parameters")
  @Valid
  private List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters;

  @JsonProperty("system_state_counts")
  @Valid
  private Map<String, Long> systemStateCounts;

  @JsonProperty("auth_instructions_url")
  private String authInstructionsUrl;

  @JsonProperty("contact_info_url")
  private String contactInfoUrl;

  @JsonProperty("tags")
  @Valid
  private Map<String, String> tags;
}
