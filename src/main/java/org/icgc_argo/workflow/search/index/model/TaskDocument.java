package org.icgc_argo.workflow.search.index.model;

import lombok.*;

import java.time.Instant;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TaskDocument {

  /** Workflow run ID */
  @NonNull private String runId;

  /** Workflow session ID */
  @NonNull private String sessionId;

  /** Task ID */
  @NonNull private Integer taskId;

  /** Task name */
  @NonNull private String name;

  /** Process name */
  @NonNull private String process;

  /** The state of the task run */
  @NonNull private String state;

  /** Task tag */
  private String tag;

  /** Task container */
  @NonNull private String container;

  /** Attempt */
  @NonNull private Integer attempt;

  /** When the command was submitted */
  @NonNull private Instant submitTime;

  /** When the command started executing */
  private Instant startTime;

  /** When the command stopped executing (completed, failed, or cancelled) */
  private Instant completeTime;

  /** Exit code of the program */
  @NonNull private Integer exit;

  /** The command line that was executed */
  @NonNull private String script;

  /** Task filesystem working directory */
  @NonNull private String workdir;

  /** Task cpu usage */
  private Integer cpus;

  /** Task memory usage */
  private Integer memory;

  /** Task duration (ms) */
  private Integer duration;

  /** Task real execution time (ms) */
  private Integer realtime;
}
