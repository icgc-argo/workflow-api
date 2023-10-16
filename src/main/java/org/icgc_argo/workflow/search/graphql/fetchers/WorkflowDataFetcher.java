package org.icgc_argo.workflow.search.graphql.fetchers;

import graphql.schema.DataFetcher;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.model.common.Run;
import org.icgc_argo.workflow.search.model.graphql.GqlRun;
import org.icgc_argo.workflow.search.model.graphql.Workflow;
import org.icgc_argo.workflow.search.repository.RunRepository;
import org.icgc_argo.workflow.search.service.graphql.EntityFetcherService;
import org.icgc_argo.workflow.search.service.graphql.RunService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WorkflowDataFetcher {

  public static final String WORKFLOW_ENTITY = "Workflow";

  /** Dependency */
  private final EntityFetcherService entityFetcherService;
  private final RunService runService;
  private final RunRepository runRepository;

  public WorkflowDataFetcher(EntityFetcherService entityFetcherService, RunRepository runRepository, RunService runService) {
    this.entityFetcherService = entityFetcherService;
    this.runService = runService;
    this.runRepository = runRepository;
  }

  public DataFetcher<List<Workflow>> getDataFetcher() {
    System.out.println("synchronous EntityDataFetchers --> getDataFetcher");
    return environment ->{
      val args = environment.getArguments();
      System.out.println("env args: "+args);
      List<Workflow> workflows = new ArrayList();

      List argMapList = (List)args.get("representations");
      Iterator itr = argMapList.iterator();
      while(itr.hasNext()){
        Map argMap = (Map)itr.next();
        if (WORKFLOW_ENTITY.equals(argMap.get("__typename"))){
          final Object runId = argMap.get("runId");
          if (runId instanceof String) {
            GqlRun gqlRun = entityFetcherService.getGqlRunByRunId((String)runId);
            workflows.add(new Workflow(runId.toString(), Run.builder().runId(gqlRun.getRunId()).state(gqlRun.getState()).repository(gqlRun.getRepository()).build()) );
            //workflows.add(new Workflow(runId.toString(), Run.builder().runId(runId.toString()).state("COMPLETE").repository("").build()) );
          }
        }
      }
      return workflows;
    };

  }


  /*private GqlRun getGqlRunByRunId(String runId){
   *//*SearchHit hit = runRepository
        .getGqlRuns(Map.of(RUN_ID, runId), null).getHits().getHits()[0];*//*
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
  }*/

}

