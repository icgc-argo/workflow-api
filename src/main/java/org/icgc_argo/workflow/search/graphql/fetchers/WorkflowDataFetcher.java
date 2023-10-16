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
import org.icgc_argo.workflow.search.util.JacksonUtils;
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
    return environment ->{
      val args = environment.getArguments();
      log.debug("env args: {}",args);
      List<Workflow> workflows = new ArrayList();

      List argMapList = (List)args.get("representations");
      Iterator itr = argMapList.iterator();
      while(itr.hasNext()){
        Map argMap = (Map)itr.next();
        if (WORKFLOW_ENTITY.equals(argMap.get("__typename"))){
          final Object runId = argMap.get("runId");
          if (runId instanceof String) {
            GqlRun gqlRun = entityFetcherService.getGqlRunByRunId((String)runId);
            //Run run = JacksonUtils.convertValue(gqlRun,Run.class);
            Run run = Run.builder()
                  .runId(gqlRun.getRunId())
                  .sessionId(gqlRun.getSessionId())
                  .repository(gqlRun.getRepository())
                  .state(gqlRun.getState())
                  .parameters(gqlRun.getParameters())
                  .startTime(gqlRun.getStartTime())
                  .completeTime(gqlRun.getCompleteTime())
                  .success(gqlRun.getSuccess())
                  .exitStatus(gqlRun.getExitStatus())
                  .duration(gqlRun.getDuration())
                  .commandLine(gqlRun.getCommandLine())
                  .engineParameters(gqlRun.getEngineParameters())
                  .build();

            workflows.add(new Workflow(runId.toString(), run));
            //workflows.add(new Workflow(runId.toString(), Run.builder().runId(gqlRun.getRunId()).state(gqlRun.getState()).repository(gqlRun.getRepository()).build()) );
          }
        }
      }
      return workflows;
    };

  }

}

