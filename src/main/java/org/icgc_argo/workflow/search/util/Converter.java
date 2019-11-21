package org.icgc_argo.workflow.search.util;

import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;
import static org.icgc_argo.workflow.search.model.SearchFields.STATE;
import static org.icgc_argo.workflow.search.model.State.fromValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.index.model.TaskDocument;
import org.icgc_argo.workflow.search.index.model.WorkflowDocument;
import org.icgc_argo.workflow.search.model.*;

@Slf4j
@UtilityClass
public class Converter {

  @SneakyThrows
  public static TaskDocument convertSearchHitToTaskDocument(@NonNull SearchHit hit) {
    val mapper = new ObjectMapper();
    return mapper.readValue(hit.getSourceAsString(), TaskDocument.class);
  }

  public static Log taskDocumentToLog(@NonNull TaskDocument task) {
    return Log.builder()
        .name(task.getName())
        .cmd(buildCommandLineList(task.getScript()))
        .startTime(task.getStartTime().toString())
        .endTime(convertEndTime(task.getCompleteTime()))
        .exitCode(task.getExit())
        // todo ticket #24 workflow-relay
        .stderr("")
        .stdout("")
        .build();
  }

  public static RunStatus convertSourceMapToRunStatus(@NonNull Map<String, Object> source) {
    return RunStatus.builder()
        .runId(source.get(RUN_ID).toString())
        .state(fromValue(source.get(STATE).toString()))
        .build();
  }

  public static RunLog buildRunLog(
      @NonNull WorkflowDocument workflowDoc,
      @NonNull String workflowTypeVersion,
      @NonNull String workflowType) {
    val runLog =
        RunLog.builder()
            .runId(workflowDoc.getRunId())
            .state(State.fromValue(workflowDoc.getState()))
            // todo ticket #24
            .outputs("")
            // build RunRequest
            .request(
                RunRequest.builder()
                    .workflowTypeVersion(workflowTypeVersion)
                    .workflowType(workflowType)
                    .workflowUrl(workflowDoc.getRepository())
                    .workflowParams(workflowDoc.getParameters())
                    .build())
            // build run log
            .runLog(
                Log.builder()
                    .name(workflowDoc.getRunName())
                    .cmd(buildCommandLineList(workflowDoc.getCommandLine()))
                    .exitCode(workflowDoc.getExitStatus())
                    .startTime(workflowDoc.getStartTime().toString())
                    .endTime(convertEndTime(workflowDoc.getCompleteTime()))
                    .stdout("")
                    .stderr(convertErrorReport(workflowDoc.getErrorReport()))
                    .build())
            .build();
    return runLog;
  }

  public static List<String> buildCommandLineList(@NonNull String commandLines) {
    return Arrays.asList(commandLines);
  }

  public static String convertEndTime(Date endTime) {
    return endTime == null ? "" : endTime.toString();
  }

  public static String convertErrorReport(String errorReport) {
    return errorReport == null ? "" : errorReport;
  }
}
