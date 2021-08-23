package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.icgc_argo.workflow.search.model.common.Task;

@JsonNaming()
@JsonIgnoreProperties(ignoreUnknown = true)
public class GqlTask extends Task {}
