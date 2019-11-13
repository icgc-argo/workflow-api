package org.icgc_argo.workflow.search.service;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.icgc_argo.workflow.search.config.ElasticsearchProperties;
import org.icgc_argo.workflow.search.model.RunListResponse;
import org.icgc_argo.workflow.search.model.exceptions.NotFoundException;
import org.icgc_argo.workflow.search.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class RunService {

  private final RestHighLevelClient client;
  private final String workflowIndex;

  @Autowired
  public RunService(
      @NonNull RestHighLevelClient client,
      @NonNull ElasticsearchProperties elasticsearchProperties) {
    this.client = client;
    this.workflowIndex = elasticsearchProperties.getWorkflowIndex();
  }

  public RunListResponse listRuns() {
    try {
      SearchRequest searchRequest = new SearchRequest(workflowIndex);
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.query(QueryBuilders.matchAllQuery());
      searchRequest.source(searchSourceBuilder);

      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      val hits = searchResponse.getHits().getHits();

      return RunListResponse.builder()
          .runs(
              Stream.of(hits)
                  .map(Converter::convertSearchHitToRunStatus)
                  .collect(Collectors.toList()))
          .build();
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    } catch (NotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
