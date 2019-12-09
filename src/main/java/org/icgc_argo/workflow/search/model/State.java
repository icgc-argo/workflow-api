package org.icgc_argo.workflow.search.model;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * - UNKNOWN: The state of the task is unknown. This provides a safe default for messages where this
 * field is missing, for example, so that a missing field does not accidentally imply that the state
 * is QUEUED. - QUEUED: The task is queued. - INITIALIZING: The task has been assigned to a worker
 * and is currently preparing to run. For example, the worker may be turning on, downloading input
 * files, etc. - RUNNING: The task is running. Input files are downloaded and the first Executor has
 * been started. - PAUSED: The task is paused. An implementation may have the ability to pause a
 * task, but this is not required. - COMPLETE: The task has completed running. Executors have exited
 * without error and output files have been successfully uploaded. - EXECUTOR_ERROR: The task
 * encountered an error in one of the Executor processes. Generally, this means that an Executor
 * exited with a non-zero exit code. - SYSTEM_ERROR: The task was stopped due to a system error, but
 * not from an Executor, for example an upload failed due to network issues, the worker's ran out of
 * disk space, etc. - CANCELED: The task was canceled by the user. - CANCELING: The task was
 * canceled by the user, and is in the process of stopping.
 */
@RequiredArgsConstructor
public enum State {
  UNKNOWN("UNKNOWN"),

  QUEUED("QUEUED"),

  INITIALIZING("INITIALIZING"),

  RUNNING("RUNNING"),

  PAUSED("PAUSED"),

  COMPLETE("COMPLETE"),

  EXECUTOR_ERROR("EXECUTOR_ERROR"),

  SYSTEM_ERROR("SYSTEM_ERROR"),

  CANCELED("CANCELED"),

  CANCELING("CANCELING");

  @NonNull private final String value;

  public static State fromValue(@NonNull String text) {
    if (text.equalsIgnoreCase("RUNNING")) {
      return State.RUNNING;
    } else if (text.equalsIgnoreCase("QUEUED")) {
      return State.QUEUED;
    } else if (text.equalsIgnoreCase("COMPLETE")) {
      return State.COMPLETE;
    } else if (text.equalsIgnoreCase("EXECUTOR_ERROR")) {
      return State.EXECUTOR_ERROR;
    } else return State.UNKNOWN;
  }

  @Override
  public String toString() {
    return value;
  }
}
