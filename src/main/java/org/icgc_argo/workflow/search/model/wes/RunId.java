package org.icgc_argo.workflow.search.model.wes;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public class RunId {
  @NonNull private String runId;
}
