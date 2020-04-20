package org.icgc_argo.workflow.search.model.wes;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.*;

/** Available workflow types supported by a given instance of the service. */
@ApiModel(description = "Available workflow types supported by a given instance of the service.")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkflowTypeVersion {

  private List<String> workflowTypeVersion;
}
