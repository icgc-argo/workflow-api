package org.icgc_argo.workflow.search.model.wes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import java.util.List;
import java.util.Map;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ServiceInfo {

  @NonNull private String authInstructionsUrl;

  @NonNull private String contactInfoUrl;

  @Valid @NonNull private List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters;

  @Valid @NonNull private List<String> supportedFilesystemProtocols;

  @Valid @NonNull private List<String> supportedWesVersions;

  @Valid @NonNull private Map<String, Long> systemStateCounts;

  @Valid @NonNull private Map<String, String> workflowEngineVersions;

  @Valid @NonNull private Map<String, String> workflowTypeVersions;
}
