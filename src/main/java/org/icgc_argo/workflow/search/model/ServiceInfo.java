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

  @JsonProperty("auth_instructions_url")
  @NonNull
  private String authInstructionsUrl;

  @JsonProperty("contact_info_url")
  @NonNull
  private String contactInfoUrl;

  @JsonProperty("default_workflow_engine_parameters")
  @Valid
  @NonNull
  private List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters;

  @JsonProperty("supported_filesystem_protocols")
  @Valid
  @NonNull
  private List<String> supportedFilesystemProtocols;

  @JsonProperty("supported_wes_versions")
  @Valid
  @NonNull
  private List<String> supportedWesVersions;

  @JsonProperty("system_state_counts")
  @Valid
  @NonNull
  private Map<String, Long> systemStateCounts;

  @JsonProperty("workflow_engine_versions")
  @Valid
  @NonNull
  private Map<String, String> workflowEngineVersions;

  @JsonProperty("workflow_type_versions")
  @Valid
  @NonNull
  private Map<String, String> workflowTypeVersions;

//  @JsonProperty("tags")
//  @Valid
//  private Map<String, String> tags;
}
