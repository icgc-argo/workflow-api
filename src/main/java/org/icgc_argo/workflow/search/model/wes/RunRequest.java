package org.icgc_argo.workflow.search.model.wes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunRequest {

  private Object workflowParams;

  private Object workflowEngineParams;

  private String workflowType;

  private String workflowTypeVersion;

  private String workflowUrl;
}
