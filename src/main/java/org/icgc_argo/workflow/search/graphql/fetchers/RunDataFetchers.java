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

package org.icgc_argo.workflow.search.graphql.fetchers;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.icgc_argo.workflow.search.model.SearchFields.ANALYSIS_ID;
import static org.icgc_argo.workflow.search.util.Converter.asImmutableMap;
import static org.icgc_argo.workflow.search.util.JacksonUtils.convertValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.graphql.AsyncDataFetcher;
import org.icgc_argo.workflow.search.model.graphql.*;
import org.icgc_argo.workflow.search.service.graphql.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RunDataFetchers {

  /** Dependency */
  private final RunService runService;

  @Autowired
  public RunDataFetchers(RunService runService) {
    this.runService = runService;
  }

  @SuppressWarnings("unchecked")
  public AsyncDataFetcher<SearchResult<GqlRun>> getRunsDataFetcher() {
    return environment -> {
      val args = environment.getArguments();

      val filter = ImmutableMap.<String, Object>builder();
      val page = ImmutableMap.<String, Integer>builder();
      val sorts = ImmutableList.<Sort>builder();
      val ranges = ImmutableList.<Range>builder();

      if (args != null) {
        if (args.get("filter") != null) filter.putAll((Map<String, Object>) args.get("filter"));
        if (args.get("page") != null) page.putAll((Map<String, Integer>) args.get("page"));
        if (args.get("sorts") != null) {
          val rawSorts = (List<Object>) args.get("sorts");
          sorts.addAll(
              rawSorts.stream()
                  .map(sort -> convertValue(sort, Sort.class))
                  .collect(toUnmodifiableList()));
        }
        if (args.get("ranges") != null) {
          val rawRanges = (List<Object>) args.get("ranges");
          ranges.addAll(
                  rawRanges.stream()
                          .map(sort -> convertValue(sort, Range.class))
                          .collect(toUnmodifiableList()));
        }
      }

      return runService.searchRuns(filter.build(), page.build(), sorts.build(), ranges.build());
    };
  }

  @SuppressWarnings("unchecked")
  public AsyncDataFetcher<AggregationResult> getAggregateRunsDataFetcher() {
    return environment -> {
      val args = environment.getArguments();

      val filter = ImmutableMap.<String, Object>builder();

      if (args != null) {
        if (args.get("filter") != null) filter.putAll((Map<String, Object>) args.get("filter"));
      }
      return runService.aggregateRuns(filter.build());
    };
  }

  public AsyncDataFetcher<GqlRun> getNestedRunInTaskDataFetcher() {
    return environment -> {
      val task = (GqlTask) environment.getSource();
      return runService.getRunByRunId(task.getRunId());
    };
  }

  public AsyncDataFetcher<List<GqlRun>> getNestedRunInAnalysisDataFetcher() {
    return environment -> {
      val analysis = (Analysis) environment.getSource();
      val analysisId = analysis.getAnalysisId();

      ImmutableMap<String, Object> filter = asImmutableMap(environment.getArgument("filter"));
      val filerAnalysisId = filter.getOrDefault(ANALYSIS_ID, analysisId);

      // short circuit here since can't find runs for invalid analysisId
      if (isNullOrEmpty(analysisId) || !analysisId.equals(filerAnalysisId)) {
        return Mono.empty();
      }

      Map<String, Object> mergedFilter = new HashMap<>(filter);
      mergedFilter.put(ANALYSIS_ID, analysisId);

      // Need to cast to get appropriate jackson annotation (camelCase property naming)
      return runService.getRuns(mergedFilter, null);
    };
  }
}
