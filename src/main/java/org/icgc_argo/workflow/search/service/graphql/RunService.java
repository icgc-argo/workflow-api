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

package org.icgc_argo.workflow.search.service.graphql;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.icgc_argo.workflow.search.model.EsDefaults.ES_PAGE_DEFAULT_FROM;
import static org.icgc_argo.workflow.search.model.EsDefaults.ES_PAGE_DEFAULT_SIZE;
import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;
import static org.icgc_argo.workflow.search.util.WesUtils.generateWesRunId;

import com.pivotal.rabbitmq.source.Sender;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.common.RunId;
import org.icgc_argo.workflow.search.model.common.RunRequest;
import org.icgc_argo.workflow.search.model.graphql.*;
import org.icgc_argo.workflow.search.rabbitmq.SenderDTO;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RunService {

  private final RunRepository runRepository;
  private final Sender<SenderDTO> sender;

  @HasQueryAndMutationAccess
  public Mono<RunId> startRun(RunRequest runRequest) {
    val runId = generateWesRunId();
    return Mono.just(SenderDTO.builder().runId(runId).runRequest(runRequest).build())
        .flatMap(sender::send)
        .map(o -> new RunId(runId));
  }

  @HasQueryAndMutationAccess
  public Mono<RunId> cancelRun(String runId) {
    return getRunByRunId(runId)
        .map(run -> SenderDTO.builder().runId(runId).cancelRequest(true).build())
        .flatMap(sender::send)
        .map(o -> new RunId(runId))
        .switchIfEmpty(Mono.error(new Exception("Can't cancel non existing run.")));
  }

  @HasQueryAccess
  public Mono<SearchResult<GqlRun>> searchRuns(
      Map<String, Object> filter, Map<String, Integer> page, List<Sort> sorts, List<Range> ranges) {
    return runRepository
        .getRuns(filter, page, sorts, ranges)
        .map(SearchResponse::getHits)
        .map(
            responseSearchHits -> {
              val totalHits = responseSearchHits.getTotalHits().value;
              val from = page.getOrDefault("from", ES_PAGE_DEFAULT_FROM);
              val size = page.getOrDefault("size", ES_PAGE_DEFAULT_SIZE);

              val runs =
                  Arrays.stream(responseSearchHits.getHits())
                      .map(RunService::hitToRun)
                      .collect(toUnmodifiableList());
              val nextFrom = (totalHits - from) / size > 0;
              return new SearchResult<>(runs, nextFrom, totalHits);
            });
  }

  @HasQueryAccess
  public Mono<AggregationResult> aggregateRuns(Map<String, Object> filter) {
    return runRepository
        .getRuns(filter, Map.of(), emptyList(), emptyList())
        .map(SearchResponse::getHits)
        .map(
            responseSearchHits -> {
              val totalHits = responseSearchHits.getTotalHits().value;
              return new AggregationResult(totalHits);
            });
  }

  @HasQueryAccess
  public Mono<Map<String, Long>> getAggregatedRunStateCounts() {
    return runRepository.getAggregatedRunStateCounts();
  }

  @HasQueryAccess
  public Mono<List<GqlRun>> getRuns(Map<String, Object> filter, Map<String, Integer> page) {
    return runRepository
        .getRuns(filter, page)
        .map(response -> Arrays.stream(response.getHits().getHits()))
        .map(hitStream -> hitStream.map(RunService::hitToRun).collect(toUnmodifiableList()));
  }

  @HasQueryAccess
  public Mono<GqlRun> getRunByRunId(String runId) {
    return runRepository
        .getRuns(Map.of(RUN_ID, runId), null)
        .map(response -> response.getHits().getHits())
        .flatMapMany(Flux::fromArray)
        .next()
        .map(RunService::hitToRun);
  }

  private static GqlRun hitToRun(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return GqlRun.parse(sourceMap);
  }
}
