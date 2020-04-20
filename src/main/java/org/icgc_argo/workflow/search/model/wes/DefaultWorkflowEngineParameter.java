package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DefaultWorkflowEngineParameter {

  @NonNull private String name;

  private String type;
}
