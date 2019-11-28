package org.icgc_argo.workflow.search.model;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class SearchFields {

  public static final String RUN_ID = "runId";
  public static final String RUN_NAME = "runName";
  public static final String COMMAND_LINE = "commandLine";
  public static final String STATE = "state";
  public static final String START_TIME = "startTime";
  public static final String COMPLETE_TIME = "completeTime";
  public static final String EXIT_STATUS = "exitStatus";
  public static final String PARAMETERS = "parameters";
  public static final String REPOSITORY = "repository";
  public static final String ERROR_REPORT = "errorReport";
}
