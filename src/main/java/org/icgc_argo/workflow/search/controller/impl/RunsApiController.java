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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.controller.RunsApi;
import org.icgc_argo.workflow.search.model.wes.RunListResponse;
import org.icgc_argo.workflow.search.model.wes.RunResponse;
import org.icgc_argo.workflow.search.model.wes.RunStatus;
import org.icgc_argo.workflow.search.model.wes.ServiceInfo;
import org.icgc_argo.workflow.search.service.wes.WesRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@Api(
    value = "WorkflowExecutionService",
    description = "the runs API",
    tags = "WorkflowExecutionService")
public class RunsApiController implements RunsApi {

  private WesRunService wesRunService;

  @Autowired
  public RunsApiController(WesRunService wesRunService) {
    this.wesRunService = wesRunService;
  }

  @GetMapping(value = "/runs/{run_id}")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Get detailed info about a workflow run",
            response = RunResponse.class)
      })
  public ResponseEntity<RunResponse> getRunLog(
      @ApiParam(required = true) @PathVariable("run_id") @NonNull String runId) {
    val response = wesRunService.getRunLog(runId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping(value = "/runs/{run_id}/status")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Get quick status info about a workflow run",
            response = RunStatus.class)
      })
  public ResponseEntity<RunStatus> getRunStatus(
      @ApiParam(required = true) @PathVariable("run_id") String runId) {

    val response = wesRunService.getRunStatusById(runId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping(value = "/runs")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "List Run Results", response = RunListResponse.class)
      })
  public ResponseEntity<RunListResponse> listRuns(
      @ApiParam(
              example = "10",
              value =
                  "OPTIONAL The preferred number of workflow runs to return in a page. If not provided, the implementation should use a default page size. The implementation must not return more items than `page_size`, but it may return fewer.  Clients should not assume that if fewer than `page_size` items are returned that all items have been returned.  The availability of additional pages is indicated by the value of `next_page_token` in the response.")
          @Valid
          @RequestParam(value = "page_size", defaultValue = "10", required = false)
          Integer pageSize,
      @ApiParam(
              example = "0",
              value =
                  "OPTIONAL Token to use to indicate where to start getting results. If unspecified, return the first page of results.")
          @Valid
          @RequestParam(value = "page_token", defaultValue = "0", required = false)
          Integer pageToken) {

    val response = wesRunService.listRuns(pageSize, pageToken);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping(value = "/service-info")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message = "Get information about workflow execution service.",
            response = ServiceInfo.class)
      })
  public ResponseEntity<ServiceInfo> getServiceInfo() {
    val response = wesRunService.getServiceInfo();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
