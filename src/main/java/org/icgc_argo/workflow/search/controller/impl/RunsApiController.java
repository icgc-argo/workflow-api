package org.icgc_argo.workflow.search.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.controller.RunsApi;
import org.icgc_argo.workflow.search.model.RunListResponse;
import org.icgc_argo.workflow.search.model.RunLog;
import org.icgc_argo.workflow.search.model.RunStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@Api(value = "WorkflowExecutionService", description = "the runs API", tags = "WorkflowExecutionService")
public class RunsApiController implements RunsApi {

  private final ObjectMapper objectMapper;
  private final HttpServletRequest request;

  @Autowired
  public RunsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    this.objectMapper = objectMapper;
    this.request = request;
  }

  public ResponseEntity<RunLog> getRunLog(
      @ApiParam(required = true) @PathVariable("run_id") String runId) {
    String accept = request.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
      try {
        return new ResponseEntity<>(
            objectMapper.readValue(
                "{  \"outputs\" : \"{}\",  \"request\" : {    \"workflow_engine_parameters\" : {      \"key\" : \"workflow_engine_parameters\"    },    \"workflow_url\" : \"workflow_url\",    \"workflow_params\" : \"{}\",    \"workflow_type\" : \"workflow_type\",    \"workflow_type_version\" : \"workflow_type_version\",    \"tags\" : {      \"key\" : \"tags\"    }  },  \"run_id\" : \"run_id\",  \"run_log\" : {    \"start_time\" : \"start_time\",    \"stdout\" : \"stdout\",    \"name\" : \"name\",    \"end_time\" : \"end_time\",    \"exit_code\" : 0,    \"cmd\" : [ \"cmd\", \"cmd\" ],    \"stderr\" : \"stderr\"  },  \"state\" : { },  \"task_logs\" : [ {    \"start_time\" : \"start_time\",    \"stdout\" : \"stdout\",    \"name\" : \"name\",    \"end_time\" : \"end_time\",    \"exit_code\" : 0,    \"cmd\" : [ \"cmd\", \"cmd\" ],    \"stderr\" : \"stderr\"  }, {    \"start_time\" : \"start_time\",    \"stdout\" : \"stdout\",    \"name\" : \"name\",    \"end_time\" : \"end_time\",    \"exit_code\" : 0,    \"cmd\" : [ \"cmd\", \"cmd\" ],    \"stderr\" : \"stderr\"  } ]}",
                RunLog.class),
            HttpStatus.NOT_IMPLEMENTED);
      } catch (IOException e) {
        log.error("Couldn't serialize response for content type application/json", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  public ResponseEntity<RunStatus> getRunStatus(
      @ApiParam(required = true) @PathVariable("run_id") String runId) {
    String accept = request.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
      try {
        return new ResponseEntity<>(
            objectMapper.readValue(
                "{  \"run_id\" : \"run_id\",  \"state\" : { }}", RunStatus.class),
            HttpStatus.NOT_IMPLEMENTED);
      } catch (IOException e) {
        log.error("Couldn't serialize response for content type application/json", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  public ResponseEntity<RunListResponse> listRuns(
      @ApiParam(
              example = "123",
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
    String accept = request.getHeader("Accept");
    if (accept != null && accept.contains("application/json")) {
      try {
        return new ResponseEntity<>(
            objectMapper.readValue(
                "{  \"next_page_token\" : \"next_page_token\",  \"runs\" : [ {    \"run_id\" : \"run_id\",    \"state\" : { }  }, {    \"run_id\" : \"run_id\",    \"state\" : { }  } ]}",
                RunListResponse.class),
            HttpStatus.NOT_IMPLEMENTED);
      } catch (IOException e) {
        log.error("Couldn't serialize response for content type application/json", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
