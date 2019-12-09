package org.icgc_argo.workflow.search.index.model;

import java.time.Instant;
import java.util.Map;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkflowDocument {

  /** Workflow run ID */
  @NonNull private String runId;

  /** Workflow run name */
  @NonNull private String runName;

  /** The overall state of the workflow run, mapped to WorkflowEvent - event */
  @NonNull private String state;

  @NonNull private Map<String, Object> parameters;

  /** When the command started executing */
  @NonNull private Instant startTime;

  /**
   * When the command stopped executing (completed, failed, or cancelled), completeTime does not
   * exist in ES when work flow is unfinished.
   */
  private Instant completeTime;

  /** The repository url */
  @NonNull private String repository;

  /** The repository release/tag/branch */
  private String revision;

  /** Exit code of the program */
  @NonNull private Integer exitStatus;

  /** The command line that was executed */
  @NonNull private String commandLine;

  /** A URL to retrieve standard error logs of the workflow run or task */
  private String errorReport;

  /** Was this a resume run */
  @NonNull private Boolean resume;

  /** Did the workflow succeed */
  @NonNull private Boolean success;

  /** Workflow duration */
  private Integer duration;
}
