package org.icgc_argo.workflow.search.model;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SearchFields {

  public static final String RUN_ID = "runId";
  public static final String SESSION_ID = "sessionId";
  public static final String COMMAND_LINE = "commandLine";
  public static final String STATE = "state";
  public static final String START_TIME = "startTime";
  public static final String COMPLETE_TIME = "completeTime";
  public static final String EXIT_STATUS = "exitStatus";
  public static final String PARAMETERS = "parameters";
  public static final String REPOSITORY = "repository";
  public static final String ERROR_REPORT = "errorReport";
  public static final String TAG = "tag";
  public static final String WORK_DIR = "workDir";
  public static final String ANALYSIS_ID = "analysisId";
}
