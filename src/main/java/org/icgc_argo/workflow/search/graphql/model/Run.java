package org.icgc_argo.workflow.search.graphql.model;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Run {
  @NonNull private String runId;
  @NonNull private String state;
  private String analysisId;
  private String workDir;
}
