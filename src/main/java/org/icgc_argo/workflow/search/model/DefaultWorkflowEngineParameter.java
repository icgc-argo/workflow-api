package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;

/** A message that allows one to describe default parameters for a workflow engine. */
@ApiModel(
    description = "A message that allows one to describe default parameters for a workflow engine.")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DefaultWorkflowEngineParameter {

  @JsonProperty("name")
  @NonNull
  private String name;

  @JsonProperty("type")
  private String type;

  @JsonProperty("default_value")
  private String defaultValue;
}
