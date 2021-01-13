/*
 * Copyright (c) 2020 The Ontario Institute for Cancer Research. All rights reserved
 *
 * This program and the accompanying materials are made available under the terms of the GNU Affero General Public License v3.0.
 * You should have received a copy of the GNU Affero General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.icgc_argo.workflow.search.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.index.model.TaskDocument;
import org.icgc_argo.workflow.search.index.model.WorkflowDocument;
import org.icgc_argo.workflow.search.model.SearchFields;
import org.icgc_argo.workflow.search.model.wes.State;
import org.icgc_argo.workflow.search.util.Converter;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static graphql.Assert.assertNotNull;
import static org.icgc_argo.workflow.search.util.Converter.convertSourceMapToRunStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ConverterTest {

  private static final String RUN_ID = UUID.randomUUID().toString();
  private static final String SESSION_ID = UUID.randomUUID().toString();
  private static final String NAME = "Task_say_hello";
  private static final String SCRIPT = "print();";
  private static final String STATE_COMPLETE = "COMPLETE";
  private static final String REPOSITORY = "https://github.com/nextflow-io/hello.git";
  private static final Integer TASK_ID = 1;
  private static final String TASK_PROCESS = "Amazing Process";
  private static final String TASK_TAG = UUID.randomUUID().toString();
  private static final String TASK_CONTAINER = "amazing-container:latest";
  private static final Integer TASK_ATTEMPT = 1;
  private static final String TASK_WORKDIR = "/this/is/dir";
  private static final Integer TASK_CPUS = 4;
  private static final Long TASK_MEM = 1024L;
  private static final Long TASK_DURATION = 2000L;
  private static final Long TASK_REALTIME = 1098L;
  private static final Long TASK_RSS = 1234L;
  private static final Long TASK_PEAK_RSS = 4567L;
  private static final Long TASK_VMEM = 8901L;
  private static final Long TASK_PEAK_VMEM = 2345L;
  private static final Long TASK_READ_BYTES = 6789L;
  private static final Long TASK_WRITE_BYTES = 10123L;



  @Test
  public void TestConvertSourceMapToRunStatus() {
    val source = new HashMap<String, Object>();
    source.put(SearchFields.RUN_ID, RUN_ID);
    source.put(SearchFields.STATE, STATE_COMPLETE);
    val runStatus = convertSourceMapToRunStatus(source);

    assertEquals(runStatus.getRunId(), RUN_ID);
    assertEquals(runStatus.getState(), State.COMPLETE);
  }

  @Test
  public void testTaskDocumentToLog() {
    val taskDocument =
        TaskDocument.builder()
            .runId(RUN_ID)
            .sessionId(SESSION_ID)
            .taskId(TASK_ID)
            .name(NAME)
            .process(TASK_PROCESS)
            .tag(TASK_TAG)
            .container(TASK_CONTAINER)
            .attempt(TASK_ATTEMPT)
            .state(STATE_COMPLETE)
            .submitTime(Instant.now())
            .exit(0)
            .script(SCRIPT)
            .workdir(TASK_WORKDIR)
            .cpus(TASK_CPUS)
            .memory(TASK_MEM)
            .duration(TASK_DURATION)
            .realtime(TASK_REALTIME)
            .rss(TASK_RSS)
            .peakRss(TASK_PEAK_RSS)
            .vmem(TASK_VMEM)
            .peakVmem(TASK_PEAK_VMEM)
            .readBytes(TASK_READ_BYTES)
            .writeBytes(TASK_WRITE_BYTES)
            .build();

    val log = Converter.taskDocumentToLog(taskDocument);

    assertEquals(log.getTaskId(), taskDocument.getTaskId());
    assertEquals(log.getName(), taskDocument.getName());
    assertEquals(log.getProcess(), taskDocument.getProcess());
    assertEquals(log.getTag(), taskDocument.getTag());
    assertEquals(log.getContainer(), taskDocument.getContainer());
    assertEquals(log.getAttempt(), taskDocument.getAttempt());
    assertEquals(log.getState().toString(), STATE_COMPLETE);
    assertEquals(log.getSubmitTime(), taskDocument.getSubmitTime().toString());
    assertEquals(log.getStartTime(), "");
    assertEquals(log.getEndTime(), "");
    assertTrue(log.getCmd().contains(SCRIPT));
    assertEquals(log.getExitCode().intValue(), 0);
    assertEquals(log.getStderr(), "");
    assertEquals(log.getStdout(), "");
    assertEquals(log.getWorkdir(), taskDocument.getWorkdir());
    assertEquals(log.getCpus(), taskDocument.getCpus());
    assertEquals(log.getMemory(), taskDocument.getMemory());
    assertEquals(log.getDuration(), taskDocument.getDuration());
    assertEquals(log.getRealtime(), taskDocument.getRealtime());
    assertEquals(log.getRss(), taskDocument.getRss());
    assertEquals(log.getPeakRss(), taskDocument.getPeakRss());
    assertEquals(log.getVmem(), taskDocument.getVmem());
    assertEquals(log.getPeakVmem(), taskDocument.getPeakVmem());
    assertEquals(log.getReadBytes(), taskDocument.getReadBytes());
    assertEquals(log.getWriteBytes(), taskDocument.getWriteBytes());
  }

  @Test
  public void testBuildRunLog() {
    val doc = buildWorkflowDoc();
    val workflowTypeVersion = "nextflow-dna-seq-alignment: 0.0.1";
    val workflowType = "nextflow";
    val runLog = Converter.buildRunLog(doc, workflowTypeVersion, workflowType);

    assertEquals(runLog.getRunId(), RUN_ID);
    assertEquals(runLog.getState().toString(), STATE_COMPLETE);

    val request = runLog.getRequest();
    assertNotNull(request);
    assertEquals(request.getWorkflowType(), workflowType);
    assertEquals(request.getWorkflowTypeVersion(), workflowTypeVersion);
    assertEquals(request.getWorkflowUrl(), REPOSITORY);
    assertTrue(request.getWorkflowParams() instanceof Map);
    val params = (Map) request.getWorkflowParams();
    assertEquals(params, doc.getParameters());
    assertEquals(params, doc.getParameters());

    val log = runLog.getRunLog();
    assertNotNull(log);
    assertEquals(log.getRunId(), RUN_ID);
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

    val engineParams = new HashMap<String, Object>();
    engineParams.put("revision", "test-branch-name");

    return WorkflowDocument.builder()
        .runId(RUN_ID)
        .sessionId(SESSION_ID)
        .commandLine(SCRIPT)
        .startTime(Instant.now())
        .completeTime(Instant.now())
        .state(STATE_COMPLETE)
        .repository(REPOSITORY)
        .errorReport("No error found.")
        .exitStatus(0)
        .parameters(params)
        .engineParameters(engineParams)
        .success(true)
        .duration(1000L)
        .build();
  }
}
