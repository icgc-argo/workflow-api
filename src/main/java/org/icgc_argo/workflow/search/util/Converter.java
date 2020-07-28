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

package org.icgc_argo.workflow.search.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.index.model.TaskDocument;
import org.icgc_argo.workflow.search.index.model.WorkflowDocument;
import org.icgc_argo.workflow.search.model.wes.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;
import static org.icgc_argo.workflow.search.model.SearchFields.STATE;
import static org.icgc_argo.workflow.search.model.wes.State.fromValue;

@Slf4j
@UtilityClass
public class Converter {

  @SneakyThrows
  public static TaskDocument convertSearchHitToTaskDocument(@NonNull SearchHit hit) {
    val mapper =
        new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
        .rss(task.getRss())
        .peakRss(task.getPeakRss())
        .vmem(task.getVmem())
        .peakVmem(task.getPeakVmem())
        .readBytes(task.getReadBytes())
        .writeBytes(task.getWriteBytes())
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

  public static RunResponse buildRunLog(
      @NonNull WorkflowDocument workflowDoc,
      @NonNull String workflowTypeVersion,
      @NonNull String workflowType) {
    return RunResponse.builder()
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
                .workflowEngineParams(workflowDoc.getEngineParameters())
                .build())
        // build run log
        .runLog(
            RunLog.builder()
                .runId(workflowDoc.getRunId())
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

  /**
   * Check if time exists and that it is not zero (epoch time)
   *
   * @param datetime
   * @return datetime as string
   */
  public static String getTimeOrEmpty(Instant datetime) {
    return datetime == null ? "" : datetime.equals(Instant.EPOCH) ? "" : datetime.toString();
  }

  public static String convertErrorReport(String errorReport) {
    return errorReport == null ? "" : errorReport;
  }
}
