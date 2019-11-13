package org.icgc_argo.workflow.search.util;

import static org.icgc_argo.workflow.search.model.SearchFields.RUN_ID;
import static org.icgc_argo.workflow.search.model.SearchFields.STATE;
import static org.icgc_argo.workflow.search.model.exceptions.NotFoundException.checkNotFound;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.SearchHit;
import org.icgc_argo.workflow.search.model.RunStatus;
import org.icgc_argo.workflow.search.model.State;

@Slf4j
@UtilityClass
public class Converter {

  private final String WORKFLOW_INDEX = "workflow";

  public static RunStatus convertSearchHitToRunStatus(@NonNull SearchHit hit) {

    checkNotFound(
        hit.getSourceAsMap() != null,
        String.format(
            "Cannot convert search hits to run status, no source found for index '%s'",
            WORKFLOW_INDEX));

    checkNotFound(
        hit.getSourceAsMap().get(RUN_ID) != null && hit.getSourceAsMap().get(STATE) != null,
        "Search hit has null values: runId, state.");

    return RunStatus.builder()
        .runId(hit.getSourceAsMap().get(RUN_ID).toString())
        .state(State.fromValue(hit.getSourceAsMap().get(STATE).toString()))
        .build();
  }
}
