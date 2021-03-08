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

package org.icgc_argo.workflow.search.controller.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.controller.WesApi;
import org.icgc_argo.workflow.search.model.wes.RunListResponse;
import org.icgc_argo.workflow.search.model.wes.RunResponse;
import org.icgc_argo.workflow.search.model.wes.RunStatus;
import org.icgc_argo.workflow.search.model.wes.ServiceInfo;
import org.icgc_argo.workflow.search.service.wes.WesApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WesApiController implements WesApi {

  private final WesApiService wesApiService;

  @GetMapping(value = "/runs/{run_id}")
  public Mono<ResponseEntity<RunResponse>> getRunLog(@NonNull String runId) {
    return wesApiService
        .getRunLog(runId)
        .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
  }

  @GetMapping(value = "/runs/{run_id}/status")
  public Mono<ResponseEntity<RunStatus>> getRunStatus(@NonNull String runId) {
    return wesApiService
        .getRunStatusById(runId)
        .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
  }

  @GetMapping(value = "/runs")
  public Mono<ResponseEntity<RunListResponse>> listRuns(Integer pageSize, Integer pageToken) {
    return wesApiService
        .listRuns(pageSize, pageToken)
        .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
  }

  @GetMapping(value = "/service-info")
  public Mono<ResponseEntity<ServiceInfo>> getServiceInfo() {
    return wesApiService
        .getServiceInfo()
        .map(response -> new ResponseEntity<>(response, HttpStatus.OK));
  }
}
