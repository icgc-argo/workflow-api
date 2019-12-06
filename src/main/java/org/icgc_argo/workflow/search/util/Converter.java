package org.icgc_argo.workflow.search.util;

import static org.icgc_argo.workflow.search.model.SearchFields.RUN_NAME;
import static org.icgc_argo.workflow.search.model.SearchFields.STATE;
import static org.icgc_argo.workflow.search.model.State.fromValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

  public static TaskLog taskDocumentToLog(@NonNull TaskDocument task) {
    return TaskLog.builder()
        .taskId(task.getTaskId())
        .name(task.getName())
        .process(task.getProcess())
        .tag(task.getTag())
        .container(task.getContainer())
        .attempt(task.getAttempt())
        .state(State.fromValue(task.getState()))
        .cmd(buildCommandLineList(task.getScript()))
        .submitTime(task.getSubmitTime().toString())
        .startTime(getTimeOrEmpty(task.getStartTime()))
        .endTime(getTimeOrEmpty(task.getCompleteTime()))
        .exitCode(task.getExit())
        .workdir(task.getWorkdir())
        .cpus(task.getCpus())
        .memory(task.getMemory())
        .duration(task.getDuration())
        .realtime(task.getRealtime())
        // todo ticket #24 workflow-relay
        .stderr("")
        .stdout("")
        .build();
  }

  public static RunStatus convertSourceMapToRunStatus(@NonNull Map<String, Object> source) {
    return RunStatus.builder()
        .runId(source.get(RUN_NAME).toString())
        .state(fromValue(source.get(STATE).toString()))
        .build();
  }

  public static RunResponse buildRunLog(
      @NonNull WorkflowDocument workflowDoc,
      @NonNull String workflowTypeVersion,
      @NonNull String workflowType) {
    return RunResponse.builder()
        .runId(workflowDoc.getRunName())
        .state(State.fromValue(workflowDoc.getState()))
        // todo ticket #24
        .outputs("")
        // build RunRequest
        .request(
            RunRequest.builder()
                .workflowTypeVersion(workflowTypeVersion)
                .workflowType(workflowType)
                .workflowUrl(workflowDoc.getRepository())
                .workflowVersion(workflowDoc.getRevision())
                .workflowParams(workflowDoc.getParameters())
                .resume(workflowDoc.getResume())
                .build())
        // build run log
        .runLog(
            RunLog.builder()
                .name(workflowDoc.getRunName())
                .cmd(buildCommandLineList(workflowDoc.getCommandLine()))
                .exitCode(workflowDoc.getExitStatus())
                .startTime(workflowDoc.getStartTime().toString())
                .endTime(getTimeOrEmpty(workflowDoc.getCompleteTime()))
                .stdout("")
                .stderr(convertErrorReport(workflowDoc.getErrorReport()))
                .success(workflowDoc.getSuccess())
                .duration(workflowDoc.getDuration())
                .build())
        .build();
  }

  public static List<String> buildCommandLineList(@NonNull String commandLines) {
    return Arrays.asList(commandLines);
  }

  public static String getTimeOrEmpty(Date time) {
    return time == null ? "" : time.toString();
  }

  public static String convertErrorReport(String errorReport) {
    return errorReport == null ? "" : errorReport;
  }
}
