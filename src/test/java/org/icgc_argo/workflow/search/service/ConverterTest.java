package org.icgc_argo.workflow.search.service;

import static org.icgc_argo.workflow.search.util.Converter.convertSourceMapToRunStatus;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.index.model.TaskDocument;
import org.icgc_argo.workflow.search.index.model.WorkflowDocument;
import org.icgc_argo.workflow.search.model.SearchFields;
import org.icgc_argo.workflow.search.model.State;
import org.icgc_argo.workflow.search.util.Converter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
public class ConverterTest {

  private static final String RUN_ID = UUID.randomUUID().toString();
  private static final String RUN_NAME = UUID.randomUUID().toString();
  private static final String NAME = "Task_say_hello";
  private static final String SCRIPT = "print();";
  private static final String STATE_COMPLETE = "COMPLETE";
  private static final String REPOSITORY = "https://github.com/nextflow-io/hello.git";

  @Test
  public void TestConvertSourceMapToRunStatus() {
    val source = new HashMap<String, Object>();
    source.put(SearchFields.RUN_NAME, this.RUN_NAME);
    source.put(SearchFields.STATE, STATE_COMPLETE);
    val runStatus = convertSourceMapToRunStatus(source);

    assertEquals(runStatus.getRunId(), this.RUN_NAME);
    assertEquals(runStatus.getState(), State.COMPLETE);
  }

  @Test
  public void testTaskDocumentToLog() {
    val taskDocument =
        TaskDocument.builder()
            .runId(RUN_ID)
            .runName(RUN_NAME)
            .startTime(new Date())
            .completeTime(new Date())
            .exit(0)
            .name(NAME)
            .state(STATE_COMPLETE)
            .script(SCRIPT)
            .build();
    val log = Converter.taskDocumentToLog(taskDocument);

    assertEquals(log.getName(), taskDocument.getName());
    assertTrue(log.getCmd().contains(SCRIPT));
    assertEquals(log.getStartTime(), taskDocument.getStartTime().toString());
    assertEquals(log.getEndTime(), taskDocument.getCompleteTime().toString());
    assertEquals(log.getExitCode().intValue(), 0);
    assertEquals(log.getStderr(), "");
    assertEquals(log.getStdout(), "");
  }

  @Test
  public void testBuildRunLog() {
    val doc = buildWorkflowDoc();
    val workflowTypeVersion = "pcawg-bwa-mem: 0.0.1";
    val workflowType = "nextflow";
    val runLog = Converter.buildRunLog(doc, workflowTypeVersion, workflowType);

    assertEquals(runLog.getRunId(), RUN_NAME);
    assertEquals(runLog.getState().toString(), STATE_COMPLETE);

    val request = runLog.getRequest();
    assertNotNull(request);
    assertEquals(request.getWorkflowType(), workflowType);
    assertEquals(request.getWorkflowTypeVersion(), workflowTypeVersion);
    assertEquals(request.getWorkflowUrl(), REPOSITORY);
    assertTrue(request.getWorkflowParams() instanceof Map);
    val params = (Map) request.getWorkflowParams();
    assertEquals(params, doc.getParameters());

    val log = runLog.getRunLog();
    assertNotNull(log);
    assertEquals(log.getName(), RUN_NAME);
    assertTrue(log.getCmd().contains(SCRIPT));
    assertEquals(log.getStartTime(), doc.getStartTime().toString());
    assertEquals(log.getEndTime(), doc.getCompleteTime().toString());
    assertEquals(log.getExitCode().intValue(), 0);
    assertEquals(log.getStdout(), "");
    assertEquals(log.getStderr(), doc.getErrorReport());
  }

  private WorkflowDocument buildWorkflowDoc() {
    val params = new HashMap<String, Object>();
    params.put("class", "File");
    params.put("path", "ftp://ftp-private.ebi.ac.uk/upload/foivos/test.txt");

    return WorkflowDocument.builder()
        .runId(RUN_ID)
        .runName(RUN_NAME)
        .commandLine(SCRIPT)
        .startTime(new Date())
        .completeTime(new Date())
        .state(STATE_COMPLETE)
        .repository(REPOSITORY)
        .revision("master")
        .errorReport("No error found.")
        .exitStatus(0)
        .parameters(params)
        .resume(false)
        .success(true)
        .duration(1000)
        .build();
  }
}
