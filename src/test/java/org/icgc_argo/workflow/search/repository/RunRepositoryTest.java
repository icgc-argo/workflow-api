package org.icgc_argo.workflow.search.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
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
import org.elasticsearch.index.query.AbstractQueryBuilder;
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

@Testcontainers
public class RunRepositoryTest {
  /*
  !!!
  IMPORTANT: These tests only work because the text analyzer for the repository field
  was set to 'simple' instead of 'standard'. Given that the index mappings are managed
  externally of this repo it is important to ensure that the 'simple' analyzer is used
  for the repository field
  !!!
   */

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

  @SneakyThrows
  @BeforeEach
  void testIsContainerRunning() {
    assertTrue(elasticsearchContainer.isRunning());
    recreateIndex();
  }

  @Test
  void testMatchAll() {
    val result = searchForRepo(null);
    assertEquals(20L, result.getHits().getTotalHits().value);
  }

  @Test
  void testSearchAll() {
    val result = searchForRepo("icgc-argo-workflows");
    assertEquals(20L, result.getHits().getTotalHits().value);
  }

  @Test
  void testSearchOrgAndRepo() {
    val result = searchForRepo("icgc-argo-workflows/open-access-variant-filtering");
    assertEquals(10L, result.getHits().getTotalHits().value);
  }

  @Test
  void testSearchExact() {
    val result =
        searchForRepo(
            "https://www.github.com/icgc-argo-workflows/open-access-variant-filtering.git");
    assertEquals(2L, result.getHits().getTotalHits().value);
  }

  @AfterAll
  static void destroy() {
    elasticsearchContainer.stop();
  }

  @SneakyThrows
  private void recreateIndex() {

    if (doesIndexExist()) {
      client.indices().delete(new DeleteIndexRequest(INDEX_NAME), RequestOptions.DEFAULT);
    }

    CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);

    createIndexRequest.mapping(
        "{\n"
            + "  \"properties\": {\n"
            + "    \"repository\": {\n"
            + "      \"type\": \"text\",\n"
            + "      \"analyzer\": \"simple\"\n"
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
            "github.com/icgc-argo-workflows/open-access-variant-filtering",
            "https://github.com/icgc-argo-workflows/rna-seq-alignment.git",
            "https://github.com/icgc-argo-workflows/rna-seq-alignment",
            "https://www.github.com/icgc-argo-workflows/rna-seq-alignment.git",
            "http://www.github.com/icgc-argo-workflows/rna-seq-alignment",
            "https://www.github.com/icgc-argo-workflows/rna-seq-alignment.git",
            "http://www.github.com/icgc-argo-workflows/rna-seq-alignment",
            "www.github.com/icgc-argo-workflows/rna-seq-alignment.git",
            "www.github.com/icgc-argo-workflows/rna-seq-alignment",
            "github.com/icgc-argo-workflows/rna-seq-alignment.git",
            "github.com/icgc-argo-workflows/rna-seq-alignment");

    repos.forEach(this::indexRepo);

    // added this so that the documents are actually there in time for the tests
    Thread.sleep(1000);
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
  private SearchResponse searchForRepo(@Nullable String repo) {
    SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    AbstractQueryBuilder<?> query = QueryBuilders.matchAllQuery();

    if (Objects.nonNull(repo)) {
      query = RunRepository.repositoryQueryFunc(repo);
    }

    searchSourceBuilder.query(query);
    searchRequest.source(searchSourceBuilder);

    return client.search(searchRequest, RequestOptions.DEFAULT);
  }
}
