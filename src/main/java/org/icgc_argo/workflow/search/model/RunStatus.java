package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ApiModel(description = "Small description of a workflow run, returned by server during listing")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunStatus {

  @NonNull private String runId;

  @NonNull private State state;
}
