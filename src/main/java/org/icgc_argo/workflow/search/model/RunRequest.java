package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * To execute a workflow, send a run request including all the details needed to begin downloading
 * and executing a given workflow.
 */
@ApiModel(
    description =
        "To execute a workflow, send a run request including all the details needed to begin downloading and executing a given workflow.")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RunRequest {

  @JsonProperty("workflow_params")
  private Object workflowParams;

  @JsonProperty("workflow_type")
  private String workflowType;

  @JsonProperty("workflow_type_version")
  private String workflowTypeVersion;

  @JsonProperty("workflow_url")
  private String workflowUrl;
}
