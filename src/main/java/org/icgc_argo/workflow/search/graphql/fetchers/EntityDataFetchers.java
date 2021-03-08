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
import static org.icgc_argo.workflow.search.model.SearchFields.ANALYSIS_ID;
import static org.icgc_argo.workflow.search.util.Converter.asImmutableMap;

import com.apollographql.federation.graphqljava._Entity;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.graphql.AsyncDataFetcher;
import org.icgc_argo.workflow.search.model.common.Run;
import org.icgc_argo.workflow.search.model.graphql.Analysis;
import org.icgc_argo.workflow.search.model.graphql.Workflow;
import org.icgc_argo.workflow.search.service.graphql.RunService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class EntityDataFetchers {

  public static final String RUN_ENTITY = "Run";
  public static final String ANALYSIS_ENTITY = "Analysis";
  public static final String WORKFLOW_ENTITY = "Workflow";

  /** Dependency */
  private final RunService runService;

  public EntityDataFetchers(RunService runService) {
    this.runService = runService;
  }

  public AsyncDataFetcher getDataFetcher() {
    return environment ->
        Flux.fromStream(
                environment.<List<Map<String, Object>>>getArgument(_Entity.argumentName).stream())
            .flatMap(
                values -> {
                  if (RUN_ENTITY.equals(values.get("__typename"))) {
                    final Object runId = values.get("runId");
                    if (runId instanceof String) {
                      return runService.getRunByRunId((String) runId);
                    }
                  }
                  if (ANALYSIS_ENTITY.equals(values.get("__typename"))) {
                    final Object analysisId = values.get("analysisId");
                    if (analysisId instanceof String) {
                      return Mono.just(
                          new Analysis(
                              (String) analysisId, inputForRunResolver((String) analysisId)));
                    }
                  }
                  if (WORKFLOW_ENTITY.equals(values.get("__typename"))) {
                    final Object runId = values.get("runId");
                    if (runId instanceof String) {
                      return runService
                          .getRunByRunId((String) runId)
                          .map(run -> new Workflow(run.getRunId(), run));
                    }
                  }
                  return Mono.empty();
                })
            .collectList();
  }

  private AsyncDataFetcher<List<Run>> inputForRunResolver(String analysisId) {
    return environment -> {
      ImmutableMap<String, Object> filter = asImmutableMap(environment.getArgument("filter"));
      val filerAnalysisId = filter.getOrDefault(ANALYSIS_ID, analysisId);

      // short circuit here since can't find runs for invalid analysisId
      if (isNullOrEmpty(analysisId) || !analysisId.equals(filerAnalysisId)) {
        return Mono.empty();
      }

      Map<String, Object> mergedFilter = new HashMap<>(filter);
      mergedFilter.put(ANALYSIS_ID, analysisId);

      return runService.getRuns(mergedFilter, null);
    };
  }
}
