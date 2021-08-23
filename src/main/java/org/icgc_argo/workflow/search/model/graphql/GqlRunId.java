package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.NonNull;
import org.icgc_argo.workflow.search.model.common.RunId;

@JsonNaming()
@JsonIgnoreProperties(ignoreUnknown = true)
public class GqlRunId extends RunId {
  public GqlRunId(@NonNull String runId) {
    super(runId);
  }
}
