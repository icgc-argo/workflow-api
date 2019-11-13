package org.icgc_argo.workflow.search.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

/** Small description of a workflow run, returned by server during listing */
@ApiModel(description = "Small description of a workflow run, returned by server during listing")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RunStatus {

  @NonNull private String runId;

  @NonNull private State state;
}
