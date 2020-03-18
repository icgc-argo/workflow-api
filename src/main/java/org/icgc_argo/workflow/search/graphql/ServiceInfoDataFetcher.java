package org.icgc_argo.workflow.search.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.icgc_argo.workflow.search.model.ServiceInfo;
import org.icgc_argo.workflow.search.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServiceInfoDataFetcher implements DataFetcher<ServiceInfo> {

  private RunService runService;

  @Autowired
  public ServiceInfoDataFetcher(@NonNull RunService runService) {
    this.runService = runService;
  }

  @Override
  public ServiceInfo get(DataFetchingEnvironment environment) throws Exception {
    return runService.getServiceInfo();
  }
}
