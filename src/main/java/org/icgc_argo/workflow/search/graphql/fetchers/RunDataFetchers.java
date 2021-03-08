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

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.icgc_argo.workflow.search.util.JacksonUtils.convertValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.graphql.AsyncDataFetcher;
import org.icgc_argo.workflow.search.model.common.Run;
import org.icgc_argo.workflow.search.model.common.Task;
import org.icgc_argo.workflow.search.model.graphql.*;
import org.icgc_argo.workflow.search.model.graphql.SearchResult;
import org.icgc_argo.workflow.search.model.graphql.Sort;
import org.icgc_argo.workflow.search.service.graphql.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
  public AsyncDataFetcher<SearchResult<Run>> getRunsDataFetcher() {
    return environment -> {
      val args = environment.getArguments();

      val filter = ImmutableMap.<String, Object>builder();
      val page = ImmutableMap.<String, Integer>builder();
      val sorts = ImmutableList.<Sort>builder();

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
      }
      return runService.searchRuns(filter.build(), page.build(), sorts.build());
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

  public AsyncDataFetcher<Run> getNestedRunDataFetcher() {
    return environment -> {
      val task = (Task) environment.getSource();
      return runService.getRunByRunId(task.getRunId());
    };
  }
}
