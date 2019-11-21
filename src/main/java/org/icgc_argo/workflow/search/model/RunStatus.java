package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ApiModel(description = "Small description of a workflow run, returned by server during listing")
public class RunStatus {

  @NonNull
  @JsonProperty("run_id")
  private String runId;

  @NonNull private State state;
}
