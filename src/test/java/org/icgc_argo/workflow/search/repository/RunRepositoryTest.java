package org.icgc_argo.workflow.search.repository;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.icgc_argo.workflow.search.testcontainers.WorkflowElasticsearchContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class RunRepositoryTest {

  private static final String INDEX_NAME = "run-repo";

  @Container
  private static final ElasticsearchContainer elasticsearchContainer =
      new WorkflowElasticsearchContainer();

  private static RestHighLevelClient client;

  @BeforeAll
  static void setUp() {
    elasticsearchContainer.start();
    client =
        new RestHighLevelClient(
            RestClient.builder(HttpHost.create(elasticsearchContainer.getHttpHostAddress())));
  }

  @BeforeEach
  void testIsContainerRunning() {
    assertTrue(elasticsearchContainer.isRunning());
    recreateIndex();
  }

  @Test
  void testBasicSearch() {
    val result = searchForRepo("");
    assertTrue(10L == result.getHits().getTotalHits().value);
  }

  @AfterAll
  static void destroy() {
    elasticsearchContainer.stop();
  }

  @SneakyThrows
  private void recreateIndex() {
    if (doesIndexExist()) {
      client.indices().delete(new DeleteIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);

      CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);

      createIndexRequest.mapping(
          "{\n"
              + "  \"properties\": {\n"
              + "    \"repository\": {\n"
              + "      \"type\": \"text\",\n"
              + "      \"analyzer\": \"standard\"\n"
              + "    }\n"
              + "  }\n"
              + "}",
          XContentType.JSON);

      client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

      val repos =
          List.of(
              "https://github.com/icgc-argo-workflows/open-access-variant-filtering.git",
              "https://github.com/icgc-argo-workflows/open-access-variant-filtering",
              "https://www.github.com/icgc-argo-workflows/open-access-variant-filtering.git",
              "http://www.github.com/icgc-argo-workflows/open-access-variant-filtering",
              "https://www.github.com/icgc-argo-workflows/open-access-variant-filtering.git",
              "http://www.github.com/icgc-argo-workflows/open-access-variant-filtering",
              "www.github.com/icgc-argo-workflows/open-access-variant-filtering.git",
              "www.github.com/icgc-argo-workflows/open-access-variant-filtering",
              "github.com/icgc-argo-workflows/open-access-variant-filtering.git",
              "github.com/icgc-argo-workflows/open-access-variant-filtering");

      repos.forEach(this::indexRepo);
    }
  }

  @SneakyThrows
  private boolean doesIndexExist() {
    return client.indices().exists(new GetIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);
  }

  @SneakyThrows
  private IndexResponse indexRepo(String repo) {
    return client.index(
        new IndexRequest(INDEX_NAME).id(UUID.randomUUID().toString()).source("repository", repo),
        RequestOptions.DEFAULT);
  }

  @SneakyThrows
  private SearchResponse searchForRepo(String repo) {
    SearchRequest searchRequest = new SearchRequest();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.matchAllQuery());
    searchRequest.source(searchSourceBuilder);

    return client.search(searchRequest, RequestOptions.DEFAULT);
  }
}
