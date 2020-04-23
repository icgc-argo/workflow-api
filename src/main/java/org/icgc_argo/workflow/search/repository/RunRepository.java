package org.icgc_argo.workflow.search.repository;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.icgc_argo.workflow.search.model.SearchFields.*;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.icgc_argo.workflow.search.config.ElasticsearchProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunRepository {

  private static final Map<String, Function<String, AbstractQueryBuilder<?>>> QUERY_RESOLVER =
      argumentPathMap();

  private final RestHighLevelClient client;
  private final String workflowIndex;

  @Autowired
  public RunRepository(
      @NonNull RestHighLevelClient client,
      @NonNull ElasticsearchProperties elasticsearchProperties) {
    this.client = client;
    this.workflowIndex = elasticsearchProperties.getWorkflowIndex();
  }

  public SearchResponse getRuns(Map<String, Object> filter, Map<String, Integer> page) {
    final AbstractQueryBuilder<?> query =
        (filter == null || filter.size() == 0) ? matchAllQuery() : queryFromArgs(filter);

    val searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.sort(START_TIME, SortOrder.DESC);
    searchSourceBuilder.query(query);

    if (page != null && page.size() != 0) {
      searchSourceBuilder.size(page.get("size"));
      searchSourceBuilder.from(page.get("from"));
    }

    return execute(searchSourceBuilder);
  }

  @SneakyThrows
  private SearchResponse execute(@NonNull SearchSourceBuilder builder) {
    val searchRequest = new SearchRequest(workflowIndex);
    searchRequest.source(builder);
    return client.search(searchRequest, RequestOptions.DEFAULT);
  }

  /**
   * For each argument, find its query producer function and apply the argument value ANDing it in a
   * bool query
   *
   * @param args Argument Map from GraphQL
   * @return Elasticsearch Bool Query containing ANDed (MUSTed) term queries
   */
  private static BoolQueryBuilder queryFromArgs(Map<String, Object> args) {
    val bool = QueryBuilders.boolQuery();
    args.forEach((key, value) -> bool.must(QUERY_RESOLVER.get(key).apply(value.toString())));
    return bool;
  }

  private static Map<String, Function<String, AbstractQueryBuilder<?>>> argumentPathMap() {
    return ImmutableMap.<String, Function<String, AbstractQueryBuilder<?>>>builder()
        .put(RUN_ID, value -> new TermQueryBuilder("runId", value))
        .put(RUN_NAME, value -> new TermQueryBuilder("runName", value))
        .put(STATE, value -> new TermQueryBuilder("state", value))
        .build();
  }
}