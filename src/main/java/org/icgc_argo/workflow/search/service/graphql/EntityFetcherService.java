package org.icgc_argo.workflow.search.service.graphql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.graphql.GqlRun;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityFetcherService {
  private final RunRepository runRepository;

  public GqlRun getGqlRunByRunId(String runId){
    /*SearchHit hit = runRepository

        .getGqlRuns(Map.of(RUN_ID, runId), null).getHits().getHits()[0];*/
    SearchResponse searchResponse = runRepository
        .getGqlRuns(Map.of(RUN_ID, runId), null);
    System.out.println("search Hits length "+searchResponse.getHits().getHits().length);
    SearchHit[] sh = searchResponse.getHits().getHits();

    return hitToRun(sh[0]);
    //return GqlRun.builder().runId(runId).repository("").state("COMPLETE").build();
  }

  private static GqlRun hitToRun(SearchHit hit) {
    val sourceMap = hit.getSourceAsMap();
    return GqlRun.parse(sourceMap);
  }
}

