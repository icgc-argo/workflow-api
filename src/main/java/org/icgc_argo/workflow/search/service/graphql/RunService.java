/*
 *
 *  * Copyright (c) 2020 The Ontario Institute for Cancer Research. All rights reserved
 *  *
 *  * This program and the accompanying materials are made available under the terms of the GNU Affero General Public License v3.0.
 *  * You should have received a copy of the GNU Affero General Public License along with
 *  * this program. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 *  * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package org.icgc_argo.workflow.search.service.graphql;

import lombok.val;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.graphql.Run;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.icgc_argo.workflow.search.service.annotations.HasQueryAccess;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toUnmodifiableList;
import static org.icgc_argo.workflow.search.model.SearchFields.ANALYSIS_ID;
import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;

@Service
@HasQueryAccess
public class RunService {

  private final RunRepository runRepository;

  public RunService(RunRepository runRepository) {
    this.runRepository = runRepository;
  }

  private static Run hitToRun(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return Run.parse(sourceMap);
  }

  public List<Run> getRuns(Map<String, Object> filter, Map<String, Integer> page) {
    val response = runRepository.getRuns(filter, page);
    val hitStream = Arrays.stream(response.getHits().getHits());
    return hitStream.map(RunService::hitToRun).collect(toUnmodifiableList());
  }

  public Run getRunByRunId(String runId) {
    val response = runRepository.getRuns(Map.of(RUN_ID, runId), null);
    val runOpt = Arrays.stream(response.getHits().getHits()).map(RunService::hitToRun).findFirst();
    return runOpt.orElse(null);
  }

  public List<Run> getRunByAnalysisId(String analysisId) {
    val response = runRepository.getRuns(Map.of(ANALYSIS_ID, analysisId), null);
    val hitStream = Arrays.stream(response.getHits().getHits());
    return hitStream.map(RunService::hitToRun).collect(toUnmodifiableList());
  }
}
