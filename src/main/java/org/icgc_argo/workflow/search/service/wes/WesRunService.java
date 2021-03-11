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

package org.icgc_argo.workflow.search.service.wes;

import static java.lang.String.format;
import static org.icgc_argo.workflow.search.model.EsDefaults.ES_PAGE_DEFAULT_SIZE;
import static org.icgc_argo.workflow.search.model.wes.State.fromValue;
import static org.icgc_argo.workflow.search.util.Converter.buildRunLog;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.config.ServiceInfoProperties;
import org.icgc_argo.workflow.search.model.common.RunRequest;
import org.icgc_argo.workflow.search.model.common.Task;
import org.icgc_argo.workflow.search.model.exceptions.NotFoundException;
import org.icgc_argo.workflow.search.model.wes.*;
import org.icgc_argo.workflow.search.service.graphql.RunService;
import org.icgc_argo.workflow.search.service.graphql.TaskService;
import org.icgc_argo.workflow.search.util.Converter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * The WesRunService is just a shim over the graphql services, used to convert the responses from
 * them to wes standard objects. All logic regarding repository query, security etc. is handled in
 * those services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WesRunService {
  @NonNull private final ServiceInfoProperties serviceInfoProperties;
  @NonNull private final RunService runService;
  @NonNull private final TaskService taskService;

  public Mono<RunListResponse> listRuns(Integer pageSize, Integer pageToken) {
    val sizeToUse = pageSize == null ? ES_PAGE_DEFAULT_SIZE : pageSize;
    val from = pageToken == null ? 0 : sizeToUse * pageToken;
    val page = Map.of("from", from, "size", sizeToUse);
    return runService
        .searchRuns(null, page, List.of())
        .map(
            searchResult -> {
              val runStatuses =
                  searchResult.getContent().stream()
                      .map(
                          run ->
                              RunStatus.builder()
                                  .runId(run.getRunId())
                                  .state(fromValue(run.getState()))
                                  .build())
                      .collect(Collectors.toList());

              val nextPageToken =
                  calculateNextPageToken(
                      searchResult.getInfo().getTotalHits(), from, sizeToUse, pageToken);
              return RunListResponse.builder()
                  .runs(runStatuses)
                  .nextPageToken(nextPageToken)
                  .build();
            });
  }

  public Mono<RunStatus> getRunStatusById(@NonNull String runId) {
    return runService
        .getRunByRunId(runId)
        .map(
            run ->
                RunStatus.builder().runId(run.getRunId()).state(fromValue(run.getState())).build());
  }

  public Mono<RunResponse> getRunLog(@NonNull String runId) {
    return runService
        .getRunByRunId(runId)
        .map(
            run ->
                buildRunLog(
                    run,
                    serviceInfoProperties.getWorkflowTypeVersions().toString(),
                    serviceInfoProperties.getWorkflowType()))
        .flatMap(
            runLog ->
                taskService
                    .getTasks(runId)
                    .map(
                        tasks -> {
                          runLog.setTaskLogs(buildTaskLogsWithTasks(tasks));
                          return runLog;
                        }))
        .switchIfEmpty(
            Mono.error(new NotFoundException(format("Cannot find run log with id %s", runId))));
  }

  public Mono<ServiceInfo> getServiceInfo() {
    return runService
        .getAggregatedRunStateCounts()
        .map(
            systemStateCounts ->
                ServiceInfo.builder()
                    .authInstructionsUrl(serviceInfoProperties.getAuthInstructionsUrl())
                    .contactInfoUrl(serviceInfoProperties.getContactInfoUrl())
                    .supportedFilesystemProtocols(
                        serviceInfoProperties.getSupportedFilesystemProtocols())
                    .supportedWesVersions(serviceInfoProperties.getSupportedWesVersions())
                    .workflowEngineVersions(serviceInfoProperties.getWorkflowEngineVersions())
                    .systemStateCounts(systemStateCounts)
                    .workflowTypeVersions(serviceInfoProperties.getWorkflowTypeVersions())
                    .defaultWorkflowEngineParameters(
                        serviceInfoProperties.getDefaultWorkflowEngineParameters())
                    .build());
  }

  public Mono<RunId> run(RunRequest runRequest) {
    return runService.startRun(runRequest);
  }

  public Mono<RunId> cancel(String runId) {
    return runService.cancelRun(runId);
  }

  private Integer calculateNextPageToken(
      Long totalHits, Integer from, Integer pageSize, Integer currentPageToken) {
    // if remaining hits can fit inside a page of size pageSize, then nextPage token is current + 1
    if ((totalHits - from) / pageSize > 0) {
      return currentPageToken + 1;
    }
    return null;
  }

  private List<TaskLog> buildTaskLogsWithTasks(@NonNull List<Task> taskList) {
    return taskList.stream().map(Converter::taskDocumentToLog).collect(Collectors.toList());
  }
}
