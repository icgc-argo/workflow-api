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
          @RequestParam(value = "page_size", required = false)
          Long pageSize,
      @ApiParam(
              value =
                  "OPTIONAL Token to use to indicate where to start getting results. If unspecified, return the first page of results.")
          @Valid
          @RequestParam(value = "page_token", required = false)
          String pageToken) {

    val response = wesRunService.listRuns();
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
