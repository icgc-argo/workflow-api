package org.icgc_argo.workflow.search.index.model;

import java.util.Date;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskDocument {

  /** Workflow run ID */
  @NonNull private String runId;

  /** Workflow run name */
  @NonNull private String runName;

  /** The overall state of the task run, mapped to Trace's "status" */
  @NonNull private String state;

  /** When the command started executing */
  @NonNull private Date startTime;

  /** When the command stopped executing (completed, failed, or cancelled) */
  private Date completeTime;

  /** Exit code of the program */
  @NonNull private Integer exit;

  /** task name */
  @NonNull private String name;

  /** The command line that was executed */
  @NonNull private String script;
}
