package org.icgc_argo.workflow.search.index.model;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkflowDocument {

  /** Workflow run ID */
  @NonNull private String runId;

  /** Workflow session ID */
  @NonNull private String sessionId;

  /** The repository url */
  @NonNull private String repository;

  /** The overall state of the workflow run, mapped to WorkflowEvent - event */
  @NonNull private String state;

  @NonNull private Map<String, Object> parameters;

  /** When the command started executing */
  @NonNull private Instant startTime;

  /**
   * When the command stopped executing (completed, failed, or cancelled), completeTime does not
   * exist in ES when workflow is unfinished.
   */
  private Instant completeTime;

  /** Did the workflow succeed */
  @NonNull private Boolean success;

  /** Exit code of the program */
  @NonNull private Integer exitStatus;

  /** A URL to retrieve standard error logs of the workflow run or task */
  private String errorReport;

  /** Workflow duration */
  private Integer duration;

  /** The command line that was executed */
  @NonNull private String commandLine;

  private Map<String, Object> engineParameters;
}
