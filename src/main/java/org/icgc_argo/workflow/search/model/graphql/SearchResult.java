package org.icgc_argo.workflow.search.model.graphql;

import lombok.Value;

import java.util.List;

@Value
public class SearchResult<T> {
  List<T> content;
  Info info;

  public SearchResult(List<T> content, Boolean hasNextFrom, Long totalHits) {
    this.content = content;
    this.info = new Info(hasNextFrom, totalHits, content.size());
  }

  @Value
  public static class Info {
    Boolean hasNextFrom;
    Long totalHits;
    Integer contentCount;
  }
}
