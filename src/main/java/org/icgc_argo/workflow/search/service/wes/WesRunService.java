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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.icgc_argo.workflow.search.config.ElasticsearchProperties;
import org.icgc_argo.workflow.search.config.ServiceInfoProperties;
import org.icgc_argo.workflow.search.index.model.TaskDocument;
import org.icgc_argo.workflow.search.index.model.WorkflowDocument;
import org.icgc_argo.workflow.search.model.exceptions.NotFoundException;
import org.icgc_argo.workflow.search.model.wes.*;
import org.icgc_argo.workflow.search.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.icgc_argo.workflow.search.model.SearchFields.*;
import static org.icgc_argo.workflow.search.util.Converter.buildRunLog;
import static org.icgc_argo.workflow.search.util.Converter.convertSourceMapToRunStatus;

@Slf4j
@Service
public class WesRunService {

  private final RestHighLevelClient client;
  private final String workflowIndex;
  private final String taskIndex;
  private final String userName;
  private final String password;
  private final boolean useAuthentication;
  private final int DEFAULT_HIT_SIZE = 100;
  private ServiceInfoProperties serviceInfoProperties;

  @Autowired
  public WesRunService(
      @NonNull RestHighLevelClient client,
      @NonNull ElasticsearchProperties elasticsearchProperties,
      @NonNull ServiceInfoProperties serviceInfoProperties) {
    this.client = client;
    this.serviceInfoProperties = serviceInfoProperties;
    this.workflowIndex = elasticsearchProperties.getWorkflowIndex();
    this.taskIndex = elasticsearchProperties.getTaskIndex();
    this.userName = elasticsearchProperties.getUsername();
    this.password = elasticsearchProperties.getPassword();
    this.useAuthentication = elasticsearchProperties.getUseAuthentication();
  }

  public RunListResponse listRuns() {
    val hits = getSearchHits(workflowIndex);
    return RunListResponse.builder()
        .runs(
            Stream.of(hits)
                .map(hit -> convertSourceMapToRunStatus(hit.getSourceAsMap()))
                .collect(Collectors.toList()))
        .build();
  }

  public RunStatus getRunStatusById(@NonNull String runId) {
    val map = getWorkflowAsMapById(runId);
    return convertSourceMapToRunStatus(map);
  }

  public RunResponse getRunLog(@NonNull String runId) {
    val workflowDoc =
        getWorkflowDocumentById(runId)
            .orElseThrow(
                () -> new NotFoundException(format("Cannot find run log with id %s", runId)));

    val runLog =
        buildRunLog(
            workflowDoc,
            serviceInfoProperties.getWorkflowTypeVersions().toString(),
            serviceInfoProperties.getWorkflowType());

    runLog.setTaskLogs(buildTaskLogs(getTaskDocumentListById(runId).get()));
    return runLog;
  }

  public ServiceInfo getServiceInfo() {
    return ServiceInfo.builder()
        .authInstructionsUrl(serviceInfoProperties.getAuthInstructionsUrl())
        .contactInfoUrl(serviceInfoProperties.getContactInfoUrl())
        .supportedFilesystemProtocols(serviceInfoProperties.getSupportedFilesystemProtocols())
        .supportedWesVersions(serviceInfoProperties.getSupportedWesVersions())
        .workflowEngineVersions(serviceInfoProperties.getWorkflowEngineVersions())
        .systemStateCounts(systemStateCounts())
        .workflowTypeVersions(serviceInfoProperties.getWorkflowTypeVersions())
        .defaultWorkflowEngineParameters(serviceInfoProperties.getDefaultWorkflowEngineParameters())
        .build();
  }

  private SearchResponse search(@NonNull SearchSourceBuilder builder, @NonNull String index) {
    try {
      SearchRequest searchRequest = new SearchRequest(index);
      searchRequest.source(builder);
      return client.search(searchRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private SearchHit[] getSearchHits(@NonNull String index) {
    try {
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.sort(START_TIME, SortOrder.DESC);
      searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(DEFAULT_HIT_SIZE);
      val searchResponse = search(searchSourceBuilder, index);
      val hits = searchResponse.getHits().getHits();

      NotFoundException.checkNotFound(
          hits != null && hits.length >= 0, format("Cannot find run log."));

      return hits;
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  public SearchHit getWorkflowById(@NonNull String runId) {
    try {
      val searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.query(QueryBuilders.termQuery(RUN_ID, runId)).size(DEFAULT_HIT_SIZE);
      val searchResponse = search(searchSourceBuilder, workflowIndex);
      val hits = searchResponse.getHits().getHits();

      NotFoundException.checkNotFound(
          hits != null && hits.length > 0, format("Cannot find run log with run id = %s", runId));

      return hits[0];
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  private Map<String, Object> getWorkflowAsMapById(@NonNull String runId) {
    val hit = getWorkflowById(runId);
    val search = hit.getSourceAsMap();
    return search;
  }

  private String getWorkflowByIdAsJson(@NonNull String runId) {
    val hit = getWorkflowById(runId);
    val search = hit.getSourceAsString();
    return search;
  }

  public Optional<WorkflowDocument> getWorkflowDocumentById(@NonNull String runId) {
    try {
      val search = getWorkflowByIdAsJson(runId);

      val customMapper =
          new ObjectMapper()
              .registerModule(new JavaTimeModule())
              .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
              .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      val doc = customMapper.readValue(search, WorkflowDocument.class);

      return Optional.of(doc);
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private List<TaskLog> buildTaskLogs(@NonNull List<TaskDocument> taskList) {
    return taskList.stream().map(Converter::taskDocumentToLog).collect(Collectors.toList());
  }

  private Optional<List<TaskDocument>> getTaskDocumentListById(@NonNull String runId) {
    val searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.termQuery(RUN_ID, runId)).size(DEFAULT_HIT_SIZE);
    val searchResponse = search(searchSourceBuilder, taskIndex);
    val hits = searchResponse.getHits().getHits();

    NotFoundException.checkNotFound(
        hits != null && hits.length > 0, format("Cannot find run log with run name = %s", runId));
    // convert hits to TaskDocument list
    List<TaskDocument> taskLogs =
        Stream.of(hits).map(Converter::convertSearchHitToTaskDocument).collect(Collectors.toList());
    return Optional.of(taskLogs);
  }

  /**
   * The system statistics, key is the statistic, value is the count of runs in that state. See the
   * State enum for the possible keys.
   *
   * @return Map of system statistics
   */
  private Map<String, Long> systemStateCounts() {
    SearchRequest searchRequest = new SearchRequest(workflowIndex);

    val matchQueryBuilder = QueryBuilders.matchAllQuery();
    val sourceBuilder = new SearchSourceBuilder();

    AggregationBuilder aggregation = AggregationBuilders.terms("states").field(STATE);

    sourceBuilder.aggregation(aggregation);
    sourceBuilder.query(matchQueryBuilder);
    searchRequest.source(sourceBuilder);

    try {
      val searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      Terms states = searchResponse.getAggregations().get("states");
      return states.getBuckets().stream()
          .collect(
              (Collectors.toMap(
                  MultiBucketsAggregation.Bucket::getKeyAsString,
                  MultiBucketsAggregation.Bucket::getDocCount)));
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
